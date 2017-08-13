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
import xyz.greatapp.apiservices.requests.user.DeleteUserRQ;
import xyz.greatapp.apiservices.services.UserService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementController_deleteUserTest
{
    @InjectMocks
    private UserManagementController userManagementController;

    @Mock
    private UserService userService;

    @Test
    public void shouldCallService() throws Exception
    {
        // given
        DeleteUserRQ request = new DeleteUserRQ("userId");

        // when
        userManagementController.deleteUser(request);

        // then
        verify(userService).deleteUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        DeleteUserRQ request = new DeleteUserRQ("userId");
        given(userService.deleteUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = userManagementController.deleteUser(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
