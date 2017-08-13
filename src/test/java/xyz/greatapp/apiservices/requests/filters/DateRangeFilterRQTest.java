package xyz.greatapp.apiservices.requests.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import xyz.greatapp.apiservices.requests.common.EasyDate;

public class DateRangeFilterRQTest
{
    @Test
    public void testGetters()
    {
        // when
        DateRangeFilterRQ dateRangeFilterRQ = new DateRangeFilterRQ(
                new EasyDate(1, 1, 2017),
                new EasyDate(2, 2, 2017));

        // then
        assertEquals(0, new EasyDate(1, 1, 2017).compareTo(dateRangeFilterRQ.getFrom()));
        assertEquals(0, new EasyDate(2, 2, 2017).compareTo(dateRangeFilterRQ.getTo()));
    }
}
