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
import xyz.greatapp.apiservices.requests.user.CreateUserRQ;
import xyz.greatapp.apiservices.services.UserService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementController_createUserTest
{
    @InjectMocks
    private UserManagementController userManagementController;

    @Mock
    private UserService userService;

    @Test
    public void shouldCallService() throws Exception
    {
        // given
        CreateUserRQ request = new CreateUserRQ("name", "email", "role", "password");

        // when
        userManagementController.createUser(request);

        // then
        verify(userService).createUser(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        CreateUserRQ request = new CreateUserRQ("name", "email", "role", "password");
        given(userService.createUser(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = userManagementController.createUser(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
