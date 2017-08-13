package xyz.greatapp.apiservices.services;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordForUserRQ;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

/**
 * Exposes functionality to update password for currently authenticated user
 * as well as user provided in request.
 */
@Service
public class PasswordService
{
    @Autowired
    private CommonService commonService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private ThreadContextService threadContextService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    /**
     * Update password for the current authenticated user.
     * @param updatePasswordRQ Contains the current user password for verification purposes as well as
     *                         the new password to be set.
     * @return One if the password was updated or zero if it wasn't.
     */
    public ServiceResult update(UpdatePasswordRQ updatePasswordRQ) throws JSONException
    {
        String userId = commonService.getAuthenticatedUserId();
        ServiceResult validationResult = validateUpdatePasswordRQ(userId, updatePasswordRQ);
        if (!validationResult.isSuccess())
            return validationResult;

        return updatePasswordForUser(userId, updatePasswordRQ.getNewPassword());
    }

    private ServiceResult validateUpdatePasswordRQ(String userId, UpdatePasswordRQ updatePasswordRQ) throws JSONException
    {
        if (!passwordMatch(getUserById(userId).getObject(), updatePasswordRQ.getCurrentPassword()))
        {
            return new ServiceResult(false, "incorrect.current.password");
        }
        if (updatePasswordRQ.getNewPassword() == null || updatePasswordRQ.getNewPassword().isEmpty())
        {
            return new ServiceResult(false, "new.password.should.not.be.empty");
        }
        if (updatePasswordRQ.getNewPassword().length() < 6)
        {
            return new ServiceResult(false, "password.should.contain.at.least.6.characters");
        }
        return new ServiceResult(true, "");
    }

    private boolean passwordMatch(String userId, String password) throws JSONException
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password,
                new JSONObject(userId).getString("password"));
    }

    private ServiceResult updatePasswordForUser(String userId, String newPassword)
    {
        ApiClientUtils apiClientUtils = new ApiClientUtils();
        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("password", commonService.getEncryptedString(newPassword))
        };

        ColumnValue[] filters = new ColumnValue[1];
        filters[0] = new ColumnValue("user_id", userId);

        HttpEntity<UpdateQueryRQ> entity = new HttpEntity<>(
                new UpdateQueryRQ("users", sets, filters),
                apiClientUtils.getHttpHeaders());

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    private ServiceResult getUserById(String userId)
    {
        ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("user_id", userId)
        };

        HttpEntity<SelectQueryRQ> entity = commonService.getHttpEntityForSelect(
                new SelectQueryRQ("users", columnValues));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    /**
     * Update password for the user specified in the request.
     * @param updatePasswordForUserRQ Contains user password who is updating the password for verification purposes
     *                                as well as the new password to be set and the id of the user to be modified.
     * @return One if the password was updated or zero if it wasn't.
     */
    public ServiceResult updateForUser(UpdatePasswordForUserRQ updatePasswordForUserRQ) throws JSONException
    {
        ServiceResult validationResult = validateUpdatePasswordForUserRQ(updatePasswordForUserRQ);
        if (!validationResult.isSuccess())
            return validationResult;

        return updatePasswordForUser(updatePasswordForUserRQ.getUserId(), updatePasswordForUserRQ.getNewPasswordForUser());
    }

    private ServiceResult validateUpdatePasswordForUserRQ(UpdatePasswordForUserRQ rq) throws JSONException
    {
        String authenticatedUserId = commonService.getAuthenticatedUserId();
        if (!passwordMatch(getUserById(authenticatedUserId).getObject(), rq.getVerificationPassword()))
        {
            return new ServiceResult(false, "incorrect.authenticated.user.password");
        }
        if (rq.getNewPasswordForUser() == null || rq.getNewPasswordForUser().isEmpty())
        {
            return new ServiceResult(false, "new.password.should.not.be.empty");
        }
        if (rq.getNewPasswordForUser().length() < 6)
        {
            return new ServiceResult(false, "new.password.should.contain.at.least.6.characters");
        }
        if(isManagerTryingToUpdateNonUserPassword(rq)) {
            return new ServiceResult(false, "permission.denied");
        }
        return new ServiceResult(true, "");
    }

    private boolean isManagerTryingToUpdateNonUserPassword(UpdatePasswordForUserRQ rq) throws JSONException
    {
        return getUserRole(commonService.getAuthenticatedUserId()).equals("MANAGER") &&
                !getUserRole(rq.getUserId()).equals("USER");
    }

    private String getUserRole(String userId) throws JSONException
    {
        JSONObject user = new JSONObject(getUserById(userId).getObject());
        return user.getString("role");
    }

    void setApiClientUtils(ApiClientUtils apiClientUtils)
    {
        this.apiClientUtils = apiClientUtils;
    }
}
