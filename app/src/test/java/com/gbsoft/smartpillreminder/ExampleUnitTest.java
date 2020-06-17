package com.gbsoft.smartpillreminder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void print_remainingTime() {
//        System.out.println(new Helper.TimeHelper().calculateTimeDiff("18:15"));
    }

    @Test
    public void print_remainingTimeInMillis() {
//        System.out.println(new Helper.TimeHelper().getRemainingTimeInLongMillis("18:38"));
    }

    @Test
    public void print_alarmTime() {
//        String time = "3:04 PM";
//        assertEquals(time, new Helper.TimeHelper().formatTime("15:4"));
//        System.out.println(new Helper.TimeHelper().formatTime("23:4"));
    }

    @Test
    public void print_uniqueRequestCodeForMedicine() {
//        System.out.println(Helper.getUniqueRequestCode("test"));
    }

    @Test
    public void addSixHoursTest() {
//        System.out.println(new Helper.TimeHelper().addSixHours("23:04"));
    }
}