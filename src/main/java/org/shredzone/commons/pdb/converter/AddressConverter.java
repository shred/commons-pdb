/*
 * Shredzone Commons - pdb
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.commons.pdb.converter;

import java.io.IOException;

import org.shredzone.commons.pdb.PdbDatabase;
import org.shredzone.commons.pdb.PdbFile;
import org.shredzone.commons.pdb.appinfo.AddressAppInfo;
import org.shredzone.commons.pdb.record.AddressRecord;

/**
 * A {@link Converter} that reads Address records.
 *
 * @see <a href="http://search.cpan.org/~bdfoy/p5-Palm-1.011/lib/Address.pm">Palm::Address</a>
 */
public class AddressConverter implements Converter<AddressRecord, AddressAppInfo> {

    private static final String[] COUNTRIES = { "AU", "AT", "BE", "BR", "CA",
            "DK", "FI", "FR", "DE", "HK", "IS", "IE", "IT", "JP", "LU", "MX",
            "NL", "NZ", "NO", "ES", "SE", "CH", "GB", "US", };

    private static final AddressRecord.Label[] PHONE_LABELS = {
        AddressRecord.Label.PHONE1, AddressRecord.Label.PHONE2,
        AddressRecord.Label.PHONE3, AddressRecord.Label.PHONE4,
        AddressRecord.Label.PHONE5, AddressRecord.Label.PHONE6,
        AddressRecord.Label.PHONE7, AddressRecord.Label.PHONE8,
    };

    private static final int LABEL_LENGTH = 16;

    @Override
    public boolean isAcceptable(
            PdbDatabase<AddressRecord, AddressAppInfo> database) {
        return "AddressDB".equals(database.getName())
               && "addr".equals(database.getCreator());
    }

    @Override
    public AddressRecord convert(PdbFile reader, int record, int size, int attribute,
            PdbDatabase<AddressRecord, AddressAppInfo> database)
            throws IOException {

        AddressRecord result = new AddressRecord(attribute);
        if (result.isDelete()) {
            return null;
        }

        int phoneFlags = reader.readInt();
        int fieldMap = reader.readInt();
        reader.readByte();

        AddressRecord.Field[] fields = AddressRecord.Field.values();
        AddressRecord.Label[] labels = AddressRecord.Label.values();
        for (int ix = 0; ix < fields.length; ix++) {
            if ((fieldMap & (1 << ix)) != 0) {
                result.setLabel(fields[ix], mapLabel(labels[ix], phoneFlags));
                result.setField(fields[ix], reader.readTerminatedString());
            }
        }

        result.setDisplayPhone((phoneFlags >> 20) & 0x0F);

        return result;
    }

    /**
     * Maps a phone label to the one selected in the phone flags.
     *
     * @param label
     *            Label to be mapped
     * @param flags
     *            Phone flags
     * @return Mapped label
     * @throws IOException
     *             if the flags were not readable
     */
    private AddressRecord.Label mapLabel(AddressRecord.Label label, int flags)
    throws IOException {
        try {
            switch (label) {
                case PHONE1: return PHONE_LABELS[flags & 0x0F];
                case PHONE2: return PHONE_LABELS[(flags >> 4) & 0x0F];
                case PHONE3: return PHONE_LABELS[(flags >> 8) & 0x0F];
                case PHONE4: return PHONE_LABELS[(flags >> 12) & 0x0F];
                case PHONE5: return PHONE_LABELS[(flags >> 16) & 0x0F];
                default: return label;
            }
        } catch (ArrayIndexOutOfBoundsException e) { //NOSONAR
            throw new IOException("unknown mapping for label " + label);
        }
    }

    @Override
    public AddressAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<AddressRecord, AddressAppInfo> database)
            throws IOException {
        AddressAppInfo result = new AddressAppInfo();
        reader.readCategories(result);

        reader.readShort();
        reader.readInt();

        for (AddressRecord.Label label : AddressRecord.Label.values()) {
            result.setLabel(label, reader.readTerminatedFixedString(LABEL_LENGTH));
        }

        int country = reader.readByte();
        if (country >= 0 && country < COUNTRIES.length) {
            result.setCountry(COUNTRIES[country]);
        }

        reader.readByte();

        return result;
    }

}
