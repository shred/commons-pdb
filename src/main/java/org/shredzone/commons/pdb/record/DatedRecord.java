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
 * This interface marks records that contain a date. Record classes must
 * implement this interface if they want to be filtered by date range.
 */
public interface DatedRecord extends Record {

    /**
     * Gets the date of this record. Usually this is the creation date. For
     * schedules this is the date of the schedule.
     *
     * @return {@link Calendar}, or {@code null} if this record has no date set.
     */
    Calendar getRecordDate();

}
