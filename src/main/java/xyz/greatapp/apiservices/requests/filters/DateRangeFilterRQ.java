package xyz.greatapp.apiservices.requests.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.greatapp.apiservices.requests.common.EasyDate;

public class DateRangeFilterRQ
{
    private final EasyDate from;
    private final EasyDate to;

    @JsonCreator
    public DateRangeFilterRQ(
            @JsonProperty("from") EasyDate from,
            @JsonProperty("to") EasyDate to)
    {

        this.from = from;
        this.to = to;
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
