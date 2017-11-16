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
import org.shredzone.commons.pdb.appinfo.RawAppInfo;
import org.shredzone.commons.pdb.record.RawRecord;

/**
 * An {@link Converter} that handles only the raw content of a record.
 */
public class RawConverter implements Converter<RawRecord, RawAppInfo> {

    @Override
    public boolean isAcceptable(PdbDatabase<RawRecord, RawAppInfo> database) {
        // Raw accepts everything
        return true;
    }

    @Override
    public RawRecord convert(PdbFile reader, int record, int size, int attribute,
            PdbDatabase<RawRecord, RawAppInfo> database) throws IOException {
        byte[] data = new byte[size];
        reader.readFully(data);
        return new RawRecord(data, attribute);
    }

    @Override
    public RawAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<RawRecord, RawAppInfo> database) throws IOException {
        byte[] data = new byte[size];
        reader.readFully(data);
        return new RawAppInfo(data);
    }

}
