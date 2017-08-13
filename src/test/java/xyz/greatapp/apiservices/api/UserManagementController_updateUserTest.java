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
import xyz.greatapp.apiservices.requests.user.UpdateUserRQ;
import xyz.greatapp.apiservices.services.UserService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementController_updateUserTest
{
    @InjectMocks
    private UserManagementController userManagementController;

    @Mock
    private UserService userService;

    @Test
    public void shouldCallService() throws Exception
    {
        // given
        UpdateUserRQ request = new UpdateUserRQ("userId", "name", "email", "role");

        // when
        userManagementController.updateUser(request);

        // then
        verify(userService).updateUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        UpdateUserRQ request = new UpdateUserRQ("userId", "name", "email", "role");
        given(userService.updateUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = userManagementController.updateUser(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
