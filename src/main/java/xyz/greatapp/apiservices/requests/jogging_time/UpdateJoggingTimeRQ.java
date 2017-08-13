package xyz.greatapp.apiservices.requests.jogging_time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateJoggingTimeRQ
{
    private final String joggingTimeId;
    private final int day;
    private final int month;
    private final int year;
    private final int distance;
    private final int time;

    @JsonCreator
    public UpdateJoggingTimeRQ(
            @JsonProperty("jogging_times_id") String joggingTimeId,
            @JsonProperty("day") int day,
            @JsonProperty("month") int month,
            @JsonProperty("year") int year,
            @JsonProperty("distance") int distance,
            @JsonProperty("time") int time)
    {
        this.joggingTimeId = joggingTimeId;
        this.day = day;
        this.month = month;
        this.year = year;
        this.distance = distance;
        this.time = time;
    }

    public String getJoggingTimeId()
    {
        return joggingTimeId;
    }

    public int getDay()
    {
        return day;
    }

    public int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }

    public int getDistance()
    {
        return distance;
    }

    public int getTime()
    {
        return time;
    }
}
