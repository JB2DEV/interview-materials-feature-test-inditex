package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.rest;

import com.interview.materials.feature.test.inditex.application.command.LoginCommand;
import com.interview.materials.feature.test.inditex.application.command.LoginResponse;
import com.interview.materials.feature.test.inditex.application.port.in.service.LoginServicePort;
import com.interview.materials.feature.test.inditex.infrastructure.mapper.AppUserMapper;
import com.interview.materials.feature.test.inditex.infrastructure.web.dto.LoginRequest;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@WebFluxTest(
        controllers = LoginRestControllerAdapter.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class
        }
)class LoginRestControllerAdapterIT {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoginServicePort loginServicePort;

    @MockBean
    private AppUserMapper appUserMapper;

    private static MockedStatic<TraceIdHolder> traceIdHolderMock;

    @BeforeAll
    static void beforeAll() {
        traceIdHolderMock = mockStatic(TraceIdHolder.class);
        traceIdHolderMock.when(TraceIdHolder::getTraceId)
                .thenReturn(Mono.just("TRACE_ID"));
    }

    @AfterAll
    static void afterAll() {
        traceIdHolderMock.close();
    }

    @Test
    void login_WithValidCredentials_ReturnsOkAndBody() {
        LoginRequest dto = new LoginRequest("admin", "admin123");
        LoginCommand cmd = new LoginCommand("admin", "admin123");
        LoginResponse resp = new LoginResponse("jwt-token-xyz");

        when(appUserMapper.toCommand(dto)).thenReturn(cmd);
        when(loginServicePort.login(cmd)).thenReturn(Mono.just(resp));

        webTestClient.post()
                .uri("/api/mgmt/1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoginResponse.class)
                .value(r -> {
                    assert r.token().equals("jwt-token-xyz");
                });

        verify(appUserMapper).toCommand(dto);
        verify(loginServicePort).login(cmd);
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() {
        LoginRequest dto = new LoginRequest("foo", "bar");
        LoginCommand cmd = new LoginCommand("foo", "bar");

        when(appUserMapper.toCommand(dto)).thenReturn(cmd);
        when(loginServicePort.login(cmd)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/mgmt/1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(UNAUTHORIZED);

        verify(appUserMapper).toCommand(dto);
        verify(loginServicePort).login(cmd);
    }
}
