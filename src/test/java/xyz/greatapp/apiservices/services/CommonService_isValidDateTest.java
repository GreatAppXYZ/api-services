package xyz.greatapp.apiservices.services;

import org.junit.Before;
import org.junit.Test;
import xyz.greatapp.libs.service.ServiceResult;

import static org.junit.Assert.*;

public class CommonService_isValidDateTest
{
    private CommonService commonService;

    @Before
    public void setUp() throws Exception
    {
        commonService = new CommonService();
    }

    @Test
    public void testEmptyDate() throws Exception
    {
        //when
        ServiceResult serviceResult = commonService.isValidDate(0, 0, 0);

        //then
        assertEquals(false, serviceResult.isSuccess());
        assertEquals("invalid.date", serviceResult.getMessage());
    }

    @Test
    public void testInvalidDay() throws Exception
    {
        //when
        ServiceResult serviceResult = commonService.isValidDate(2017, 2, 30);

        //then
        assertEquals(false, serviceResult.isSuccess());
        assertEquals("invalid.date", serviceResult.getMessage());
    }

    @Test
    public void testFutureDate() throws Exception
    {
        //when
        ServiceResult serviceResult = commonService.isValidDate(3000, 1, 1);

        //then
        assertEquals(false, serviceResult.isSuccess());
        assertEquals("invalid.future.date", serviceResult.getMessage());
    }

    @Test
    public void testValidDate() throws Exception
    {
        //when
        ServiceResult serviceResult = commonService.isValidDate(2017, 2, 28);

        //then
        assertEquals(true, serviceResult.isSuccess());
    }
}
