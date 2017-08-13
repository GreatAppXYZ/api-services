package xyz.greatapp.apiservices.requests.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterUserRQ
{
    private final String email;
    private final String password;

    @JsonCreator
    public RegisterUserRQ(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password)
    {
        this.email = email;
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }
}
