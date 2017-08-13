package xyz.greatapp.apiservices.services;

import static org.mockito.Matchers.any;
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
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class UserService_getAllUsersTest
{
    @InjectMocks
    private UserService userService;

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
    private HttpEntity<SelectQueryRQ> httpEntity;
    @Mock
    private CommonService commonService;

    @Before
    public void setUp() throws Exception
    {
        userService.setApiClientUtils(apiClientUtils);
        when(threadContextService.getEnvironment()).thenReturn(DEV);
        when(apiClientUtils.getRestTemplate()).thenReturn(restTemplate);
        when(serviceLocator.getServiceURI(any(), any())).thenReturn("");
        when(responseEntity.getBody()).thenReturn(new ServiceResult(true, "", "[]"));
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(ServiceResult.class))).thenReturn(responseEntity);
        when(commonService.getHttpEntityForSelect(any(SelectQueryRQ.class))).thenReturn(httpEntity);
    }

    @Test
    public void shouldCallServiceLocator() throws JSONException
    {
        // when
        userService.getAllUsers();

        // then
        verify(serviceLocator).getServiceURI(DATABASE, DEV);
    }

    @Test
    public void shouldPostForEntityUsingRestTemplate() throws JSONException
    {
        // when
        userService.getAllUsers();

        // then
        verify(restTemplate).postForEntity("/selectList", httpEntity, ServiceResult.class);
    }
}
