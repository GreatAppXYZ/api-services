package xyz.greatapp.apiservices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

/**
 * This service allows the execution of End2End services by openning
 * a transaction in database service and call it rollback on it at
 * then end of the tests.
 */
@Service
public class FunctionalTestTransactionService
{
    @Autowired
    private ThreadContextService threadContextService;
    @Autowired
    private ServiceLocator serviceLocator;
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Begins a transaction in database service which will be open across multiple calls to that service.
     * @return Success if the transaction was successfully opened or false otherwise.
     */
    public ServiceResult beginTransaction()
    {
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment())
                + "/acceptance_test/transaction/begin";

        ResponseEntity<ServiceResult> response = restTemplate.getForEntity(url, ServiceResult.class);
        return response.getBody();
    }

    /**
     * It calls database service in order to rollback the across-all-calls transaction.
     * @return Success if the transaction was successfully reverted or false otherwise.
     */
    public ServiceResult rollbackTransaction()
    {
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment())
                + "/acceptance_test/transaction/rollback";
        ResponseEntity<ServiceResult> response = restTemplate.getForEntity(url, ServiceResult.class);
        return response.getBody();
    }

    void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
}
