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
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordForUserRQ;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordRQ;
import xyz.greatapp.apiservices.services.PasswordService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class PasswordManagementController_updateForUserTest
{
    @InjectMocks
    private PasswordManagementController passwordManagementController;

    @Mock
    private PasswordService passwordService;

    @Test
    public void shouldCallPasswordService() throws Exception
    {
        // given
        UpdatePasswordForUserRQ request = new UpdatePasswordForUserRQ(
                "verificationPassword", "userId", "newPassword");

        // when
        passwordManagementController.updateForUser(request);

        // then
        verify(passwordService).updateForUser(request);
    }


    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        UpdatePasswordForUserRQ request = new UpdatePasswordForUserRQ(
                "verificationPassword", "userId", "newPassword");
        given(passwordService.updateForUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = passwordManagementController.updateForUser(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
