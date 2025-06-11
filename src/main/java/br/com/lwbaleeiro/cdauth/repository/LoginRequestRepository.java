package br.com.lwbaleeiro.cdauth.repository;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginRequestRepository extends JpaRepository<LoginRequest, UUID> {
}
