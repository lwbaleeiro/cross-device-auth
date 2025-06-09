package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.config.JwtService;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DeviceService deviceService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.deviceService = deviceService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String register(User user, String deviceId, String deviceName) {

        if (userRepository.existsByEmail(user.getEmail())) {
            // TODO: Mudar para custom exception
            throw new RuntimeException("This e-mail is already been taken");
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setEnabled(true);

        userRepository.save(user);

        createOrUpdateDevice(deviceId, deviceName, user);

        return jwtService.generateToken(user, user.getId(), deviceId);
    }

    @Override
    public String authentication(String email, String password, String deviceId, String deviceName) {
        // TODO: Mudar para custom exception
        var user = userRepository.findByEmail(email).orElseThrow();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        createOrUpdateDevice(deviceId, deviceName, user);
        return jwtService.generateToken(user, user.getId(), deviceId);
    }

    private void createOrUpdateDevice(String deviceId, String deviceName, User user) {
        if (deviceService.exists(deviceId, user)) {
            deviceService.update(deviceId, user);
        } else {
            deviceService.create(deviceId, deviceName, user);
        }
    }

}
