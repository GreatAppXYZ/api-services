package xyz.greatapp.apiservices.requests.password;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePasswordForUserRQ
{
    private final String verificationPassword;
    private final String userId;
    private final String newPasswordForUser;

    @JsonCreator
    public UpdatePasswordForUserRQ(
            @JsonProperty("verification_password") String verificationPassword,
            @JsonProperty("user_id") String userId,
            @JsonProperty("new_password_for_user") String newPasswordForUser)
    {
        this.verificationPassword = verificationPassword;
        this.userId = userId;
        this.newPasswordForUser = newPasswordForUser;
    }

    public String getVerificationPassword()
    {
        return verificationPassword;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getNewPasswordForUser()
    {
        return newPasswordForUser;
    }
}
