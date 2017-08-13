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
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.services.JoggingTimeService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeManagementController_updateForUserTest
{
    @InjectMocks
    private JoggingTimeManagementController joggingTimeManagementController;

    @Mock
    private JoggingTimeService joggingTimeService;

    @Test
    public void shouldCallService() throws Exception
    {
        //given
        UpdateJoggingTimeForUserRQ request = new UpdateJoggingTimeForUserRQ("userId", "joggingTimeId",
                1, 1, 2017, 100, 3600);

        // when
        joggingTimeManagementController.updateForUser(request);

        // then
        verify(joggingTimeService).updateJoggingTimeForUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        UpdateJoggingTimeForUserRQ request = new UpdateJoggingTimeForUserRQ("userId", "joggingTimeId",
                1, 1, 2017, 100, 3600);
        given(joggingTimeService.updateJoggingTimeForUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = joggingTimeManagementController.updateForUser(request);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
