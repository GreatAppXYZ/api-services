package xyz.greatapp.apiservices.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.apiservices.services.FunctionalTestTransactionService;
import xyz.greatapp.libs.service.ServiceResult;

/**
 * This service allows the execution of End2End services by openning
 * a transaction in database service and call it rollback on it at
 * then end of the tests.
 */
@RestController
@RequestMapping("/acceptance_test/transaction")
public class AcceptanceTestTransactionController
{
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    private final FunctionalTestTransactionService functionalTestTransactionService;

    @Autowired
    public AcceptanceTestTransactionController(FunctionalTestTransactionService functionalTestTransactionService)
    {
        this.functionalTestTransactionService = functionalTestTransactionService;
    }

    /**
     * Begins a transaction in database service which will be open across multiple calls to that service.
     * @param request This parameter helps to validate we open transaction just in functional test environment.
     * @return Success if the transaction was successfully opened or false otherwise.
     * @throws Exception If any error occurs while opening the transaction.
     */
    @RequestMapping(value = "/begin", method = GET)
    public ResponseEntity<ServiceResult> begin(HttpServletRequest request) throws Exception
    {
        if (request.getServerName().equals("test.localhost"))
        {
            try
            {
                ServiceResult result = functionalTestTransactionService.beginTransaction();
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
                return new ResponseEntity<>(new ServiceResult(true, e.getMessage()), INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new ServiceResult(false, ""), BAD_REQUEST);
    }

    /**
     * It calls database service in order to rollback the across-all-calls transaction.
     * @param request This parameter helps to validate we revert the transaction just in functional test environment.
     * @return Success if the transaction was successfully reverted or false otherwise.
     * @throws Exception If any error occurs while reverting the transaction.
     */
    @RequestMapping(value = "/rollback", method = GET)
    public ResponseEntity<ServiceResult> rollback(HttpServletRequest request) throws Exception
    {
        if (request.getServerName().equals("test.localhost"))
        {
            try
            {
                ServiceResult result = functionalTestTransactionService.rollbackTransaction();
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
                return new ResponseEntity<>(new ServiceResult(true, e.getMessage()), INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new ServiceResult(false, ""), BAD_REQUEST);
    }
}
