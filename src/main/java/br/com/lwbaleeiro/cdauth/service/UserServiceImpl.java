package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.config.JwtService;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            // TODO: Mudar para custom exception
            throw new RuntimeException("This e-mail is already been taken");
        }

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    @Override
    public String authentication(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        // TODO: Mudar para custom exception
        var user = userRepository.findByEmail(email).orElseThrow();
        return jwtService.generateToken(user);
    }
}
