package xyz.greatapp.apiservices.requests.jogging_time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.greatapp.apiservices.requests.common.EasyDate;

public class GetJoggingTimesByUserWithFilterRQ
{
    private final String userId;
    private final EasyDate from;
    private final EasyDate to;

    @JsonCreator
    public GetJoggingTimesByUserWithFilterRQ(
            @JsonProperty("user_id") String userId,
            @JsonProperty("from") EasyDate from,
            @JsonProperty("to") EasyDate to)
    {
        this.userId = userId;
        this.from = from;
        this.to = to;
    }

    public String getUserId()
    {
        return userId;
    }

    public EasyDate getFrom()
    {
        return from;
    }

    public EasyDate getTo()
    {
        return to;
    }
}
