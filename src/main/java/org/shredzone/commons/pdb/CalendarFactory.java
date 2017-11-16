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

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A factory singleton for creating {@link Calendar} instances and keeping a central
 * {@link TimeZone}.
 * <p>
 * The default is the system's local time zone. To change to a different time zone, invoke
 * {@code CalendarFactory.getInstance().setTimeZone(tz)}. Instances are thread local, so
 * different threads may have different time zone settings.
 */
public class CalendarFactory {

    /**
     * Epoch of PalmOS starts on January 1st, 1904.
     */
    public static final int EPOCH_YEAR = 1904;

    private static final ThreadLocal<CalendarFactory> INSTANCES = new ThreadLocal<CalendarFactory>() {
        @Override
        protected CalendarFactory initialValue() {
            return new CalendarFactory();
        }
    };

    private TimeZone timeZone = TimeZone.getDefault();

    /**
     * Gets the singleton instance of the factory. The returned singleton is thread local
     * and can only be used by the invoking thread. On the other hand, multiple threads
     * can have individual time zone settings.
     *
     * @return {@link CalendarFactory} instance
     */
    public static CalendarFactory getInstance() {
        return INSTANCES.get();
    }

    /**
     * The {@link TimeZone} to be used. Defaults to the system's time zone.
     */
    public void setTimeZone(TimeZone timeZone)      { this.timeZone = timeZone; }
    public TimeZone getTimeZone()                   { return timeZone; }

    /**
     * Creates a new {@link Calendar} instance with the current time zone.
     *
     * @return {@link Calendar}
     */
    public Calendar create() {
        return Calendar.getInstance(timeZone);
    }

    /**
     * Creates a new {@link Calendar} instance with the given time zone.
     *
     * @param tz
     *            {@link TimeZone} to be used
     * @return {@link Calendar}
     */
    public Calendar createWithTimeZone(TimeZone tz) {
        return Calendar.getInstance(tz);
    }

    /**
     * Creates a {@link Calendar} that is set to the PalmOS epoch.
     *
     * @return {@link Calendar} set to the PalmOS epoch.
     */
    public Calendar createPalmEpoch() {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.clear();
        cal.set(EPOCH_YEAR, 0, 1, 0, 0, 0);
        return cal;
    }

}
