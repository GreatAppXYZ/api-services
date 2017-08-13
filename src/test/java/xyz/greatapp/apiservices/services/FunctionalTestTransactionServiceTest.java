package xyz.greatapp.apiservices.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.greatapp.libs.service.Environment.DEV;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;

@RunWith(MockitoJUnitRunner.class)
public class FunctionalTestTransactionServiceTest
{
    @InjectMocks
    private FunctionalTestTransactionService service;

    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity<ServiceResult> responseEntity;

    @Before
    public void setUp() throws Exception
    {
        service.setRestTemplate(restTemplate);
        when(threadContextService.getEnvironment()).thenReturn(DEV);
        when(restTemplate.getForEntity(anyString(), eq(ServiceResult.class))).thenReturn(responseEntity);
        when(serviceLocator.getServiceURI(any(), any())).thenReturn("");
    }

    @Test
    public void beginTransactionShouldOpenTransactionOnDatabaseService() throws Exception
    {
        // when
        service.beginTransaction();

        // then
        verify(restTemplate).getForEntity("/acceptance_test/transaction/begin", ServiceResult.class);
    }

    @Test
    public void rollbackTransactionShouldOpenTransactionOnDatabaseService() throws Exception
    {
        // when
        service.rollbackTransaction();

        // then
        verify(restTemplate).getForEntity("/acceptance_test/transaction/rollback", ServiceResult.class);
    }

}
