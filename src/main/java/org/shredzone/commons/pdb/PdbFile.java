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
package org.shredzone.commons.pdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

import org.shredzone.commons.pdb.appinfo.AppInfo;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.commons.pdb.converter.Converter;
import org.shredzone.commons.pdb.record.Record;

/**
 * Opens a PDB file and gives access to its contents.
 *
 * @see <a href="http://membres.multimania.fr/microfirst/palm/pdb.html">The Pilot Record Database Format</a>
 */
public class PdbFile extends RandomAccessFile {

    private static final String CHARSET = "iso-8859-1";
    private static final int NUM_CATEGORIES = 16;

    private CalendarFactory cf = CalendarFactory.getInstance();

    /**
     * Creates a new {@link PdbFile} for the given {@link File}.
     *
     * @param file
     *            {@link File} to be opened
     */
    public PdbFile(File file) throws FileNotFoundException {
        super(file, "r");
    }

    /**
     * Reads the entire database file and returns a {@link PdbDatabase}. You usually want
     * to invoke this method, as the other methods are just helpers.
     *
     * @param <T>
     *            {@link Record} subclass the database shall consist of
     * @param converter
     *            {@link Converter} that converts the raw database entries into
     *            {@link Record} objects
     * @return {@link PdbDatabase} containing the file contents
     * @throws IOException
     *             The file could not be read. This can have various reasons, for example
     *             if the file was no valid PDB file or the converter was not able to
     *             convert the file's contents.
     */
    public <T extends Record, U extends AppInfo> PdbDatabase<T, U> readDatabase(Converter<T, U> converter)
    throws IOException {
        PdbDatabase<T, U> result = new PdbDatabase<>();

        // Read the database header
        seek(0);
        result.setName(readTerminatedFixedString(32));
        result.setAttributes(readShort());
        result.setVersion(readShort());
        result.setCreationTime(readDate());
        result.setModificationTime(readDate());
        result.setBackupTime(readDate());
        result.setModificationNumber(readInt());
        int appInfoPos = readInt();
        int sortInfoPos = readInt();
        result.setType(readFixedString(4));
        result.setCreator(readFixedString(4));
        readInt();                              // Unique ID seed
        readInt();                              // Next index
        int records = readShort();

        // Read the entire record list
        int[] offsets = new int[records];
        int[] attributes = new int[records];
        for (int ix = 0; ix < records; ix++) {
            offsets[ix] = readInt();
            attributes[ix] = readUnsignedByte();
            readByte();
            readShort();
        }

        // Ask converter if it accepts the content
        if (!converter.isAcceptable(result)) {
            throw new IOException("Wrong database format");
        }

        // Read appInfo if available
        if (appInfoPos > 0) {
            int endPos = (records > 0) ? offsets[0] : (int) length();
            if (sortInfoPos > appInfoPos && sortInfoPos < endPos) {
                endPos = sortInfoPos;
            }
            int size = endPos - appInfoPos;

            seek(appInfoPos);
            result.setAppInfo(converter.convertAppInfo(this, size, result));
        }

        // Read each record
        for (int ix = 0; ix < records; ix++) {
            if (offsets[ix] >= length()) {
                continue;
            }

            int size;
            if (ix < records - 1) {
                size = offsets[ix + 1] - offsets[ix];
            } else {
                size = ((int) length()) - offsets[ix];
            }

            seek(offsets[ix]);
            T entry = converter.convert(this, ix, size, attributes[ix], result);
            if (entry != null) {
                result.getRecords().add(entry);
            }
        }

        return result;
    }

    /**
     * Reads a string of a fixed length, not null terminated.
     *
     * @param length
     *            The length of the string
     * @return String that was read
     */
    public String readFixedString(int length) throws IOException {
        byte[] data = new byte[length];
        readFully(data);
        return convertSpecialChars(new String(data, CHARSET));
    }

    /**
     * Reads a string of a fixed length that is null terminated. The given number of bytes
     * are always read, but the everything including and after the terminator character is
     * ignored.
     *
     * @param length
     *            The length of the string
     * @return String that was read
     */
    public String readTerminatedFixedString(int length) throws IOException {
        byte[] data = new byte[length];
        readFully(data);
        int pos = 0;
        while (pos < length) {
            if (data[pos] == 0) {
                return new String(data, 0, pos, CHARSET);
            }
            pos++;
        }
        return convertSpecialChars(new String(data, CHARSET));
    }

    /**
     * Reads a string of a variable length that is null terminated.
     *
     * @return String that was read
     */
    public String readTerminatedString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while(true) {
            int ch = readByte();
            if (ch == 0) break;
            baos.write(ch);
        }

