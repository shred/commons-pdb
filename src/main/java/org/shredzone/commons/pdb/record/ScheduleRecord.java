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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.shredzone.commons.pdb.CalendarFactory;

/**
 * An {@link Record} implementation for the Calendar PDB.
 */
public class ScheduleRecord extends AbstractRecord implements DatedRecord {

    private CalendarFactory cf = CalendarFactory.getInstance();
    private ShortDate schedule;
    private ShortTime startTime;
    private ShortTime endTime;
    private Alarm alarm;
    private Repeat repeat;
    private List<ShortDate> exceptions = new ArrayList<>();
    private String description;
    private String note;
    private String location;
    private String category;

    /**
     * Creates a new {@link ScheduleRecord} entry.
     *
     * @param attribute
     *            Record's attribute
     */
    public ScheduleRecord(int attribute) {
        super(attribute);
    }

    /**
     * Gets the scheduled date.
     */
    public ShortDate getSchedule()              { return schedule; }
    public void setSchedule(ShortDate schedule) { this.schedule = schedule; }

    /**
     * Gets the starting time of the schedule. May be {@code null} if the schedule is for
     * the entire day.
     */
    public ShortTime getStartTime()             { return startTime; }
    public void setStartTime(ShortTime startTime) { this.startTime = startTime; }

    /**
     * Gets the ending time of the schedule. May be {@code null}. If the ending time is
     * before the starting time, it is related to the next day.
     */
    public ShortTime getEndTime()               { return endTime; }
    public void setEndTime(ShortTime endTime)   { this.endTime = endTime; }

    /**
     * Gets the alarm for this entry. May be {@code null} if there is no alarm.
     */
    public Alarm getAlarm()                     { return alarm; }
    public void setAlarm(Alarm alarm)           { this.alarm = alarm; }

    /**
     * Gets the repetition of this schedule. May be {@code null} if the schedule does not
     * repeat.
     */
    public Repeat getRepeat()                   { return repeat; }
    public void setRepeat(Repeat repeat)        { this.repeat = repeat; }

    /**
     * Gets the exceptions of a repeated schedule. Is never {@code null}, but may be
     * empty.
     */
    public List<ShortDate> getExceptions()      { return exceptions; }

