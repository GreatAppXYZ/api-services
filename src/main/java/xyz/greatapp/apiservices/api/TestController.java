package xyz.greatapp.apiservices.api;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.libs.service.ServiceResult;

/** The purpose of this class is to expose a simple service that let
 * clients know the service is up.
 * It also allows to send CSRF Cookie to web clients.
 */
@RestController
public class TestController
{
    @RequestMapping(method = GET, value = "/test")
    public ResponseEntity<ServiceResult> testGet()
    {
        return new ResponseEntity<>(new ServiceResult(true, ""), OK);
    }

    @RequestMapping(method = POST, value = "/test")
    public ResponseEntity<ServiceResult> testPost()
    {
        return new ResponseEntity<>(new ServiceResult(true, ""), OK);
    }
}
