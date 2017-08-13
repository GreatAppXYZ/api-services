package xyz.greatapp.apiservices.requests.password;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePasswordRQ
{
    private final String currentPassword;
    private final String newPassword;

    @JsonCreator
    public UpdatePasswordRQ(
            @JsonProperty("current_password") String currentPassword,
            @JsonProperty("new_password") String newPassword)
    {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }
}
