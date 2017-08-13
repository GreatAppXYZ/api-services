package xyz.greatapp.apiservices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import xyz.greatapp.apiservices.csrf.CrossDomainCsrfTokenRepository;
import xyz.greatapp.apiservices.filters.SecurityFilter;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.context.ThreadContextServiceImpl;
import xyz.greatapp.libs.service.filters.ContextFilter;
import xyz.greatapp.libs.service.location.ServiceLocator;

@Configuration
@EnableWebSecurity
public class AppConfiguration extends ResourceServerConfigurerAdapter
{
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/test").permitAll()
                .antMatchers("/registerUser").permitAll()
                .antMatchers("/acceptance_test/transaction/**").permitAll()
                .antMatchers("/user/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/password/updateForUser").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/joggingTime/getByUser").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/joggingTime/createForUser").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/joggingTime/updateForUser").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/joggingTime/deleteForUser").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/**").authenticated()
                .and()
                .csrf()
                .ignoringAntMatchers("/test")
                .csrfTokenRepository(new CrossDomainCsrfTokenRepository(getThreadContextService()));
    }

    @Bean
    public ThreadContextService getThreadContextService()
    {
        return new ThreadContextServiceImpl();
    }

    @Bean
    public ServiceLocator getServiceLocator()
    {
        return new ServiceLocator();
    }

    @Bean
    public SecurityFilter getSecurityFilter()
    {
        return new SecurityFilter();
    }

    @Bean
    public ContextFilter getContextFilter(ThreadContextService threadContextService)
    {
        return new ContextFilter(threadContextService);
    }
}
