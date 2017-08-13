package xyz.greatapp.apiservices.requests.common;

import org.junit.Test;

import static org.junit.Assert.*;


public class EasyDateTest
{
    @Test
    public void compareDays() throws Exception
    {
        //given
        EasyDate february_4_2017 = new EasyDate(4, 2, 2017);
        EasyDate february_3_2017 = new EasyDate(3, 2, 2017);

        //when
        int result = february_4_2017.compareTo(february_3_2017);

        //then
        assertEquals(1, result);
    }

    @Test
    public void compareMonths() throws Exception
    {
        //given
        EasyDate february_2_2017 = new EasyDate(1, 2, 2017);
        EasyDate march_3_2017 = new EasyDate(3, 3, 2017);

        //when
        int result = february_2_2017.compareTo(march_3_2017);

        //then
        assertEquals(-1, result);
    }

    @Test
    public void compareBeforeYear() throws Exception
    {
        //given
        EasyDate february_1_2016 = new EasyDate(1, 2, 2016);
        EasyDate february_1_2017 = new EasyDate(1, 2, 2017);

        //when
        int result = february_1_2016.compareTo(february_1_2017);

        //then
        assertEquals(-1, result);
    }

    @Test
    public void compareAfterYear() throws Exception
    {
        //given
        EasyDate february_1_2016 = new EasyDate(1, 2, 2016);
        EasyDate february_1_2015 = new EasyDate(1, 2, 2015);

        //when
        int result = february_1_2016.compareTo(february_1_2015);

        //then
        assertEquals(1, result);
    }

    @Test
    public void compareSameDate() throws Exception
    {
        //given
        EasyDate date1 = new EasyDate(1, 2, 2016);
        EasyDate date2 = new EasyDate(1, 2, 2016);

        //when
        int result = date1.compareTo(date2);

        //then
        assertEquals(0, result);
    }

    @Test
    public void testGetters() throws Exception
    {
        //given
        EasyDate easyDate = new EasyDate(1, 2, 2016);

        //when
        int day = easyDate.getDay();
        int month = easyDate.getMonth();
        int year = easyDate.getYear();

        //then
        assertEquals(1, day);
        assertEquals(2, month);
        assertEquals(2016, year);
    }
}
