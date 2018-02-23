package com.knobtviker.android.things.contrib.community.driver.ds3231;

public class Alarm1 extends Alarm {
    private final int mSecond;

    /**
     * Obtains an instance of {@link Alarm1} from alarmRate, day, hour, minute, second.
     *
     * @param alarmRate One of Ds3231 alarm rates.
     * @param day       the day to represent, from 1 to 31 if alarmRate is
     *                  month, hour, minutes - or - from 1 to 7 if alarmRate is
     *                  week, hour, minutes,
     * @param hourOfDay the hour-of-day to represent, from 0 to 23
     * @param minute    the minute-of-hour to represent, from 0 to 59
     * @param second    the second-of-minute to represent, from 0 to 59
     */
    public Alarm1(int[] alarmRate, int day, int hourOfDay, int minute, int second) {
        super(alarmRate, day, hourOfDay, minute);
        if ((alarmRate != Ds3231.ALARM1_EVERY_SECOND
            && alarmRate != Ds3231.ALARM1_MATCH_SECONDS
            && alarmRate != Ds3231.ALARM1_MATCH_MINUTES_SECONDS
            && alarmRate != Ds3231.ALARM1_MATCH_HOURS_MINUTES_SECONDS
            && alarmRate != Ds3231.ALARM1_MATCH_DAY_OF_MONTH_HOURS_MINUTES_SECONDS
            && alarmRate != Ds3231.ALARM1_MATCH_DAY_OF_WEEK_HOURS_MINUTES_SECONDS)) {
            throw new IllegalArgumentException("Invalid alarmRate value");
        }
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException("Invalid second value. Second: " + second);
        }

        mSecond = second;
    }

    public int getSecond() {
        return mSecond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final Alarm1 alarm1 = (Alarm1) o;

        return mSecond == alarm1.mSecond;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mSecond;
        return result;
    }
}
