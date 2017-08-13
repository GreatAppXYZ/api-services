package xyz.greatapp.apiservices.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import xyz.greatapp.libs.service.ServiceResult;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeReportServiceTest
{
    @InjectMocks
    private JoggingTimeReportService joggingTimeReportService;

    @Mock
    private JoggingTimeService joggingTimeService;

    @Before
    public void setUp() throws Exception
    {
        JSONArray array = new JSONArray();
        array.put(createJsonObject(1, 2, 2016, 2000, 3400));
        array.put(createJsonObject(1, 2, 2017, 3000, 1800));
        array.put(createJsonObject(3, 3, 2017, 1500, 3900));
        array.put(createJsonObject(4, 3, 2017, 1000, 4500));
        when(joggingTimeService.getAllJoggingTimesForAuthenticatedUser()).thenReturn(
                new ServiceResult(true, "", array.toString()));
    }

    private JSONObject createJsonObject(int day, int month, int year, int distance, int time) throws JSONException
    {
        JSONObject joggingTime = new JSONObject();
        joggingTime.put("day", day);
        joggingTime.put("month", month);
        joggingTime.put("year", year);
        joggingTime.put("distance", distance);
        joggingTime.put("time", time);
        return joggingTime;
    }

    @Test
    public void shouldGetAllJoggingTimesFromService() throws Exception
    {
        //when
        joggingTimeReportService.getPerWeek();

        //then
        verify(joggingTimeService).getAllJoggingTimesForAuthenticatedUser();
    }
}