        return convertSpecialChars(baos.toString(CHARSET));
    }

    /**
     * Reads an unsigned integer.
     *
     * @return Unsigned integer that was read
     */
    public long readUnsignedInt() throws IOException {
        int msb = readUnsignedShort();
        int lsb = readUnsignedShort();
        return ((long) msb) << 16 | lsb;
    }

    /**
     * Reads a PalmOS date.
     *
     * @return Calendar that was read. May be {@code null} if no date was set.
     */
    public Calendar readDate() throws IOException {
        long date = readUnsignedInt();
        if (date > 0) {
            Calendar result = cf.createPalmEpoch();
            result.setTimeInMillis(result.getTimeInMillis() + (date * 1000L));
            return result;
        } else {
            return null;
        }
    }

    /**
     * Reads a packed PalmOS date.
     *
     * @return Calendar containing the date read. May be {@code null} if no date
     * was set. The time part is always midnight local time.
     */
    public Calendar readPackedDate() throws IOException {
        int packed = readUnsignedShort();

        if (packed == 0xFFFF) {
            return null;
        }

        int year  = ((packed >> 9) & 0x007F) + CalendarFactory.EPOCH_YEAR;
        int month = ((packed >> 5) & 0x000F);
        int day   = ((packed     ) & 0x001F);

        Calendar cal = cf.create();
        cal.clear();
        cal.set(year, month - 1, day);
        return cal;
    }

    /**
     * Reads a PalmOS date and time that is stored in seven words.
     *
     * @return Calendar that was read
     */
    public Calendar readDateTimeWords() throws IOException {
        int second = readUnsignedShort();
        int minute = readUnsignedShort();
        int hour   = readUnsignedShort();
        int day    = readUnsignedShort();
        int month  = readUnsignedShort();   // 1..12
        int year   = readUnsignedShort();   // 4 digits
        readUnsignedShort();                // day of week, to be ignored...

        Calendar cal = cf.create();
        cal.clear();
        cal.set(year, month - 1, day, hour, minute, second);
        return cal;
    }

    /**
     * Reads the categories from a standard appinfo area and fills them into a
     * {@link CategoryAppInfo} object. After invocation, the file pointer points after the
     * category part, where further application information may be stored.
     *
     * @param appInfo
     *            {@link CategoryAppInfo} where the categories are stored.
     * @return Bytes that were actually read from the appinfo area. The file pointer is
     *         located at the beginning of the appinfo area plus the result of this
     *         method.
     */
    public int readCategories(CategoryAppInfo appInfo)
    throws IOException {
        long startPos = getFilePointer();

        // Read the rename flags
        int renamed = readShort();

        // Read the category names
        String[] catNames = new String[NUM_CATEGORIES];
        for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
            String catName = readTerminatedFixedString(16);
            if (catName.length() > 0) {
                catNames[ix] = catName;
            }
        }

        // Read the category keys
        for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
            int key = readByte();

            if (catNames[ix] != null) {
                appInfo.getCategories().add(new Category(
                    catNames[ix],
                    key,
                    (renamed & (1 << ix)) != 0
                ));
            } else {
                appInfo.getCategories().add(null);
            }
        }

        readByte();     // last unique ID
        readByte();     // padding

        long endPos = getFilePointer();

        return (int) (endPos - startPos);
    }

    /**
     * Converts special PalmOS characters into their unicode equivalents. The string
     * methods of {@link PdbFile} will invoke this method by itself, so you usually do not
     * need to invoke it. Note that some very special PalmOS characters cannot be
     * converted (as there are no unicode equivalents) and will be kept unchanged.
     *
     * @param str
     *            String to be converted
     * @return Converted string
     */
    public static String convertSpecialChars(String str) {
        return str
                .replace('\u0018', '\u2026') // Ellipsis
                .replace('\u0019', '\u2007') // Numeric Space
                .replace('\u0080', '\u20AC') // Euro
                .replace('\u0082', '\u201A') // Single Low Quotation Mark
                .replace('\u0083', '\u0192') // Small F with Hook
                .replace('\u0084', '\u201E') // Double Low Quotation Mark
                .replace('\u0085', '\u2026') // Ellipsis
                .replace('\u0086', '\u2020') // Dagger
                .replace('\u0087', '\u2021') // Double Dagger
                .replace('\u0088', '\u0302') // Combining Circumflex Accent
                .replace('\u0089', '\u2030') // Per Mille
                .replace('\u008A', '\u0160') // Capital S with Caron
                .replace('\u008B', '\u2039') // Single Left-pointing Angle Quotation Mark
                .replace('\u008C', '\u0152') // Capital Ligature OE
                .replace('\u008D', '\u2662') // Diamond
                .replace('\u008E', '\u2663') // Club
                .replace('\u008F', '\u2661') // Heart
                .replace('\u0090', '\u2660') // Spade
                .replace('\u0091', '\u2018') // Left Single Quotation Mark
                .replace('\u0092', '\u2019') // Right Single Quotation Mark
                .replace('\u0093', '\u201C') // Left Double Quotation Mark
                .replace('\u0094', '\u201D') // Right Double Quotation Mark
                .replace('\u0095', '\u2219') // Bullet
                .replace('\u0096', '\u2011') // Non-breaking Hyphen
                .replace('\u0097', '\u2012') // Figure Dash
                .replace('\u0098', '\u0303') // Combining Tilde
                .replace('\u0099', '\u2122') // Trademark
                .replace('\u009A', '\u0161') // Small S with Caron
                .replace('\u009B', '\u203A') // Single Right-pointing Angle Quotation Mark
                .replace('\u009C', '\u0153') // Small Ligature OE
                .replace('\u009F', '\u0178') // Capital Y with Diaeresis
                ;
    }

}
