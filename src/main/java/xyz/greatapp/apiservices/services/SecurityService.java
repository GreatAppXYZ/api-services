package xyz.greatapp.apiservices.services;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@Service
public class SecurityService
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private ThreadContextService threadContextService;
    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private CommonService commonService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    public boolean joggingTimeBelongsToAuthUser(String joggingTimesId) throws JSONException
    {
        ServiceResult serviceResult = getJoggingTimeByIdAndUser(joggingTimesId);
        return !serviceResult.getObject().equals("{}");
    }

    private ServiceResult getJoggingTimeByIdAndUser(String joggingTimesId) throws JSONException
    {
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("jogging_times_id", joggingTimesId),
                new ColumnValue("user_id", commonService.getAuthenticatedUserId())
        };

        HttpEntity<SelectQueryRQ> entity = commonService.getHttpEntityForSelect(
                new SelectQueryRQ("jogging_times", filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
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

    public void setApiClientUtils(ApiClientUtils apiClientUtils)
    {
        this.apiClientUtils = apiClientUtils;
    }
}
