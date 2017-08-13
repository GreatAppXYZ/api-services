package xyz.greatapp.apiservices.services;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.greatapp.apiservices.requests.user.CreateUserRQ;
import xyz.greatapp.apiservices.requests.user.DeleteUserRQ;
import xyz.greatapp.apiservices.requests.user.UpdateUserRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

/**
 * This class performs CRUD operations for users and jogging times.
 * It calls Database Service to save and retrieve information.
 */
@Service
public class UserService
{
    private final Logger logger = getLogger(getCallingClass());

    private final static Set<String> VALID_ROLES = new HashSet<>(asList("USER", "MANAGER", "ADMIN"));

    @Autowired
    private CommonService commonService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private ThreadContextService threadContextService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    /**
     * Retrieves all the users from Database Service.
     * @return ServiceResult object with success information and a list of users
     * @throws JSONException if any parsing goes wrong
     */
    public ServiceResult getAllUsers() throws JSONException
    {
        HttpEntity<SelectQueryRQ> entity = commonService.getHttpEntityForSelect(new SelectQueryRQ("users", new ColumnValue[0]));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        ServiceResult body = responseEntity.getBody();
        ServiceResult result;
        if (body.isSuccess())
        {
            result = new ServiceResult(body.isSuccess(), body.getMessage(), createUserList(body.getObject()).toString());
        }
        else
        {
            result = new ServiceResult(false, "Error returning users");
            logger.error(body.getMessage());
        }
        return result;
    }

