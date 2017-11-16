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

/**
 * A {@link Record} implementation that contains a Memo record.
 */
public class MemoRecord extends AbstractRecord {

    private String memo;

    /**
     * Creates a new {@link MemoRecord}.
     *
     * @param attribute
     *            Record attribute
     */
    public MemoRecord(int attribute) {
        super(attribute);
    }

    /**
     * Gets the memo message.
     */
    public String getMemo()                     { return memo; }
    public void setMemo(String memo)            { this.memo = memo; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memo:[").append(memo).append(']');
        return sb.toString();
    }

}
