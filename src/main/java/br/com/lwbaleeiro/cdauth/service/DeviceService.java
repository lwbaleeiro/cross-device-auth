package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.Device;
import br.com.lwbaleeiro.cdauth.entity.User;

import java.util.Optional;

public interface DeviceService {
    boolean exists(String id, User user);
    Device update(String id, User user);
    Device create(String id, String name, User user);
    void updateLastUsedAt(String deviceId, String userEmail);
    Optional<Device> getByDeviceIdAndUser(String deviceIdApprove, User user);
}
