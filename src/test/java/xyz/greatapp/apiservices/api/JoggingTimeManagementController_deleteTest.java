package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeRQ;
import xyz.greatapp.apiservices.services.JoggingTimeService;
import xyz.greatapp.apiservices.services.SecurityService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeManagementController_deleteTest
{
    @InjectMocks
    private JoggingTimeManagementController joggingTimeManagementController;

    @Mock
    private JoggingTimeService joggingTimeService;
    @Mock
    private SecurityService securityService;

    @Test
    public void shouldCallServiceIfJoggingTimeBelongsToAuthenticatedUser() throws Exception
    {
        // given
        DeleteJoggingTimeRQ request = new DeleteJoggingTimeRQ("joggingTimesId");
        given(securityService.joggingTimeBelongsToAuthUser(anyString())).willReturn(true);

        // when
        joggingTimeManagementController.delete(request);

        // then
        verify(joggingTimeService).delete(request);
    }

    @Test
    public void shouldNeverCallServiceIfJoggingTimeDoesNotBelongsToAuthenticatedUser() throws Exception
    {
        // given
        DeleteJoggingTimeRQ request = new DeleteJoggingTimeRQ("joggingTimesId");
        given(securityService.joggingTimeBelongsToAuthUser(anyString())).willReturn(false);

        // when
        ResponseEntity<ServiceResult> response = joggingTimeManagementController.delete(request);

        // then
        verify(joggingTimeService, never()).delete(request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        DeleteJoggingTimeRQ request = new DeleteJoggingTimeRQ("joggingTimesId");
        given(securityService.joggingTimeBelongsToAuthUser(anyString())).willReturn(true);
        given(joggingTimeService.delete(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = joggingTimeManagementController.delete(request);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
