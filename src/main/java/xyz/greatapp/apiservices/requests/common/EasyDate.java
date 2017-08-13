package xyz.greatapp.apiservices.requests.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EasyDate implements Comparable
{
    private final int day;
    private final int month;
    private final int year;

    @JsonCreator
    public EasyDate(
            @JsonProperty("day") int day,
            @JsonProperty("month") int month,
            @JsonProperty("year") int year)
    {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    int getDay()
    {
        return day;
    }

    int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }

    @Override
    public int compareTo(Object o)
    {
        EasyDate that = (EasyDate) o;
        if (this.year != that.year)
        {
            return this.year > that.year ? 1 : -1;
        }
        if (this.month != that.month)
        {
            return this.month > that.month ? 1 : -1;
        }
        if (this.day != that.day)
        {
            return this.day > that.day ? 1 : -1;
        }
        return 0;
    }
}
