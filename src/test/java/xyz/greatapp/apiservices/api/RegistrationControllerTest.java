package xyz.greatapp.apiservices.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.user.RegisterUserRQ;
import xyz.greatapp.apiservices.services.RegistrationService;
import xyz.greatapp.libs.service.ServiceResult;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest
{
    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private RegistrationService registrationService;

    @Test
    public void shouldCallService() throws Exception
    {
        //given
        RegisterUserRQ request = new RegisterUserRQ("email", "password");

        // when
        registrationController.registerUser(request);

        // then
        verify(registrationService).register(request);
    }

    @Test
    public void shouldReturnErrorIfAnyExceptionOccurs() throws Exception
    {
        // given
        RegisterUserRQ request = new RegisterUserRQ("email", "password");
        given(registrationService.register(request)).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = registrationController.registerUser(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
