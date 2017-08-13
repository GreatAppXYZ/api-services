package xyz.greatapp.apiservices.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.requests.user.RegisterUserRQ;
import xyz.greatapp.apiservices.services.RegistrationService;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * This service helps user creation by just providing email and password.
 * Role USER is automatically assigned to the new user.
 */
@RestController
public class RegistrationController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private RegistrationService registrationService;

    /**
     * Creates a user record by providing email and password.
     * @param registerUserRQ Contains email and password for new user.
     * @return The id of the new user as well as success information.
     */
    @RequestMapping(method = POST, value = "/registerUser")
    public ResponseEntity<ServiceResult> registerUser(@RequestBody RegisterUserRQ registerUserRQ)
    {
        try
        {
            return new ResponseEntity<>(registrationService.register(registerUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
