package xyz.greatapp.apiservices.services;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
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
import xyz.greatapp.apiservices.requests.user.CreateUserRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class UserService_createUserTest
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
    private ResponseEntity<ServiceResult> responseEntityForSelect;
    @Mock
    private ResponseEntity<ServiceResult> responseEntity;
    @Mock
    private HttpEntity<SelectQueryRQ> httpEntityForSelect;
    @Mock
    private HttpEntity<InsertQueryRQ> httpEntity;
    @Mock
    private CommonService commonService;

    @Before
    public void setUp() throws Exception
    {
        userService.setApiClientUtils(apiClientUtils);
        when(threadContextService.getEnvironment()).thenReturn(DEV);
        when(serviceLocator.getServiceURI(any(), any())).thenReturn("");
        when(responseEntity.getBody()).thenReturn(new ServiceResult(true, "", "ABCD"));
        when(responseEntityForSelect.getBody()).thenReturn(new ServiceResult(true, "", "{}"));
        setupRestTemplate();
        setupCommonService();
    }

    private void setupCommonService()
    {
        when(commonService.getHttpEntityForSelect(any(SelectQueryRQ.class))).thenReturn(httpEntityForSelect);
        when(commonService.getHttpEntityForInsert(any(InsertQueryRQ.class))).thenReturn(httpEntity);
    }

    private void setupRestTemplate()
    {
        when(apiClientUtils.getRestTemplate()).thenReturn(restTemplate);
        when(restTemplate.postForEntity("/insert", httpEntity, ServiceResult.class)).thenReturn(responseEntity);
        when(restTemplate.postForEntity("/select", httpEntityForSelect, ServiceResult.class)).thenReturn(responseEntityForSelect);
    }

    @Test
    public void shouldCallServiceLocator() throws JSONException
    {
        //given
        CreateUserRQ createUserRQ = new CreateUserRQ("name", "email@email.com", "USER", "password");

        // when
        userService.createUser(createUserRQ);

        // then
        verify(serviceLocator, times(2)).getServiceURI(DATABASE, DEV);
    }

    @Test
    public void shouldPostForEntityUsingRestTemplate() throws JSONException
    {
        //given
        CreateUserRQ createUserRQ = new CreateUserRQ("name", "email@email.com", "USER", "password");

        // when
        userService.createUser(createUserRQ);

        // then
        verify(restTemplate).postForEntity("/insert", httpEntity, ServiceResult.class);
    }
}
