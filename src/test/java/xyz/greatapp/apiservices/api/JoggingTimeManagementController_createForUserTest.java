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
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.services.JoggingTimeService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeManagementController_createForUserTest
{
    @InjectMocks
    private JoggingTimeManagementController joggingTimeManagementController;

    @Mock
    private JoggingTimeService joggingTimeService;

    @Test
    public void shouldCallService() throws Exception
    {
        //given
        CreateJoggingTimeForUserRQ request = new CreateJoggingTimeForUserRQ("userId", 1, 1, 2017, 100, 3600);

        // when
        joggingTimeManagementController.createForUser(request);

        // then
        verify(joggingTimeService).createJoggingTimeForUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        CreateJoggingTimeForUserRQ request = new CreateJoggingTimeForUserRQ("userId", 1, 1, 2017, 100, 3600);
        given(joggingTimeService.createJoggingTimeForUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = joggingTimeManagementController.createForUser(request);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
