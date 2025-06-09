package br.com.lwbaleeiro.cdauth.repository;

import br.com.lwbaleeiro.cdauth.entity.Device;
import br.com.lwbaleeiro.cdauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByDeviceIdAndUser(String id, User user);
}
