package xyz.greatapp.apiservices.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.requests.filters.DateRangeFilterRQ;
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.GetJoggingTimesByUserWithFilterRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeRQ;
import xyz.greatapp.apiservices.services.JoggingTimeService;
import xyz.greatapp.apiservices.services.SecurityService;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * This service performs CRUD operations for jogging times.
 * It calls Database Service to save and retrieve information.
 */
@RestController
@RequestMapping("/joggingTime")
public class JoggingTimeManagementController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private JoggingTimeService joggingTimeService;

    @Autowired
    private SecurityService securityService;

    /**
     * Retrieves all jogging times between the date range filter passed in parameters.
     * @param dateRangeFilterRQ Contains dates from and to in order to filter the jogging times retrieved.
     * @return A list of jogging times as well as success status.
     */
    @RequestMapping(method = POST, value = "/getAll")
    public ResponseEntity<ServiceResult> getAll(@RequestBody DateRangeFilterRQ dateRangeFilterRQ)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.getAllJoggingTimes(dateRangeFilterRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all jogging times related to a specific user using a date range filter.
     * @param rq Contains User ID and date range filter.
     * @return An array with all the jogging times.
     */
    @RequestMapping(method = POST, value = "/getByUser")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ServiceResult> getByUser(@RequestBody GetJoggingTimesByUserWithFilterRQ rq)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.getAllJoggingTimesByUser(rq), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new jogging time record for the current authenticated user.
     * @param createJoggingTimeRQ Holds date, distance in meters and time in seconds for the new jogging time.
     * @return The ID for the new jogging time records as well as success info.
     */
    @RequestMapping(method = POST, value = "/create")
    public ResponseEntity<ServiceResult> create(@RequestBody CreateJoggingTimeRQ createJoggingTimeRQ)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.createJoggingTimeForAuthenticatedUser(createJoggingTimeRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a jogging time record for the specified user in parameters
     * @param createJoggingTimeForUserRQ Contains the user id and jogging time details.
     * @return The Id for the new jogging time record as well as success information.
     */
    @RequestMapping(method = POST, value = "/createForUser")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ServiceResult> createForUser(@RequestBody CreateJoggingTimeForUserRQ createJoggingTimeForUserRQ)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.createJoggingTimeForUser(createJoggingTimeForUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a jogging time providing date, distance in meters and time in seconds.
     * @param updateJoggingTimeRQ Details and id for the jogging time to update. Date, Distance in meters and time in seconds.
     * @return The number of updated rows. It should always be one. Returns false if none record was updated.
     */
    @RequestMapping(method = PUT, value = "/update")
    public ResponseEntity<ServiceResult> update(@RequestBody UpdateJoggingTimeRQ updateJoggingTimeRQ)
    {
        try
        {
            if (securityService.joggingTimeBelongsToAuthUser(updateJoggingTimeRQ.getJoggingTimeId()))
            {
                return new ResponseEntity<>(joggingTimeService.updateJoggingTime(updateJoggingTimeRQ), OK);
            }
            return new ResponseEntity<>(new ServiceResult(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a jogging time for the specified user in request providing date, distance in meters and time in seconds.
     * @param updateJoggingTimeForUserRQ User id  and id for the jogging time to update. Date, Distance in meters and time in seconds.
     * @return The number of updated rows. It should always be one. Returns false if none record was updated.
     */
    @RequestMapping(method = PUT, value = "/updateForUser")
    public ResponseEntity<ServiceResult> updateForUser(@RequestBody UpdateJoggingTimeForUserRQ updateJoggingTimeForUserRQ)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.updateJoggingTimeForUser(updateJoggingTimeForUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a jogging time record by Id.
     * @param deleteJoggingTimeRQ Contains the id for the jogging time record to delete.
     * @return Number of affected rows. Returns zero if none record was deleted.
     */
    @RequestMapping(method = DELETE, value = "/delete")
    public ResponseEntity<ServiceResult> delete(@RequestBody DeleteJoggingTimeRQ deleteJoggingTimeRQ)
    {
        try
        {
            if (securityService.joggingTimeBelongsToAuthUser(deleteJoggingTimeRQ.getJoggingTimesId()))
            {
                return new ResponseEntity<>(joggingTimeService.delete(deleteJoggingTimeRQ), OK);
            }
            return new ResponseEntity<>(new ServiceResult(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a jogging time record by Id for the specified user in request.
     * @param deleteJoggingTimeForUserRQ Contains the id for the jogging time record to delete and the user id that the jogging time belongs to.
     * @return Number of affected rows. Returns zero if none record was deleted.
     */
    @RequestMapping(method = DELETE, value = "/deleteForUser")
    public ResponseEntity<ServiceResult> deleteForUser(@RequestBody DeleteJoggingTimeForUserRQ deleteJoggingTimeForUserRQ)
    {
        try
        {
            return new ResponseEntity<>(joggingTimeService.deleteForUser(deleteJoggingTimeForUserRQ), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
