package xyz.greatapp.apiservices.requests.jogging_time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteJoggingTimeRQ
{
    private final String joggingTimesId;

    @JsonCreator
    public DeleteJoggingTimeRQ(@JsonProperty("jogging_times_id") String joggingTimesId)
    {
        this.joggingTimesId = joggingTimesId;
    }

    public String getJoggingTimesId()
    {
        return joggingTimesId;
    }
}
