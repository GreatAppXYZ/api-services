package xyz.greatapp.apiservices.services;

import static org.apache.commons.lang.RandomStringUtils.random;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.greatapp.apiservices.requests.user.RegisterUserRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;

/**
 * This service helps user creation by just providing email and password.
 * Role USER is automatically assigned to the new user.
 */
@Service
public class RegistrationService
{
    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadContextService threadContextService;
    @Autowired
    private CommonService commonService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    /**
     * Creates a user record by providing email and password.
     * @param registerUserRQ Contains email and password for new user.
     * @return The id of the new user as well as success information.
     */
    public ServiceResult register(RegisterUserRQ registerUserRQ)
    {
        ServiceResult serviceResult = validateRequest(registerUserRQ);
        if (!serviceResult.isSuccess())
            return serviceResult;

        return doRegisterUser(registerUserRQ);
    }

    private ServiceResult validateRequest(RegisterUserRQ registerUserRQ)
    {
        if (registerUserRQ.getEmail() == null || registerUserRQ.getEmail().isEmpty())
        {
            return new ServiceResult(false, "email.should.not.be.empty");
        }
        if (!EmailValidator.getInstance().isValid(registerUserRQ.getEmail()))
        {
            return new ServiceResult(false, "wrong.email.format");
        }
        if (registerUserRQ.getPassword() == null || registerUserRQ.getPassword().isEmpty())
        {
            return new ServiceResult(false, "password.should.not.be.empty");
        }
        if (registerUserRQ.getPassword().length() < 6)
        {
            return new ServiceResult(false, "password.should.contain.at.least.6.characters");
        }
        if (isExistingEmail(registerUserRQ))
        {
            return new ServiceResult(false, "email.already.in.use");
        }
        return new ServiceResult(true, "");
    }

    private boolean isExistingEmail(RegisterUserRQ registerUserRQ)
    {
        return !userService.getUserByEmail(registerUserRQ.getEmail()).getObject().equals("{}");
    }

    private ServiceResult doRegisterUser(RegisterUserRQ registerUserRQ)
    {
        ColumnValue[] columnValues = new ColumnValue[] {
                new ColumnValue("user_id", random(30, true, true)),
                new ColumnValue("email", registerUserRQ.getEmail()),
                new ColumnValue("password", new BCryptPasswordEncoder().encode(registerUserRQ.getPassword())),
                new ColumnValue("role", "USER"),
                new ColumnValue("must_change_password", false),
                new ColumnValue("active", true),
                new ColumnValue("activation_key", random(60, true, true))
        };

        HttpEntity<InsertQueryRQ> entity = commonService.getHttpEntityForInsert(
                new InsertQueryRQ("users", columnValues, "user_id"));

        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
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
