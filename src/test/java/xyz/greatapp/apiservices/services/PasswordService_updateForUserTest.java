package xyz.greatapp.apiservices.services;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.apiservices.requests.password.UpdatePasswordForUserRQ;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class PasswordService_updateForUserTest
{
    @InjectMocks
    private PasswordService passwordService;
    @Mock
    private CommonService commonService;
    @Mock
    private ApiClientUtils apiClientUtils;
    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private HttpEntity<SelectQueryRQ> httpEntityForSelect;
    @Mock
    private ResponseEntity<ServiceResult> responseEntityForSelect;
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception
    {
        when(serviceLocator.getServiceURI(any(), any())).thenReturn("");
        passwordService.setApiClientUtils(apiClientUtils);
        when(commonService.getHttpEntityForSelect(any(SelectQueryRQ.class))).thenReturn(httpEntityForSelect);
        when(apiClientUtils.getRestTemplate()).thenReturn(restTemplate);
        when(responseEntityForSelect.getBody()).thenReturn(new ServiceResult(true, "", "{\"password\": \"\"}"));
        when(restTemplate.postForEntity("/select", httpEntityForSelect, ServiceResult.class)).thenReturn(responseEntityForSelect);
    }

    @Test
    public void shouldGetCurrentAuthenticatedUser() throws Exception
    {
        // given
        UpdatePasswordForUserRQ request = new UpdatePasswordForUserRQ("currentPassword", "userId", "newPassword");

        // when
        passwordService.updateForUser(request);

        // then
        verify(commonService).getAuthenticatedUserId();
    }

}
