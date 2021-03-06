package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.common.EasyDate;
import xyz.greatapp.apiservices.requests.jogging_time.GetJoggingTimesByUserWithFilterRQ;
import xyz.greatapp.apiservices.services.JoggingTimeService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeManagementController_getByUserTest
{
    @InjectMocks
    private JoggingTimeManagementController joggingTimeManagementController;

    @Mock
    private JoggingTimeService joggingTimeService;

    @Test
    public void shouldCallService() throws Exception
    {
        // given
        GetJoggingTimesByUserWithFilterRQ request = new GetJoggingTimesByUserWithFilterRQ("userId",
                new EasyDate(1, 1, 2017), new EasyDate(2, 2, 2017));

        // when
        joggingTimeManagementController.getByUser(request);

        // then
        verify(joggingTimeService).getAllJoggingTimesByUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        GetJoggingTimesByUserWithFilterRQ request = new GetJoggingTimesByUserWithFilterRQ("userId",
                new EasyDate(1, 1, 2017), new EasyDate(2, 2, 2017));
        given(joggingTimeService.getAllJoggingTimesByUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = joggingTimeManagementController.getByUser(request);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
