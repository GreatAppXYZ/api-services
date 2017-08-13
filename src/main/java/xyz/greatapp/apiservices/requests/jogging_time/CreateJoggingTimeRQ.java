package xyz.greatapp.apiservices.requests.jogging_time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateJoggingTimeRQ
{
    private final int day;
    private final int month;
    private final int year;
    private final int distance;
    private final int time;

    @JsonCreator
    public CreateJoggingTimeRQ(
            @JsonProperty("day") int day,
            @JsonProperty("month") int month,
            @JsonProperty("year") int year,
            @JsonProperty("distance") int distance,
            @JsonProperty("time") int time)
    {

        this.day = day;
        this.month = month;
        this.year = year;
        this.distance = distance;
        this.time = time;
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
