package xyz.greatapp.apiservices.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.apiservices.services.FunctionalTestTransactionService;
import xyz.greatapp.libs.service.ServiceResult;

@RunWith(MockitoJUnitRunner.class)
public class AcceptanceTestTransactionControllerTest
{
    private AcceptanceTestTransactionController controller;
    @Mock
    private FunctionalTestTransactionService service;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception
    {
        controller = new AcceptanceTestTransactionController(service);
    }

    @Test
    public void shouldCallBeginTransactionInLocalTestEnv() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");

        // when
        controller.begin(request);

        // then
        verify(service).beginTransaction();
    }

    @Test
    public void beginShouldReturnErrorResponseIfServiceThrowsException() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");
        given(service.beginTransaction()).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = controller.begin(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void beginStatusResponseShouldBeOK() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");

        // when
        ResponseEntity<ServiceResult> responseEntity = controller.begin(request);

        // then
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    public void shouldCallRollbackTransactionInLocalTestEnv() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");

        // when
        controller.rollback(request);

        // then
        verify(service).rollbackTransaction();
    }

    @Test
    public void rollbackShouldReturnErrorResponseIfServiceThrowsException() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");
        given(service.rollbackTransaction()).willThrow(new RuntimeException());

        // when
        ResponseEntity<ServiceResult> response = controller.rollback(request);

        // then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void rollbackStatusResponseShouldBeOK() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("test.localhost");

        // when
        ResponseEntity<ServiceResult> responseEntity = controller.rollback(request);

        // then
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    public void shouldNotCallBeginTransactionDevEnv() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("localhost");

        // when
        controller.begin(request);

        // then
        verify(service, never()).beginTransaction();
    }

    @Test
    public void shouldCallRollbackTransactionInDevEnv() throws Exception
    {
        // given
        given(request.getServerName()).willReturn("localhost");

        // when
        controller.rollback(request);

        // then
        verify(service, never()).rollbackTransaction();
    }

}
