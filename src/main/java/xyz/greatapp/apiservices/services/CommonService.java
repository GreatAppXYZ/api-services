package xyz.greatapp.apiservices.services;

import java.util.Calendar;
import java.util.LinkedHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@Component
class CommonService
{
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    String getAuthenticatedUserId()
    {
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication.getUserAuthentication();
        LinkedHashMap details = (LinkedHashMap) token.getDetails();
        LinkedHashMap principal = (LinkedHashMap) details.get("principal");
        return principal.get("userId").toString();
    }

    ServiceResult isValidDate(int year, int month, int day)
    {
        if (isInvalidDate(year, month, day))
            return new ServiceResult(false, "invalid.date");

        if (isFutureDate(year, month, day))
        {
            return new ServiceResult(false, "invalid.future.date");
        }

        return new ServiceResult(true, "");
    }

    private boolean isInvalidDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(year, month - 1, day);
        try
        {
            calendar.getTime();
        }
        catch (Exception e)
        {
            return true;
        }
        return false;
    }

    private boolean isFutureDate(int year, int month, int day)
    {
        Calendar dateFromRequest = Calendar.getInstance();
        dateFromRequest.set(year, month - 1, day);
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        return now.before(dateFromRequest);
    }

    String getEncryptedString(String str)
    {
        return new BCryptPasswordEncoder().encode(str);
    }

    HttpEntity<SelectQueryRQ> getHttpEntityForSelect(SelectQueryRQ selectQueryRQ)
    {
        return new HttpEntity<>(selectQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<InsertQueryRQ> getHttpEntityForInsert(InsertQueryRQ insertQueryRQ)
    {
        return new HttpEntity<>(insertQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<UpdateQueryRQ> getHttpEntityForUpdate(UpdateQueryRQ updateQueryRQ)
    {
        return new HttpEntity<>(updateQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<DeleteQueryRQ> getHttpEntityForDelete(DeleteQueryRQ deleteQueryRQ)
    {
        return new HttpEntity<>(deleteQueryRQ, apiClientUtils.getHttpHeaders());
    }

    void setApiClientUtils(ApiClientUtils apiClientUtils)
    {
        this.apiClientUtils = apiClientUtils;
    }
}
