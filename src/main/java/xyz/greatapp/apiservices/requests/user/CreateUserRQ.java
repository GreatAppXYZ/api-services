package xyz.greatapp.apiservices.requests.user;

import static org.owasp.encoder.Encode.forHtml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateUserRQ
{
    private final String name;
    private final String email;
    private final String role;
    private final String password;

    @JsonCreator
    public CreateUserRQ(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("role") String role,
            @JsonProperty("password") String password)
    {

        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
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

    public String getPassword()
    {
        return password;
    }
}
