package xyz.greatapp.apiservices.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.greatapp.libs.service.Environment.DEV;
import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.apiservices.requests.jogging_time.DeleteJoggingTimeRQ;
import xyz.greatapp.apiservices.requests.jogging_time.UpdateJoggingTimeRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class JoggingTimeService_DeleteTest
{
    @InjectMocks
    private JoggingTimeService joggingTimeService;

    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private ApiClientUtils apiClientUtils;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity<ServiceResult> responseEntity;
    @Mock
    private HttpEntity<SelectQueryRQ> httpEntityForSelect;
    @Mock
    private HttpEntity<DeleteQueryRQ> httpEntity;
    @Mock
    private CommonService commonService;
    @Mock
    private DeleteJoggingTimeRQ request;

    @Before
    public void setUp() throws Exception
    {
        joggingTimeService.setApiClientUtils(apiClientUtils);
        when(threadContextService.getEnvironment()).thenReturn(DEV);
        when(apiClientUtils.getRestTemplate()).thenReturn(restTemplate);
        when(serviceLocator.getServiceURI(any(), any())).thenReturn("");
        when(responseEntity.getBody()).thenReturn(new ServiceResult(true, "", "[]"));
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(ServiceResult.class))).thenReturn(responseEntity);
        when(commonService.getHttpEntityForSelect(any(SelectQueryRQ.class))).thenReturn(httpEntityForSelect);
        when(commonService.getHttpEntityForDelete(any(DeleteQueryRQ.class))).thenReturn(httpEntity);
        when(commonService.isValidDate(anyInt(), anyInt(), anyInt())).thenReturn(new ServiceResult(true, ""));
    }

    @Test
    public void shouldCallServiceLocator() throws JSONException
    {
        // when
        joggingTimeService.delete(request);

        // then
        verify(serviceLocator).getServiceURI(DATABASE, DEV);
    }

    @Test
    public void shouldPostForEntityUsingRestTemplate() throws JSONException
    {
        // when
        joggingTimeService.delete(request);

        // then
        verify(restTemplate).postForEntity("/delete", httpEntity, ServiceResult.class);
    }
}
