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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.shredzone.commons.pdb.PdbDatabase;
import org.shredzone.commons.pdb.PdbFile;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.record.NotepadRecord;

/**
 * An {@link Converter} that handles notepad entries.
 * <p>
 * <em>NOTE:</em> This converter does not work in Android environments.
 */
public class NotepadConverter implements Converter<NotepadRecord, CategoryAppInfo> {

    private static final int FLAG_TITLE = 0x0002;
    private static final int FLAG_ALARM = 0x0004;

    private static final int TYPE_BITMAP = 0; // Raw bitmap
    private static final int TYPE_RLE    = 1; // Run Length Encoded bitmap
    private static final int TYPE_PNG    = 2; // PNG file

    @Override
    public boolean isAcceptable(PdbDatabase<NotepadRecord, CategoryAppInfo> database) {
        return "npadDB".equals(database.getName())
                && "npad".equals(database.getCreator());
    }

    @Override
    public NotepadRecord convert(PdbFile reader, int record, int size, int attribute,
            PdbDatabase<NotepadRecord, CategoryAppInfo> database) throws IOException {
        long current = reader.getFilePointer();

        NotepadRecord result = new NotepadRecord(attribute);
        if (result.isDelete()) {
            return null;
        }

        result.setCreated(reader.readDateTimeWords());
        result.setModified(reader.readDateTimeWords());
        int flags = reader.readUnsignedShort();

        if ((flags & FLAG_ALARM) != 0) {
            result.setAlarm(reader.readDateTimeWords());
        }

        if ((flags & FLAG_TITLE) != 0) {
            long start = reader.getFilePointer();
            result.setTitle(reader.readTerminatedString());
            long end = reader.getFilePointer();

            // If we're on an odd position, read one padding byte to make it even
            if (((end - start) % 2) == 1) {
                reader.readByte();
            }
        }

        reader.readUnsignedInt();                       // Offset to the image's end (?)
        int width = (int) reader.readUnsignedInt();     // Full image width
        int height = (int) reader.readUnsignedInt();    // Full image height
        reader.readUnsignedInt();                       // Always 1 (?)
        int type = (int) reader.readUnsignedInt();      // 0 = bitmap, 1 = RLE, 2 = PNG
        reader.readUnsignedInt();                       // Offset to the image's end (?)

        // Read image data
        int fileSize = size - (int) (reader.getFilePointer() - current);
        byte[] pngData = new byte[fileSize];
        reader.readFully(pngData);

        // Convert image if necessary
        switch (type) {
            case TYPE_RLE: //NOSONAR: falls through...
                pngData = uncompressRle(pngData);

            case TYPE_BITMAP: //NOSONAR: falls through...
                pngData = convertToPng(width, height, pngData);

            case TYPE_PNG:
                break;

            default:
                throw new IOException("unable to handle notepad image type " + type + " at record " + record);
        }

        result.setImagePng(pngData);

        return result;
    }

    @Override
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<NotepadRecord, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        reader.readCategories(result);
        return result;
    }

    /**
     * Uncompresses a Run Length Encoded bitmap. The compression scheme is actually very
     * simple. The first byte gives the number of times the second byte is written (i.e.
     * "0x06 0xC0" gives 6 times 0xC0). If the first byte is 0x00, the end of data has
     * been reached.
     *
     * @param width
     *            Image width
     * @param height
     *            Image height
     * @param rle
     *            Run Length Encoded data
     * @return Uncompressed raw bitmap
     */
    private byte[] uncompressRle(byte[] rle) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int ix = 0; ix < rle.length; ix += 2) {
                int cnt = rle[ix] & 0xFF;
                if (cnt == 0x00) break;

                byte data = rle[ix+1];
                while (cnt-- > 0) {
                    baos.write(data);
                }
            }

            return baos.toByteArray();
        }
    }

    /**
     * Converts a raw bitmap to a PNG file.
     *
     * @param width
     *            Image width
     * @param height
     *            Image height
     * @param bitmap
     *            Raw bitmap to be converted
     * @return PNG file containing that bitmap
     */
    private byte[] convertToPng(int width, int height, byte[] bitmap) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = image.getRaster();

        // Make the width a multiple of 16 first
        int bytesPerRow = ((((width - 1) / 16) + 1) * 16) / 8;

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                byte data = bitmap[(h * bytesPerRow) + (w / 8)];
                if ((data & (0x80 >> (w % 8))) == 0) { //NOSONAR: int promotion is safe
                    raster.setSample(w,h,0,1);
                }
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }

}
