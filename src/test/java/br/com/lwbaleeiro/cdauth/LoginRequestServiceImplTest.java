package br.com.lwbaleeiro.cdauth;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.Device;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.LoginRequestRepository;
import br.com.lwbaleeiro.cdauth.service.DeviceService;
import br.com.lwbaleeiro.cdauth.service.LoginRequestCacheService;
import br.com.lwbaleeiro.cdauth.service.impl.LoginRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginRequestServiceImplTest {

    @InjectMocks
    private LoginRequestServiceImpl loginRequestService;

    @Mock
    private LoginRequestRepository loginRequestRepository;

    @Mock
    private DeviceService deviceService;

    @Mock
    private LoginRequestCacheService cacheService;

    @Test
    void shouldCreateLoginRequestSuccessfully() {

        String deviceId = "abc-123";
        String deviceName = "My Phone";
        UUID generatedId = UUID.randomUUID();

        when(loginRequestRepository.save(any(LoginRequest.class)))
                .thenAnswer(invocation -> {
                    LoginRequest request = invocation.getArgument(0);
                    request.setId(generatedId);
                    return request;
                });

        LoginRequestResponse result = loginRequestService.create(deviceId, deviceName);

        assertNotNull(result);
        assertEquals(generatedId, result.id());
        assertEquals(LoginRequestStatus.PENDING.name(), result.status());

        verify(cacheService).cacheLoginRequest(eq(generatedId), any(LoginRequest.class));
        verify(loginRequestRepository, times(1)).save(any(LoginRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDeviceIdIsNull() {

        String deviceIdRequester = null;
        String deviceName = "My Phone";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginRequestService.create(deviceIdRequester, deviceName);
        });

        assertEquals("deviceIdRequester cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectLoginRequestSuccessfully() {

        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        String deviceIdReject = "device-456";

        User user = new User();
        user.setId(userId);

        Device device = new Device();
        device.setId(deviceId);
        device.setDeviceId(deviceIdReject);
        device.setUser(user);

        // CORREÇÃO: Mock do deviceService ao invés do deviceRepository
        when(deviceService.getByDeviceIdAndUser(eq(deviceIdReject), eq(user)))
                .thenReturn(Optional.of(device));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setId(requestId);
        loginRequest.setStatus(LoginRequestStatus.PENDING);

        when(loginRequestRepository.findById(requestId)).thenReturn(Optional.of(loginRequest));
        when(loginRequestRepository.save(any(LoginRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LoginRequestResponse response = loginRequestService.loginReject(requestId, deviceIdReject, user);

        // Assert
        assertEquals(LoginRequestStatus.REJECTED, loginRequest.getStatus());
        assertEquals(deviceIdReject, loginRequest.getDeviceIdApprove());
        assertEquals(user, loginRequest.getUser());
        assertNotNull(loginRequest.getRejectedAt());
        assertNotNull(response);

        verify(loginRequestRepository).save(loginRequest);
        verify(cacheService).deleteLoginRequest(requestId);
        // ADICIONAL: Verificar se o deviceService foi chamado corretamente
        verify(deviceService).getByDeviceIdAndUser(deviceIdReject, user);
    }

    @Test
    void shouldApproveLoginRequestSuccessfully() {

        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        String deviceIdApprove = "device-123";

        User user = new User();
        user.setId(userId);

        Device device = new Device();

        device.setId(deviceId);
        device.setDeviceId(deviceIdApprove);
        device.setUser(user);

        // Mock do deviceService
        when(deviceService.getByDeviceIdAndUser(eq(deviceIdApprove), eq(user)))
                .thenReturn(Optional.of(device));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setDeviceIdRequester("device-999");
        loginRequest.setId(requestId);
        loginRequest.setStatus(LoginRequestStatus.PENDING);

        when(loginRequestRepository.findById(requestId)).thenReturn(Optional.of(loginRequest));
        when(loginRequestRepository.save(any(LoginRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LoginRequestResponse response = loginRequestService.loginApprove(requestId, deviceIdApprove, user);

        // Assert
        assertEquals(LoginRequestStatus.APPROVED, loginRequest.getStatus());
        assertEquals(deviceIdApprove, loginRequest.getDeviceIdApprove());
        assertEquals(user, loginRequest.getUser());
        assertNotNull(loginRequest.getApprovedAt());
        assertNotNull(response);

        verify(deviceService).getByDeviceIdAndUser(deviceIdApprove, user);
        verify(loginRequestRepository).save(loginRequest);
        verify(cacheService).deleteLoginRequest(requestId);
    }

}
