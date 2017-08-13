package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class TestControllerTest
{
    private TestController testController;

    @Before
    public void setUp() throws Exception
    {
        testController = new TestController();
    }

    @Test
    public void shouldReturnSuccessfulResponse() throws Exception
    {
        // when
        ResponseEntity<ServiceResult> response = testController.testGet();

        // then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void testPost() throws Exception
    {
        // when
        ResponseEntity<ServiceResult> response = testController.testPost();

        // then
        assertEquals(OK, response.getStatusCode());
    }

}
