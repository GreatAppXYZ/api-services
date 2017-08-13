package xyz.greatapp.apiservices.services;

import static java.util.Calendar.WEEK_OF_YEAR;
import static java.util.Calendar.YEAR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * Service that provides information about speed average per week by
 * retrieving jogging times information from database.
 */
@Service
public class JoggingTimeReportService
{
    @Autowired
    private JoggingTimeService joggingTimeService;

    /**
     * returns an accumulate of jogging times by year ans week.
     * @return The report information (year, week, total distance per week, total time per week,
     * speed average per week).
     */
    public ServiceResult getPerWeek() throws JSONException
    {
        ServiceResult allJoggingTimes = joggingTimeService.getAllJoggingTimesForAuthenticatedUser();
        Map<Integer, List<JSONObject>> joggingTimesPerYear = groupJoggingTimesPerYear(allJoggingTimes.getObject());
        Map<Integer, Map<Integer, List<JSONObject>>> joggingTimesPerWeek = groupJoggingTimesPerWeek(joggingTimesPerYear);
        Map<Integer, Map<Integer, JSONObject>> averageSpeedPerWeek = groupAverageSpeedPerWeek(joggingTimesPerWeek);
        JSONArray result = convertToJsonArray(averageSpeedPerWeek);
        return new ServiceResult(true, "", result.toString());
    }

    private Map<Integer, List<JSONObject>> groupJoggingTimesPerYear(String object) throws JSONException
    {
        Map<Integer, List<JSONObject>> joggingTimesPerWeek = new HashMap<>();
        JSONArray jsonArray = new JSONArray(object);

        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
            int year = jsonObject.getInt("year");
            List<JSONObject> list = joggingTimesPerWeek.getOrDefault(year, new ArrayList<>());
            list.add(jsonObject);
            joggingTimesPerWeek.put(year, list);
        }
        return joggingTimesPerWeek;
    }

    private Map<Integer, Map<Integer, List<JSONObject>>> groupJoggingTimesPerWeek(Map<Integer, List<JSONObject>> perYearMap) throws JSONException
    {
        Map<Integer, Map<Integer, List<JSONObject>>> result = new HashMap<>();

        for (Map.Entry<Integer, List<JSONObject>> perYear : perYearMap.entrySet())
        {
            Map<Integer, List<JSONObject>> joggingTimesPerWeek = new HashMap<>();

            for (JSONObject joggingTime : perYear.getValue())
            {
                int week = getWeekNumber(joggingTime);
                List<JSONObject> list = joggingTimesPerWeek.getOrDefault(week, new ArrayList<>());
                list.add(joggingTime);

                joggingTimesPerWeek.put(week, list);
            }
            result.put(perYear.getKey(), joggingTimesPerWeek);
        }

        return result;
    }

    private int getWeekNumber(JSONObject jsonObject) throws JSONException
    {
        Calendar instance = Calendar.getInstance();
        instance.set(jsonObject.getInt("year"), jsonObject.getInt("month") - 1, jsonObject.getInt("day"));
        return instance.get(WEEK_OF_YEAR);
    }

    private Map<Integer, Map<Integer, JSONObject>> groupAverageSpeedPerWeek(Map<Integer, Map<Integer, List<JSONObject>>> perWeekYearMap) throws JSONException
    {
        Map<Integer, Map<Integer, JSONObject>> result = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, List<JSONObject>>> perWeekYear : perWeekYearMap.entrySet())
        {
            Map<Integer, JSONObject> averagePerWeek = new HashMap<>();
            Map<Integer, List<JSONObject>> weeks = perWeekYear.getValue();
            for (Map.Entry<Integer, List<JSONObject>> week : weeks.entrySet())
            {
                averagePerWeek.put(week.getKey(), getDistanceAndSpeedAverage(week, perWeekYear.getKey()));
            }

            result.put(perWeekYear.getKey(), averagePerWeek);
        }

        return result;
    }

    private JSONObject getDistanceAndSpeedAverage(Map.Entry<Integer, List<JSONObject>> week, Integer year) throws JSONException
    {
        double distance = 0;
        double time = 0;
        for (JSONObject joggingTime : week.getValue())
        {
            distance += joggingTime.getInt("distance");
            time += joggingTime.getInt("time");
        }

        JSONObject result = new JSONObject();
        result.put("distance", distance);
        result.put("time", time);
        result.put("startDate", getFirstDayOfTheWeek(week.getKey(), year));
        double value = (distance / 1000) / (time / 3600);
        if (Double.isNaN(value))
        {
            result.put("averageSpeed", 0);
        }
        else
        {
            result.put("averageSpeed", value);
        }

        return result;
    }

    private String getFirstDayOfTheWeek(Integer week, Integer year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(WEEK_OF_YEAR, week);
        calendar.set(YEAR, year);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/YYYY");
        return format.format(calendar.getTime());
    }

    private JSONArray convertToJsonArray(Map<Integer, Map<Integer, JSONObject>> averageSpeedPerWeek) throws JSONException
    {
        JSONArray result = new JSONArray();
        for (Map.Entry<Integer, Map<Integer, JSONObject>> yearsMap : averageSpeedPerWeek.entrySet())
        {
            JSONArray weeks = new JSONArray();
            for (Map.Entry<Integer, JSONObject> weeksMap : yearsMap.getValue().entrySet())
            {
                JSONObject averagePerWeek = new JSONObject();
                averagePerWeek.put("week", weeksMap.getKey());
                averagePerWeek.put("averageData", weeksMap.getValue());
                weeks.put(averagePerWeek);
            }

            JSONObject averagePerYear = new JSONObject();
            averagePerYear.put("year", yearsMap.getKey());
            averagePerYear.put("weeks", weeks);
            result.put(averagePerYear);
        }
        return result;
    }
}
