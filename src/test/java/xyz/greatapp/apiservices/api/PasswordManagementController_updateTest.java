package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordRQ;
import xyz.greatapp.apiservices.services.PasswordService;
import xyz.greatapp.apiservices.services.SecurityService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class PasswordManagementController_updateTest
{
    @InjectMocks
    private PasswordManagementController passwordManagementController;

    @Mock
    private PasswordService passwordService;

    @Test
    public void shouldCallPasswordService() throws Exception
    {
        // given
        UpdatePasswordRQ request = new UpdatePasswordRQ("currentPassword", "newPassword");

        // when
        passwordManagementController.update(request);

        // then
        verify(passwordService).update(request);
    }


    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        UpdatePasswordRQ request = new UpdatePasswordRQ("currentPassword", "newPassword");
        given(passwordService.update(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = passwordManagementController.update(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