    private JSONArray createUserList(String object) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(object);
        JSONObject[] userRSList = new JSONObject[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
            JSONObject userRS = new JSONObject();
            userRS.put("user_id", jsonObject.getString("user_id"));
            userRS.put("name", jsonObject.getString("name"));
            userRS.put("email", jsonObject.getString("email"));
            userRS.put("role", jsonObject.getString("role"));
            userRSList[i] = userRS;
        }
        return new JSONArray(asList(userRSList));
    }

    /**
     * Creates a new user
     * @param createUserRQ Contains information about the new user. E.g. name, email, role, etc.
     * @return a Service Result object with success information and the new user Id.
     */
    public ServiceResult createUser(CreateUserRQ createUserRQ)
    {
        ServiceResult serviceResult = validateCreateUserRQ(createUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doCreateUser(createUserRQ);
    }

    private ServiceResult validateCreateUserRQ(CreateUserRQ createUserRQ)
    {
        ServiceResult serviceResult = validateEmailAndRole(createUserRQ.getEmail(), createUserRQ.getRole());
        if (!serviceResult.isSuccess())
        {
            return serviceResult;
        }
        if (createUserRQ.getPassword() == null || createUserRQ.getPassword().isEmpty())
        {
            return new ServiceResult(false, "password.should.not.be.empty");
        }
        if (!EmailValidator.getInstance().isValid(createUserRQ.getEmail()))
        {
            return new ServiceResult(false, "wrong.email.format");
        }
        if (createUserRQ.getPassword().length() < 6)
        {
            return new ServiceResult(false, "password.should.contain.at.least.6.characters");
        }
        if (isExistingEmail(createUserRQ.getEmail()))
        {
            return new ServiceResult(false, "email.already.in.use");
        }
        return new ServiceResult(true, "");
    }

    private ServiceResult validateEmailAndRole(String email, String role)
    {
        if (email.isEmpty())
        {
            return new ServiceResult(false, "email.should.not.be.empty");
        }
        if (!VALID_ROLES.contains(role))
        {
            return new ServiceResult(false, "role.should.be.USER.MANAGER.or.ADMIN");
        }
        return new ServiceResult(true, "");
    }

    private boolean isExistingEmail(String email)
    {
        return !getUserByEmail(email).getObject().equals("{}");
    }

    ServiceResult getUserByEmail(String email)
    {

        ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("email", email)
        };

        HttpEntity<SelectQueryRQ> entity = commonService.getHttpEntityForSelect(new SelectQueryRQ("users", columnValues));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    private ServiceResult doCreateUser(CreateUserRQ createUserRQ)
    {
        ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("user_id", random(30, true, true)),
                new ColumnValue("name", createUserRQ.getName()),
                new ColumnValue("email", createUserRQ.getEmail()),
                new ColumnValue("password", commonService.getEncryptedString(createUserRQ.getPassword())),
                new ColumnValue("role", createUserRQ.getRole()),
                new ColumnValue("must_change_password", false),
                new ColumnValue("active", true),
                new ColumnValue("activation_key", random(60, true, true))
        };
        HttpEntity<InsertQueryRQ> entity = commonService.getHttpEntityForInsert(new InsertQueryRQ("users", columnValues, "user_id"));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    /**
     * Update user information. Except password.
     * @param updateUserRQ object with new information to be updated.
     * @return a Service Result containing success information as well as the number of rows updated from database service.
     * @throws JSONException if any parsing goes wrong.
     */
    public ServiceResult updateUser(UpdateUserRQ updateUserRQ) throws JSONException
    {
        ServiceResult serviceResult = validateUpdateUserRQ(updateUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doUpdateUser(updateUserRQ);
    }

    private ServiceResult validateUpdateUserRQ(UpdateUserRQ updateUserRQ) throws JSONException
    {
        ServiceResult serviceResult = validateEmailAndRole(updateUserRQ.getEmail(), updateUserRQ.getRole());
        if (!serviceResult.isSuccess())
            return serviceResult;

        serviceResult = validateOtherUserWithSameEmail(updateUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        if (!EmailValidator.getInstance().isValid(updateUserRQ.getEmail()))
        {
            return new ServiceResult(false, "wrong.email.format");
        }
        return new ServiceResult(true, "");
    }

    private ServiceResult validateOtherUserWithSameEmail(UpdateUserRQ updateUserRQ) throws JSONException
    {
        ServiceResult serviceResult = getUserByEmail(updateUserRQ.getEmail());
        if (!serviceResult.getObject().equals("{}"))
        {
            String userId = new JSONObject(serviceResult.getObject()).getString("user_id");
            if (!userId.equals(updateUserRQ.getUserId()))
            {
                return new ServiceResult(false, "email.already.in.use");
            }
        }
        return new ServiceResult(true, "");
    }

    private ServiceResult doUpdateUser(UpdateUserRQ updateUserRQ)
    {
        ColumnValue[] sets = new ColumnValue[3];
        sets[0] = new ColumnValue("name", updateUserRQ.getName());
        sets[1] = new ColumnValue("email", updateUserRQ.getEmail());
        sets[2] = new ColumnValue("role", updateUserRQ.getRole());

        ColumnValue[] filters = new ColumnValue[1];
        filters[0] = new ColumnValue("user_id", updateUserRQ.getUserId());

        HttpEntity<UpdateQueryRQ> entity = commonService.getHttpEntityForUpdate(new UpdateQueryRQ("users", sets, filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    /**
     * Deletes a single user from the repository.
     * @param deleteUserRQ Contains the id of the user to be removed.
     * @return Numbers of deleted rows. It should always be 1.
     * @throws JSONException in case any parsing goes wrong.
     */
    public ServiceResult deleteUser(DeleteUserRQ deleteUserRQ) throws JSONException
    {
        if (isAdmin(deleteUserRQ))
            return new ServiceResult(false, "main.admin.should.not.be.deleted");

        return doDelete(deleteUserRQ);
    }

    private boolean isAdmin(DeleteUserRQ deleteUserRQ) throws JSONException
    {
        return deleteUserRQ.getUserId().equals(getAdminId());
    }

    private String getAdminId() throws JSONException
    {
        ServiceResult serviceResult = getAllUsers();
        JSONArray users = new JSONArray(serviceResult.getObject());
        for (int i = 0; i < users.length(); i++)
        {
            JSONObject jsonObject = new JSONObject(users.get(i).toString());
            if (jsonObject.get("email").equals("admin@greatapp.xyz"))
            {
                return jsonObject.getString("user_id");
            }
        }
        return "";
    }

    private ServiceResult doDelete(DeleteUserRQ deleteUserRQ)
    {
        ColumnValue[] filters = new ColumnValue[1];
        filters[0] = new ColumnValue("user_id", deleteUserRQ.getUserId());

        HttpEntity<DeleteQueryRQ> entity = commonService.getHttpEntityForDelete(new DeleteQueryRQ("users", filters));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/delete";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    void setApiClientUtils(ApiClientUtils apiClientUtils)
    {
        this.apiClientUtils = apiClientUtils;
    }
}
