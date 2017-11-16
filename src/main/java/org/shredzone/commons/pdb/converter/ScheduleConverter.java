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
import java.util.Calendar;

import org.shredzone.commons.pdb.PdbDatabase;
import org.shredzone.commons.pdb.PdbFile;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.commons.pdb.record.ScheduleRecord;
import org.shredzone.commons.pdb.record.ScheduleRecord.Alarm.Unit;
import org.shredzone.commons.pdb.record.ScheduleRecord.Repeat.Mode;

/**
 * An {@link Converter} that reads Calendar records.
 *
 * @see <a href="http://search.cpan.org/~bdfoy/p5-Palm-1.011/lib/Datebook.pm">Palm::Datebook</a>
 */
public class ScheduleConverter implements Converter<ScheduleRecord, CategoryAppInfo> {

    public static final int FLAG_ALARM = 0x4000;
    public static final int FLAG_REPEAT = 0x2000;
    public static final int FLAG_NOTE = 0x1000;
    public static final int FLAG_EXCEPTIONS = 0x0800;
    public static final int FLAG_DESCRIPTION = 0x0400;
    public static final int FLAG_LOCATION = 0x0200;  // only if creator is "PDat"

    @Override
    public boolean isAcceptable(PdbDatabase<ScheduleRecord, CategoryAppInfo> database) {
        return "PDat".equals(database.getCreator());
    }

    @Override
    public ScheduleRecord convert(PdbFile reader, int record, int size, int attribute,
            PdbDatabase<ScheduleRecord, CategoryAppInfo> database) throws IOException {
        ScheduleRecord result = new ScheduleRecord(attribute);
        if (result.isDelete()) {
            return null;
        }

        Category cat = database.getAppInfo().getCategoryByIndex(result.getCategoryIndex());
        if (cat != null) {
            result.setCategory(cat.getName());
        }

        byte startHour = reader.readByte();
        byte startMinute = reader.readByte();
        byte endHour = reader.readByte();
        byte endMinute = reader.readByte();
        Calendar date = reader.readPackedDate();
        int flags = reader.readShort();

        if (startHour >= 0 && startMinute >= 0) {
            result.setStartTime(new ScheduleRecord.ShortTime(startHour, startMinute));
        }

        if (endHour >= 0 && endMinute >= 0) {
            result.setEndTime(new ScheduleRecord.ShortTime(endHour, endMinute));
        }

        result.setSchedule(new ScheduleRecord.ShortDate(date));

        if ((flags & FLAG_ALARM) != 0) {
            int advance = reader.readByte();
            int unit = reader.readUnsignedByte();

            ScheduleRecord.Alarm.Unit alarmUnit;
            switch (unit) {
                case 0: alarmUnit = Unit.MINUTES; break;
                case 1: alarmUnit = Unit.HOURS; break;
                case 2: alarmUnit = Unit.DAYS; break;
                default: throw new IOException("Unknown alarm unit: " + unit);
            }

            result.setAlarm(new ScheduleRecord.Alarm(advance, alarmUnit));
        }

        if ((flags & FLAG_REPEAT) != 0) {
            int type = reader.readUnsignedByte();
            reader.readByte();

            ScheduleRecord.Repeat.Mode repeatMode;
            switch (type) {
                case 1: repeatMode = Mode.DAILY; break;
                case 2: repeatMode = Mode.WEEKLY; break;
                case 3: repeatMode = Mode.MONTHLY_BY_DAY; break;
                case 4: repeatMode = Mode.MONTHLY; break;
                case 5: repeatMode = Mode.YEARLY; break;
                default: throw new IOException("Unknown repeat mode: " + type);
            }

            ScheduleRecord.ShortDate endDate = null;
            Calendar ending = reader.readPackedDate();
            if (ending != null) {
                endDate = new ScheduleRecord.ShortDate(ending);
            }

            int frequency = reader.readUnsignedByte();
            int repeatOn = reader.readUnsignedByte();
            reader.readUnsignedByte();
            reader.readByte();

            boolean[] weeklyDays = new boolean[7];
            if (repeatMode == Mode.WEEKLY) {
                for (int ix = 0; ix < weeklyDays.length; ix++) {
                    weeklyDays[ix] = (repeatOn & (1 << ix)) != 0;
                }
            }

            int monthlyWeek = 0;
            int monthlyDay = 0;
            if (repeatMode == Mode.MONTHLY_BY_DAY) {
                monthlyWeek = repeatOn / 7;
                monthlyDay = repeatOn % 7;
            }

            result.setRepeat(new ScheduleRecord.Repeat(
                            repeatMode,
                            frequency,
                            endDate,
                            weeklyDays,
                            monthlyWeek,
                            monthlyDay
            ));
        }

        if ((flags & FLAG_EXCEPTIONS) != 0) {
            int numExceptions = reader.readUnsignedShort();
            for (int ix = 0; ix < numExceptions; ix++) {
                Calendar excDate = reader.readPackedDate();
                result.getExceptions().add(new ScheduleRecord.ShortDate(excDate));
            }
        }

        if ((flags & FLAG_DESCRIPTION) != 0) {
            result.setDescription(reader.readTerminatedString());
        }

        if ((flags & FLAG_NOTE) != 0) {
            result.setNote(reader.readTerminatedString());
        }

        if ((flags & FLAG_LOCATION) != 0) {
            result.setLocation(reader.readTerminatedString());
        }

        return result;
    }

    @Override
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<ScheduleRecord, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        reader.readCategories(result);
        return result;
    }

}
