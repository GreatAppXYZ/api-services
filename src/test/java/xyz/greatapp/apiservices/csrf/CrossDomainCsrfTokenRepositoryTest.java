package xyz.greatapp.apiservices.csrf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.csrf.CsrfToken;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.context.ThreadContextService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CrossDomainCsrfTokenRepositoryTest
{
    private CrossDomainCsrfTokenRepository crossDomainCsrfTokenRepository;
    @Mock
    ThreadContextService threadContextService;
    @Mock
    private CsrfToken token;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Cookie cookie;

    @Before
    public void setUp() throws Exception
    {
        crossDomainCsrfTokenRepository = new CrossDomainCsrfTokenRepository(threadContextService) {
            @Override Cookie getCookie(String tokenValue)
            {
                return cookie;
            }
        };
    }

    @Test
    public void shouldSetDomainInProdEnv() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(Environment.PROD);

        //when
        crossDomainCsrfTokenRepository.saveToken(token, request, response);

        //then
        verify(cookie).setDomain("greatapp.xyz");
    }

    @Test
    public void shouldNotSetDomainInDevEnv() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(Environment.DEV);

        //when
        crossDomainCsrfTokenRepository.saveToken(token, request, response);

        //then
        verify(cookie, never()).setDomain("greatapp.xyz");
    }
}
