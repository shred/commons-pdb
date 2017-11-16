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
import org.shredzone.commons.pdb.appinfo.AppInfo;
import org.shredzone.commons.pdb.record.Record;

/**
 * Converts a PDB record into an {@link Record} object.
 */
public interface Converter <T extends Record, U extends AppInfo> {

    /**
     * Checks if this record converter is able to convert records for the PdbDatabase.
     *
     * @param database
     *            The {@link PdbDatabase} that is currently generated. Note that the
     *            database is still being read. The basic attributes are already read and
     *            may be used, but the appinfo and records are still incomplete. You would
     *            usually access the database to read the database name or creator.
     * @return {@code true} if the converter is able to process the database records.
     */
    boolean isAcceptable(PdbDatabase<T, U> database);

    /**
     * Converts raw record data to a {@link Record} object.
     *
     * @param reader
     *            {@link PdbFile} with the file cursor at the beginning of the record
     * @param record
     *            Record number that is currently read
     * @param size
     *            Size of this record, in bytes
     * @param attribute
     *            Attributes of this record (unsigned byte)
     * @param database
     *            The {@link PdbDatabase} that is currently generated. Note that the
     *            database is still being read. The attributes and appinfo are already
     *            read and may be used, but the records are still incomplete. You would
     *            usually access the database to read the database name or the category
     *            map.
     * @return {@link Record} object containing the data of this record, or {@code null}
     *         if the raw data did not result in a record (e.g. because it was deleted).
     */
    T convert(PdbFile reader, int record, int size, int attribute, PdbDatabase<T, U> database)
        throws IOException;

    /**
     * Converts raw application info data to an {@link AppInfo}.
     *
     * @param reader
     *            {@link PdbFile} with the file cursor at the beginning of the appinfo
     *            area
     * @param size
     *            Estimated size of the appinfo area, in bytes. Note that the actual
     *            appinfo area might be smaller.
     * @param database
     *            The {@link PdbDatabase} that is currently generated. Note that the
     *            database is still being read. The simple attributes are already read
     *            and may be used, but the appinfo and records are still empty. Usually
     *            you would like to ignore this parameter if possible.
     * @return {@link AppInfo} object containing the converted appinfo
     */
    U convertAppInfo(PdbFile reader, int size, PdbDatabase<T, U> database)
        throws IOException;

}
