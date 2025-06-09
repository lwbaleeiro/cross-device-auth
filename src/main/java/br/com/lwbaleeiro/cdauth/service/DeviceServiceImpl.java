package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.Device;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public boolean exists(String id, User user) {
        return deviceRepository.findByDeviceIdAndUser(id, user).isPresent();
    }

    @Override
    public Device update(String id, User user) {

        Device device = deviceRepository.findByDeviceIdAndUser(id, user).orElseThrow(
                () -> new RuntimeException("No device found for this Id and User."));

        device.setLastUsedAt(LocalDateTime.now());
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
                .lastUsedAt(LocalDateTime.now())
                .build();

        return deviceRepository.save(device);
    }
}
