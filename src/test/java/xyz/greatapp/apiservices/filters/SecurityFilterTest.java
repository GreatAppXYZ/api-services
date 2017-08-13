package xyz.greatapp.apiservices.filters;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityFilterTest
{
    private SecurityFilter securityFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    @Before
    public void setUp() throws Exception
    {
        securityFilter = new SecurityFilter();
        when(request.getHeader("origin")).thenReturn("http://localhost");
        when(request.getMethod()).thenReturn("POST");
    }

    @Test
    public void shouldSetCORSHeadersForValidOrigin() throws Exception
    {
        // when
        securityFilter.doFilter(request, response, chain);

        // then
        verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,x-xsrf-token");
        verify(response).setHeader("Access-Control-Max-Age", "360");
        verify(response).setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
    }
}
