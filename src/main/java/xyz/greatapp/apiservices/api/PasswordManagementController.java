package xyz.greatapp.apiservices.api;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordForUserRQ;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordRQ;
import xyz.greatapp.apiservices.services.PasswordService;
import xyz.greatapp.libs.service.ServiceResult;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Exposes functionality to update password for currently authenticated user
 * as well as user provided in request.
 */
@RestController
@RequestMapping("/password")
public class PasswordManagementController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private PasswordService passwordService;

    /**
     * Update password for the current authenticated user.
     * @param updatePasswordRQ Contains the current user password for verification purposes as well as
     *                         the new password to be set.
     * @return One if the password was updated or zero if it wasn't.
     */
    @RequestMapping(method = PUT, value = "/update")
    public ResponseEntity<ServiceResult> update(@RequestBody UpdatePasswordRQ updatePasswordRQ)
    {
        try
        {
            return new ResponseEntity<>(passwordService.update(updatePasswordRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update password for the user specified in the request.
     * @param updatePasswordForUserRQ Contains user password who is updating the password for verification purposes
     *                                as well as the new password to be set and the id of the user to be modified.
     * @return One if the password was updated or zero if it wasn't.
     */
    @RequestMapping(method = PUT, value = "/updateForUser")
    public ResponseEntity<ServiceResult> updateForUser(@RequestBody UpdatePasswordForUserRQ updatePasswordForUserRQ)
    {
        try
        {
            return new ResponseEntity<>(passwordService.updateForUser(updatePasswordForUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
