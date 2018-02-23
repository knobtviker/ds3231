package com.knobtviker.android.things.contrib.community.driver.ds3231;

public class Alarm2 extends Alarm {
    /**
     * Obtains an instance of {@link Alarm2} from alarmRate, day, hour, minute, second.
     *
     * @param alarmRate One of ALARM2_EVERY_MINUTE, ALARM2_MATCH_MINUTES,
     *                  ALARM2_MATCH_HOURS_MINUTES, ALARM2_MATCH_DAY_OF_MONTH_HOURS_MINUTES,
     *                  ALARM2_MATCH_DAY_OF_WEEK_HOURS_MINUTES.
     * @param day       the day to represent, from 1 to 31 if alarmRate is
     *                  ALARM2_MATCH_DAY_OF_MONTH_HOURS_MINUTES, from 1 to 7 if alarmRate is
     *                  ALARM2_MATCH_DAY_OF_WEEK_HOURS_MINUTES,
     * @param hourOfDay the hour-of-day to represent, from 0 to 23
     * @param minute    the minute-of-hour to represent, from 0 to 59
     */
    public Alarm2(int[] alarmRate, int day, int hourOfDay, int minute) {
        super(alarmRate, day, hourOfDay, minute);
        if ((alarmRate != Ds3231.ALARM2_EVERY_MINUTE
            && alarmRate != Ds3231.ALARM2_MATCH_MINUTES
            && alarmRate != Ds3231.ALARM2_MATCH_HOURS_MINUTES
            && alarmRate != Ds3231.ALARM2_MATCH_DAY_OF_MONTH_HOURS_MINUTES
            && alarmRate != Ds3231.ALARM2_MATCH_DAY_OF_WEEK_HOURS_MINUTES)) {
            throw new IllegalArgumentException("Invalid alarmRate value");
        }
    }
}
