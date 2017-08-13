package xyz.greatapp.apiservices.requests.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteUserRQ
{
    private final String userId;

    @JsonCreator
    public DeleteUserRQ(@JsonProperty("user_id") String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }
}
