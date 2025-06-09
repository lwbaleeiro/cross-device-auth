package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.Device;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.DeviceRepository;
import br.com.lwbaleeiro.cdauth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean exists(String id, User user) {

        Optional<Device> device = deviceRepository.findByDeviceIdAndUser(id, user);

        return device.isPresent() && device.get().isActive();
    }

    @Override
    public Device update(String id, User user) {

        Device device = deviceRepository.findByDeviceIdAndUser(id, user).orElseThrow(
                () -> new EntityNotFoundException("No device found for this Id and User."));

        device.setLastUsedAt(Instant.now());
        device.setActive(true);

        return deviceRepository.save(device);
    }

    @Override
    public Device create(String id, String name, User user) {

        if (deviceRepository.findByDeviceIdAndUser(id, user).isPresent()) {
            // TODO: Mudar para custom exception
            throw new RuntimeException("This device already exists with this user.");
        }

        Device device = Device.builder()
                .deviceName(name)
                .deviceId(id)
                .active(true)
                .user(user)
                .lastUsedAt(Instant.now())
                .build();

        return deviceRepository.save(device);
    }

    @Override
    public void updateLastUsedAt(String deviceId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User not found by given e-mail."));

        Device device = deviceRepository.findByDeviceIdAndUser(deviceId, user).orElseThrow(
                () -> new EntityNotFoundException("No device found for this Id and User."));

        device.setLastUsedAt(Instant.now());
        deviceRepository.save(device);
        log.debug("Updating lastUsedAt for deviceId: {} and user: {}", deviceId, userEmail);
    }
}
