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

import java.util.Calendar;

/**
 * An {@link Record} implementation that contains a Notepad entry.
 */
public class NotepadRecord extends AbstractRecord implements DatedRecord {

    private String title;
    private Calendar created;
    private Calendar modified;
    private Calendar alarm;
    private byte[] imagePng;

    /**
     * Creates a new {@link NotepadRecord}.
     *
     * @param attribute
     *            Record attribute
     */
    public NotepadRecord(int attribute) {
        super(attribute);
    }

    /**
     * Get the title of the note. May be {@code null}.
     */
    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    /**
     * Get the date and time when the note was created.
     */
    public Calendar getCreated()                { return created; }
    public void setCreated(Calendar created)    { this.created = created; }

    /**
     * Get the date and time when the note was modified. May be {@code null}.
     */
    public Calendar getModified()               { return modified; }
    public void setModified(Calendar modified)  { this.modified = modified; }

    /**
     * Get the date and time of the notepad alarm. {@code null} when no alarm is set.
     */
    public Calendar getAlarm()                  { return alarm; }
    public void setAlarm(Calendar alarm)        { this.alarm = alarm; }

    /**
     * Gets the image data. This is a PNG file.
     */
    public byte[] getImagePng()                 { return imagePng; }
    public void setImagePng(byte[] imagePng)    { this.imagePng = imagePng; }

    @Override
    public Calendar getRecordDate() {
        return getModified();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notepad:[");
        sb.append("created=").append(created);
        if (modified != null) {
            sb.append(" modified=").append(modified);
        }
        if (title != null) {
            sb.append(" title='").append(title).append('\'');
        }
        if (alarm != null) {
            sb.append(" alarm=").append(alarm);
        }
        sb.append(" image-png=").append(imagePng.length).append(" bytes]");
        return sb.toString();
    }

}
