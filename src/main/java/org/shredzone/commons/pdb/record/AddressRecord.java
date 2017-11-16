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
package org.shredzone.commons.pdb.record;

import java.util.EnumMap;

/**
 * A {@link Record} implementation that contains an Address record.
 */
public class AddressRecord extends AbstractRecord {

    private EnumMap<Field, Label> labels = new EnumMap<>(Field.class);
    private EnumMap<Field, String> fields = new EnumMap<>(Field.class);
    private int displayPhone;

    public AddressRecord(int attribute) {
        super(attribute);
    }

    /**
     * Gets the value of a field. May be {@code null} when that field was not
     * set.
     *
     * @param field
     *            Field name
     * @return value
     */
    public String getField(Field field)                { return fields.get(field); }
    public void setField(Field field, String value)    { fields.put(field, value); }

    /**
     * Gets the label of a field. May be {@code null} when that field was not
     * set.
     *
     * @param field
     *            Field name
     * @return Label of that field
     */
    public Label getLabel(Field field)                { return labels.get(field); }
    public void setLabel(Field field, Label label)    { labels.put(field, label); }

    /**
     * Gets the phone index to be displayed.
     */
    public int getDisplayPhone()                      { return displayPhone; }
    public void setDisplayPhone(int displayPhone )    { this.displayPhone = displayPhone; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:[").append("display=").append(displayPhone);

        for (Field field : Field.values()) {
            String value = getField(field);
            if (value != null) {
                sb.append(' ').append(getLabel(field)).append("='").append(value).append('\'');
            }
        }

        sb.append(']');
        return sb.toString();
    }


    /**
     * Available fields of an address.
     */
    public enum Field {
        NAME, FIRST_NAME, COMPANY, PHONE1, PHONE2, PHONE3, PHONE4, PHONE5,
        ADDRESS, CITY, STATE, ZIP, COUNTRY, TITLE, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4,
        NOTE,
    }

    /**
     * Available labels of an address.
     */
    public enum Label {
        NAME, FIRST_NAME, COMPANY, PHONE1, PHONE2, PHONE3, PHONE4, PHONE5,
        ADDRESS, CITY, STATE, ZIP, COUNTRY, TITLE, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4,
        NOTE, PHONE6, PHONE7, PHONE8,
    }

}
