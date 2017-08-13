package xyz.greatapp.apiservices.csrf;

import static xyz.greatapp.libs.service.Environment.PROD;
import static xyz.greatapp.libs.service.Environment.UAT;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import xyz.greatapp.libs.service.context.ThreadContextService;

public class CrossDomainCsrfTokenRepository implements CsrfTokenRepository
{
    private static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
    private static final String CSRF_QUERY_PARAM_NAME = "_csrf";
    private final ThreadContextService threadContextService;

    private final CookieCsrfTokenRepository delegate = new CookieCsrfTokenRepository();

    public CrossDomainCsrfTokenRepository(ThreadContextService threadContextService)
    {
        delegate.setCookieHttpOnly(true);
        delegate.setCookieName(XSRF_TOKEN_COOKIE_NAME);
        delegate.setParameterName(CSRF_QUERY_PARAM_NAME);
        this.threadContextService = threadContextService;
    }

    @Override
    public CsrfToken generateToken(final HttpServletRequest request)
    {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(final CsrfToken token, final HttpServletRequest request, final HttpServletResponse response)
    {
        String tokenValue = token == null ? "" : token.getToken();
        Cookie cookie = getCookie(tokenValue);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        if (token == null) {
            cookie.setMaxAge(0);
        }
        else {
            cookie.setMaxAge(-1);
        }
        cookie.setHttpOnly(false);
        if (threadContextService.getEnvironment() == PROD || threadContextService.getEnvironment() == UAT)
        {
            cookie.setDomain("greatapp.xyz");
        }
        response.addCookie(cookie);
    }

    Cookie getCookie(String tokenValue)
    {
        return new Cookie(XSRF_TOKEN_COOKIE_NAME, tokenValue);
    }

    @Override
    public CsrfToken loadToken(final HttpServletRequest request)
    {
        return delegate.loadToken(request);
    }
}
