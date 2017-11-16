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
package org.shredzone.commons.pdb.appinfo;

import java.util.EnumMap;

import org.shredzone.commons.pdb.record.AddressRecord;

/**
 * A {@link CategoryAppInfo} with more details about the {@link AddressRecord}.
 */
public class AddressAppInfo extends CategoryAppInfo {

    private EnumMap<AddressRecord.Label, String> labels = new EnumMap<>(AddressRecord.Label.class);

    private String country;

    /**
     * Gets the label text for a label.
     *
     * @param field
     *            Label to get the label text from
     * @return Label text of that label
     */
    public String getLabel(AddressRecord.Label field) { return labels.get(field); }
    public void setLabel(AddressRecord.Label field, String label) { labels.put(field, label); }

    /**
     * Gets the country code (as ISO 3166-1-alpha-2 code). {@code null} if the appinfo
     * country code was unknown.
     *
     * @see <a href="http://www.iso.org/iso/country_codes/iso_3166_code_lists/country_names_and_code_elements.htm">ISO 3166 Code Lists</a>
     */
    public String getCountry()                  { return country; }
    public void setCountry(String country)      { this.country = country; }

}