    /**
     * Gets the description for this schedule. May be {@code null}.
     */
    public String getDescription()              { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets an optional note. May be {@code null}.
     */
    public String getNote()                     { return note; }
    public void setNote(String note)            { this.note = note; }

    /**
     * Gets the location of the schedule. May be {@code null}.
     */
    public String getLocation()                 { return location; }
    public void setLocation(String location)    { this.location = location; }

    /**
     * Gets the category of the schedule.
     */
    public String getCategory()                 { return category; }
    public void setCategory(String category)    { this.category = category; }

    @Override
    public Calendar getRecordDate() {
        Calendar result = cf.create();
        result.clear();
        result.set(schedule.getYear(), schedule.getMonth() - 1, schedule.getDay());
        if (startTime != null) {
            result.set(Calendar.HOUR_OF_DAY, startTime.getHour());
            result.set(Calendar.MINUTE, startTime.getMinute());
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule:[");
        sb.append(schedule);
        if (startTime != null) {
            sb.append(" start=").append(startTime);
        }
        if (endTime != null) {
            sb.append(" end=").append(endTime);
        }

        if (alarm != null) {
            sb.append(" alarm=").append(alarm);
        }

        if (repeat != null) {
            sb.append(" repeat=").append(repeat);
        }

        if (!exceptions.isEmpty()) {
            sb.append(" exceptions={");
            for (ShortDate exc : exceptions) {
                sb.append(exc).append(',');
            }
            sb.append('}');
        }

        if (description != null) {
            sb.append(" description='").append(description).append('\'');
        }

        if (note != null) {
            sb.append(" note='").append(note).append('\'');
        }

        if (location != null) {
            sb.append(" location='").append(location).append('\'');
        }

        if (category != null) {
            sb.append(" category='").append(category).append('\'');
        }

        sb.append(']');
        return sb.toString();
    }

    /**
     * Contains an immutable date.
     */
    public static class ShortDate {
        private final int year;
        private final int month;
        private final int day;

        /**
         * Creates a new {@link ShortDate}.
         *
         * @param year
         *            Year (fully, this is all four digits)
         * @param month
         *            Month (starting from 1!)
         * @param day
         *            Day
         */
        public ShortDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        /**
         * Creates a new {@link ShortDate}.
         *
         * @param calendar
         *            {@link Calendar} to read the date from
         */
        public ShortDate(Calendar calendar) {
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH) + 1;
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public int getYear()                { return year; }
        public int getMonth()               { return month; }
        public int getDay()                 { return day; }

        @Override
        public String toString() {
            return String.format("%04d-%02d-%02d", year, month, day);
        }
    }

    /**
     * Contains an immutable time.
     */
    public static class ShortTime {
        private final int hour;
        private final int minute;

        /**
         * Creates a new {@link ShortTime}.
         *
         * @param hour
         *            Hour (24 hours notation)
         * @param minute
         *            Minute
         */
        public ShortTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        /**
         * Creates a new {@link ShortTime}.
         *
         * @param calendar
         *            {@link Calendar} to read the time from
         */
        public ShortTime(Calendar calendar) {
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);
        }

        public int getHour()                { return hour; }
        public int getMinute()              { return minute; }

        @Override
        public String toString() {
            return String.format("%02d:%02d", hour, minute);
        }
    }

    /**
     * Defines an Alarm.
     */
    public static class Alarm {
        public enum Unit {
            MINUTES, HOURS, DAYS;
        }

        private final int value;
        private final Unit unit;

        /**
         * Creates a new {@link Alarm}.
         *
         * @param value
         *            Number of units <em>before</em> the scheduled date.
         * @param unit
         *            Unit
         */
        public Alarm(int value, Unit unit) {
            this.value = value;
            this.unit = unit;

        }

        public int getValue()               { return value; }
        public Unit getUnit()               { return unit; }

        @Override
        public String toString() {
            return String.format("%02d-%s", value, unit);
        }
    }

    /**
     * Defines the repetition of a schedule.
     */
    public static class Repeat {
        public enum Mode {
            DAILY, WEEKLY, MONTHLY_BY_DAY, MONTHLY, YEARLY;
        }

        private static final String[] WEEKDAYS = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };

        private final Mode mode;
        private final int frequency;
        private final ShortDate until;
        private final boolean[] weeklyDays;
        private final int monthlyWeek;
        private final int monthlyDay;

        /**
         * Creates a new {@link Repeat}.
         *
         * @param mode
         *            Repeat mode
         * @param frequency
         *            Repetition frequency
         * @param until
         *            Ending date
         * @param weeklyDays
         *            For Mode.WEEKLY: bitmap of weekdays
         * @param monthlyWeek
         *            For Mode.MONTHLY_BY_DAY: week number
         * @param monthlyDay
         *            For Mode.MONTHLY_BY_DAY: day of week
         */
        public Repeat(Mode mode, int frequency, ShortDate until, boolean[] weeklyDays,
            int monthlyWeek, int monthlyDay) {
            this.mode = mode;
            this.frequency = frequency;
            this.until = until;
            this.weeklyDays = weeklyDays;
            this.monthlyWeek = monthlyWeek;
            this.monthlyDay = monthlyDay;
        }

        public Mode getMode()                   { return mode; }
        public int getFrequency()               { return frequency; }
        public ShortDate getUntil()             { return until; }

        /**
         * Array of days in week where the repetition occurs. Index 0 = Sunday, and so on.
         * Valid for Mode.WEEKLY only.
         */
        public boolean[] getWeeklyDays()        { return weeklyDays; }

        /**
         * Week in month where the repetition occurs. Counted starting from 0. 4 means
         * "last week of month". Valid for Mode.MONTHLY_BY_DAY only.
         */
        public int getMonthlyWeek()             { return monthlyWeek; }

        /**
         * Weekday in week where the repetition occurs. 0 = Sunday, and so on.  Valid for
         * Mode.MONTHLY_BY_DAY only.
         */
        public int getMonthlyDay()              { return monthlyDay; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(mode).append('-').append(frequency);

            if (until != null) {
                sb.append("-until:").append(until);
            }

            if (mode == Mode.WEEKLY) {
                sb.append("-on");
                for (int ix = 0; ix < WEEKDAYS.length; ix++) {
                    if(weeklyDays[ix]) {
                        sb.append(':').append(WEEKDAYS[ix]);
                    }
                }
            }

            if (mode == Mode.MONTHLY_BY_DAY) {
                sb.append("-week:").append(monthlyWeek);
                sb.append("-day:").append(WEEKDAYS[monthlyDay]);
            }

            return sb.toString();
        }
    }

}
