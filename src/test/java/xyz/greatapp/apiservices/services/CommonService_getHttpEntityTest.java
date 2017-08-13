package xyz.greatapp.apiservices.services;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class CommonService_getHttpEntityTest
{
    private CommonService commonService;
    @Mock
    private ApiClientUtils apiClientUtils;

    @Before
    public void setUp() throws Exception
    {
        commonService = new CommonService();
        commonService.setApiClientUtils(apiClientUtils);
    }

    @Test
    public void getHttpEntityForSelectShouldGetHttpHeaders() throws Exception
    {
        //when
        commonService.getHttpEntityForSelect(new SelectQueryRQ("table", new ColumnValue[0]));

        //then
        verify(apiClientUtils).getHttpHeaders();
    }

    @Test
    public void getHttpEntityForInsertShouldGetHttpHeaders() throws Exception
    {
        //when
        commonService.getHttpEntityForInsert(new InsertQueryRQ("table", new ColumnValue[0], "idColumnName"));

        //then
        verify(apiClientUtils).getHttpHeaders();
    }

    @Test
    public void getHttpEntityForUpdateShouldGetHttpHeaders() throws Exception
    {
        //when
        commonService.getHttpEntityForUpdate(new UpdateQueryRQ("table", new ColumnValue[0], new ColumnValue[0]));

        //then
        verify(apiClientUtils).getHttpHeaders();
    }

    @Test
    public void getHttpEntityForDeleteShouldGetHttpHeaders() throws Exception
    {
        //when
        commonService.getHttpEntityForDelete(new DeleteQueryRQ("table", new ColumnValue[0]));

        //then
        verify(apiClientUtils).getHttpHeaders();
    }
}
