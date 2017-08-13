package xyz.greatapp.apiservices.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.requests.user.CreateUserRQ;
import xyz.greatapp.apiservices.requests.user.DeleteUserRQ;
import xyz.greatapp.apiservices.requests.user.UpdateUserRQ;
import xyz.greatapp.apiservices.services.UserService;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * This service performs CRUD operations for users.
 * It calls Database Service to save and retrieve information.
 */
@RestController
@RequestMapping("/user")
public class UserManagementController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private UserService userService;

    /**
     * Retrieves all the users from Database Service.
     * @return ServiceResult object with success information and a list of users
     */
    @RequestMapping(method = GET, value = "/getAll")
    public ResponseEntity<ServiceResult> getAll()
    {
        try
        {
            return new ResponseEntity<>(userService.getAllUsers(), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new user
     * @param createUserRQ Contains information about the new user. E.g. name, email, role, etc.
     * @return a Service Result object with success information and the new user Id.
     */
    @RequestMapping(method = POST, value = "/create")
    public ResponseEntity<ServiceResult> createUser(@RequestBody CreateUserRQ createUserRQ)
    {
        try
        {
            return new ResponseEntity<>(userService.createUser(createUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update user information. Except password.
     * @param updateUserRQ object with new information to be updated.
     * @return a Service Result containing success information as well as the number of rows updated from database service.
     * Returns zero if none records were updated.
     */
    @RequestMapping(method = PUT, value = "/update")
    public ResponseEntity<ServiceResult> updateUser(@RequestBody UpdateUserRQ updateUserRQ)
    {
        try
        {
            return new ResponseEntity<>(userService.updateUser(updateUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a single user from the repository.
     * @param deleteUserRQ Contains the id of the user to be removed.
     * @return Numbers of deleted rows. It should always be 1.
     */
    @RequestMapping(method = DELETE, value = "/delete")
    public ResponseEntity<ServiceResult> deleteUser(@RequestBody DeleteUserRQ deleteUserRQ)
    {
        try
        {
            return new ResponseEntity<>(userService.deleteUser(deleteUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
