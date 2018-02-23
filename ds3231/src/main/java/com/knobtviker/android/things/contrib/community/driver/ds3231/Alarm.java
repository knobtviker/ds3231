package com.knobtviker.android.things.contrib.community.driver.ds3231;

import java.util.Arrays;

class Alarm {
    private final int[] mAlarmRate;
    private final int mDay;
    private final int mHourOfDay;
    private final int mMinute;

    public Alarm(final int[] alarmRate, final int day, final int hourOfDay, final int minute) {
        if (day < 1 || day > 31 || (day > 7
            && (alarmRate == Ds3231.ALARM1_MATCH_DAY_OF_WEEK_HOURS_MINUTES_SECONDS
            || alarmRate == Ds3231.ALARM2_MATCH_DAY_OF_WEEK_HOURS_MINUTES))) {
            throw new IllegalArgumentException("Invalid day value. Day: " + day);
        }
        if (hourOfDay < 0 || hourOfDay > 23) {
            throw new IllegalArgumentException("Invalid hourOfDay value. Hour of day: " + hourOfDay);
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute value. Minute: " + minute);
        }

        mAlarmRate = alarmRate;
        mDay = day;
        mHourOfDay = hourOfDay;
        mMinute = minute;
    }

    public int[] getAlarmRate() {
        return mAlarmRate;
    }

    public int getDay() {
        return mDay;
    }

    public int getHourOfDay() {
        return mHourOfDay;
    }

    public int getMinute() {
        return mMinute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Alarm alarm = (Alarm) o;

        if (mDay != alarm.mDay) {
            return false;
        }
        if (mHourOfDay != alarm.mHourOfDay) {
            return false;
        }
        if (mMinute != alarm.mMinute) {
            return false;
        }

        return Arrays.equals(mAlarmRate, alarm.mAlarmRate);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mAlarmRate);
        result = 31 * result + mDay;
        result = 31 * result + mHourOfDay;
        result = 31 * result + mMinute;
        return result;
    }
}
