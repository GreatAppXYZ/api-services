package xyz.greatapp.apiservices.requests.jogging_time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteJoggingTimeForUserRQ
{
    private final String userId;
    private final String joggingTimesId;

    @JsonCreator
    public DeleteJoggingTimeForUserRQ(
            @JsonProperty("user_id") String userId,
            @JsonProperty("jogging_times_id") String joggingTimesId)
    {
        this.userId = userId;
        this.joggingTimesId = joggingTimesId;
    }

    public String getJoggingTimesId()
    {
        return joggingTimesId;
    }

    public String getUserId()
    {
        return userId;
    }
}
