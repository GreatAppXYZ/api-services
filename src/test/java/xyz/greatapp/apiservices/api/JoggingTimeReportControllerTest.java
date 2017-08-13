package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.common.EasyDate;
import xyz.greatapp.apiservices.requests.filters.DateRangeFilterRQ;
import xyz.greatapp.apiservices.services.JoggingTimeReportService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeReportControllerTest
{
    @InjectMocks
    private JoggingTimeReportController joggingTimeReportController;

    @Mock
    private JoggingTimeReportService joggingTimeReportService;

    @Test
    public void shouldCallService() throws Exception
    {
        // when
        joggingTimeReportController.getPerWeek();

        // then
        verify(joggingTimeReportService).getPerWeek();
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        given(joggingTimeReportService.getPerWeek()).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = joggingTimeReportController.getPerWeek();

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
