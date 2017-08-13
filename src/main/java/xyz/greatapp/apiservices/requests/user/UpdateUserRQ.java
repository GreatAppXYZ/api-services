package xyz.greatapp.apiservices.requests.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.owasp.encoder.Encode.forHtml;

public class UpdateUserRQ
{
    private final String userId;
    private final String name;
    private final String email;
    private final String role;

    @JsonCreator
    public UpdateUserRQ(
            @JsonProperty("user_id") String userId,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("role") String role)
    {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName()
    {
        return forHtml(name);
    }

    public String getEmail()
    {
        return email;
    }

    public String getRole()
    {
        return role;
    }

    public String getUserId()
    {
        return userId;
    }
}
