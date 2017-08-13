package xyz.greatapp.apiservices.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.services.JoggingTimeReportService;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * Service that provides information about speed average per week by
 * retrieving jogging times information from database.
 */
@RestController
@RequestMapping("/joggingTime/report")
public class JoggingTimeReportController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private JoggingTimeReportService joggingTimeReportService;

    /**
     * returns an accumulate of jogging times by year ans week.
     * @return The report information (year, week, total distance per week, total time per week,
     * speed average per week).
     */
    @RequestMapping(method = GET, value = "/perWeek")
    public ResponseEntity<ServiceResult> getPerWeek()
    {
        try
        {
            return new ResponseEntity<>(joggingTimeReportService.getPerWeek(), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
