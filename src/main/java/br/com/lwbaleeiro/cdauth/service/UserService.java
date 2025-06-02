package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.User;

public interface UserService {

    String register(User user);
    String authentication(String email, String password);
}
