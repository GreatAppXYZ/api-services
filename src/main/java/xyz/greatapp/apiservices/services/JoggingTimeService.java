package xyz.greatapp.apiservices.services;

import static org.apache.commons.lang.RandomStringUtils.random;
import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.greatapp.apiservices.requests.common.EasyDate;
import xyz.greatapp.apiservices.requests.filters.DateRangeFilterRQ;
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.CreateJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.GetJoggingTimesByUserWithFilterRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeForUserRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@Service
public class JoggingTimeService
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ThreadContextService threadContextService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    /**
     * Retrieves all jogging times between the date range filter passed in parameters.
     * @param dateRangeFilterRQ Contains dates from and to in order to filter the jogging times retrieved.
     * @return A list of jogging times as well as success status.
     * @throws JSONException In case any parsing fails.
     */
    public ServiceResult getAllJoggingTimes(DateRangeFilterRQ dateRangeFilterRQ) throws JSONException
    {
        return getJoggingTimesByUserIdAndDateRange(commonService.getAuthenticatedUserId(), dateRangeFilterRQ);
    }

    /**
     * Creates a new jogging time record for the current authenticated user.
     * @param createJoggingTimeRQ Holds date, distance and time for the new jogging time.
     * @return The ID for the new jogging time records as well as success info.
     */
    public ServiceResult createJoggingTimeForAuthenticatedUser(CreateJoggingTimeRQ createJoggingTimeRQ)
    {
        ServiceResult serviceResult = isValidCreateJoggingTimeRQ(createJoggingTimeRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doCreateJoggingTimeForAuthenticatedUser(createJoggingTimeRQ);
    }

    private ServiceResult isValidCreateJoggingTimeRQ(CreateJoggingTimeRQ rq)
    {
        return commonService.isValidDate(rq.getYear(), rq.getMonth(), rq.getDay());
    }

    private ServiceResult doCreateJoggingTimeForAuthenticatedUser(CreateJoggingTimeRQ createJoggingTimeRQ)
    {ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("jogging_times_id", random(60, true, true)),
                new ColumnValue("user_id", commonService.getAuthenticatedUserId()),
                new ColumnValue("day", createJoggingTimeRQ.getDay()),
                new ColumnValue("month", createJoggingTimeRQ.getMonth()),
                new ColumnValue("year", createJoggingTimeRQ.getYear()),
                new ColumnValue("distance", createJoggingTimeRQ.getDistance()),
                new ColumnValue("time", createJoggingTimeRQ.getTime())
        };


        HttpEntity<InsertQueryRQ> entity = commonService.getHttpEntityForInsert(
                new InsertQueryRQ("jogging_times", columnValues, "jogging_times_id"));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    private String createJoggingTimeList(String object, DateRangeFilterRQ dateRangeFilterRQ) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(object);
        List<JSONObject> userRSList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
            EasyDate dateInResponse = createEasyDateFromJson(jsonObject);

            if (dateInResponse.compareTo(dateRangeFilterRQ.getFrom()) >= 0
                    && dateInResponse.compareTo(dateRangeFilterRQ.getTo()) <= 0)
            {
                JSONObject userRS = new JSONObject();
                userRS.put("jogging_times_id", jsonObject.getString("jogging_times_id"));
                userRS.put("day", jsonObject.getString("day"));
                userRS.put("month", jsonObject.getString("month"));
                userRS.put("year", jsonObject.getString("year"));
                userRS.put("distance", jsonObject.getString("distance"));
                userRS.put("time", jsonObject.getString("time"));
                userRSList.add(userRS);
            }
        }
        return new JSONArray(userRSList).toString();
    }

    private EasyDate createEasyDateFromJson(JSONObject jsonObject) throws JSONException
    {
        return new EasyDate(
                jsonObject.getInt("day"),
                jsonObject.getInt("month"),
                jsonObject.getInt("year"));
    }

    /**
     * Deletes a jogging time record by Id.
     * @param deleteJoggingTimeRQ Contains the id for the jogging time record to delete.
     * @return Number of affected rows. Returns zero if none record was deleted.
     */
    public ServiceResult delete(DeleteJoggingTimeRQ deleteJoggingTimeRQ)
    {
        ColumnValue[] filters = new ColumnValue[1];
        filters[0] = new ColumnValue("jogging_times_id", deleteJoggingTimeRQ.getJoggingTimesId());

        HttpEntity<DeleteQueryRQ> entity = commonService.getHttpEntityForDelete(
                new DeleteQueryRQ("jogging_times", filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/delete";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    /**
     * Deletes a jogging time record by Id for the specified user in request.
     * @param deleteJoggingTimeForUserRQ Contains the id for the jogging time record to delete and the user id that the jogging time belongs to.
     * @return Number of affected rows. Returns zero if none record was deleted.
     */
    public ServiceResult deleteForUser(DeleteJoggingTimeForUserRQ deleteJoggingTimeForUserRQ)
    {
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("user_id", deleteJoggingTimeForUserRQ.getUserId()),
                new ColumnValue("jogging_times_id", deleteJoggingTimeForUserRQ.getJoggingTimesId())
        };

        HttpEntity<DeleteQueryRQ> entity = commonService.getHttpEntityForDelete(
                new DeleteQueryRQ("jogging_times", filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/delete";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    private ServiceResult getJoggingTimesByUserIdAndDateRange(String userId, DateRangeFilterRQ dateRangeFilterRQ) throws JSONException
    {
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("user_id", userId)
        };

        HttpEntity<SelectQueryRQ> entity = commonService.getHttpEntityForSelect(new SelectQueryRQ("jogging_times", filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        ServiceResult body = responseEntity.getBody();
        ServiceResult result;
        if (body.isSuccess())
        {
            result = new ServiceResult(
                    body.isSuccess(),
                    body.getMessage(),
                    createJoggingTimeList(body.getObject(), dateRangeFilterRQ));
        }
        else
        {
            result = new ServiceResult(false, "Error returning jogging times");
            logger.error(body.getMessage());
        }
        return result;
    }

    /**
     * Retrieves all jogging times related to a specific user using a date range filter.
     * @param rq Contains User ID and date range filter.
     * @return An array with all the jogging times.
     */
    public ServiceResult getAllJoggingTimesByUser(GetJoggingTimesByUserWithFilterRQ rq) throws JSONException
    {
        return getJoggingTimesByUserIdAndDateRange(
                rq.getUserId(),
                new DateRangeFilterRQ(rq.getFrom(), rq.getTo()));
    }

    public ServiceResult createJoggingTimeForUser(CreateJoggingTimeForUserRQ createJoggingTimeForUserRQ)
    {
        ServiceResult serviceResult = isValidCreateJoggingTimeRQ(createJoggingTimeForUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doCreateForUser(createJoggingTimeForUserRQ);
    }

    private ServiceResult isValidCreateJoggingTimeRQ(CreateJoggingTimeForUserRQ rq)
    {
        return commonService.isValidDate(rq.getYear(), rq.getMonth(), rq.getDay());
    }


    private ServiceResult doCreateForUser(CreateJoggingTimeForUserRQ createJoggingTimeForUserRQ)
    {
        ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("jogging_times_id", random(60, true, true)),
                new ColumnValue("user_id", createJoggingTimeForUserRQ.getUserId()),
                new ColumnValue("day", createJoggingTimeForUserRQ.getDay()),
                new ColumnValue("month", createJoggingTimeForUserRQ.getMonth()),
                new ColumnValue("year", createJoggingTimeForUserRQ.getYear()),
                new ColumnValue("distance", createJoggingTimeForUserRQ.getDistance()),
                new ColumnValue("time", createJoggingTimeForUserRQ.getTime())
        };

        HttpEntity<InsertQueryRQ> entity = commonService.getHttpEntityForInsert(
                new InsertQueryRQ("jogging_times", columnValues, "jogging_times_id"));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    public ServiceResult updateJoggingTime(UpdateJoggingTimeRQ updateJoggingTimeRQ)
    {
        ServiceResult serviceResult = validateUpdateJoggingTimeRQ(updateJoggingTimeRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("day", updateJoggingTimeRQ.getDay()),
                new ColumnValue("month", updateJoggingTimeRQ.getMonth()),
                new ColumnValue("year", updateJoggingTimeRQ.getYear()),
                new ColumnValue("distance", updateJoggingTimeRQ.getDistance()),
                new ColumnValue("time", updateJoggingTimeRQ.getTime())
        };

        ColumnValue[] filters = new ColumnValue[1];
        filters[0] = new ColumnValue("jogging_times_id", updateJoggingTimeRQ.getJoggingTimeId());

        HttpEntity<UpdateQueryRQ> entity = commonService.getHttpEntityForUpdate(
                new UpdateQueryRQ("jogging_times", sets, filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    /**
     * Updates a jogging time for the specified user in request providing date, distance in meters and time in seconds.
     * @param updateJoggingTimeForUserRQ User id  and id for the jogging time to update. Date, Distance in meters and time in seconds.
     * @return The number of updated rows. It should always be one. Returns false if none record was updated.
     */
    public ServiceResult updateJoggingTimeForUser(UpdateJoggingTimeForUserRQ updateJoggingTimeForUserRQ)
    {
        ServiceResult serviceResult = isValidCreateJoggingTimeRQ(updateJoggingTimeForUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doUpdateForUser(updateJoggingTimeForUserRQ);
    }

    private ServiceResult isValidCreateJoggingTimeRQ(UpdateJoggingTimeForUserRQ rq)
    {
        return commonService.isValidDate(rq.getYear(), rq.getMonth(), rq.getDay());
    }

    private ServiceResult doUpdateForUser(UpdateJoggingTimeForUserRQ updateJoggingTimeForUserRQ)
    {
        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("day", updateJoggingTimeForUserRQ.getDay()),
                new ColumnValue("month", updateJoggingTimeForUserRQ.getMonth()),
                new ColumnValue("year", updateJoggingTimeForUserRQ.getYear()),
                new ColumnValue("distance", updateJoggingTimeForUserRQ.getDistance()),
                new ColumnValue("time", updateJoggingTimeForUserRQ.getTime())
        };

        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("jogging_times_id", updateJoggingTimeForUserRQ.getJoggingTimeId()),
                new ColumnValue("user_id", updateJoggingTimeForUserRQ.getUserId())
        };

        HttpEntity<UpdateQueryRQ> entity = commonService.getHttpEntityForUpdate(
                new UpdateQueryRQ("jogging_times", sets, filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    private ServiceResult validateUpdateJoggingTimeRQ(UpdateJoggingTimeRQ rq)
    {
        return commonService.isValidDate(rq.getYear(), rq.getMonth(), rq.getDay());
    }

    ServiceResult getAllJoggingTimesForAuthenticatedUser() throws JSONException
    {
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("user_id", commonService.getAuthenticatedUserId())
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("jogging_times", filters),
                apiClientUtils.getHttpHeaders());
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        ServiceResult body = responseEntity.getBody();
        ServiceResult result;
        if (body.isSuccess())
        {
            result = new ServiceResult(
                    body.isSuccess(),
                    body.getMessage(),
                    body.getObject());
        }
        else
        {
            result = new ServiceResult(false, "Error returning jogging times");
            logger.error(body.getMessage());
        }
        return result;
    }

    void setApiClientUtils(ApiClientUtils apiClientUtils)
    {
        this.apiClientUtils = apiClientUtils;
    }
}
