package br.com.lwbaleeiro.cdauth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "login_request")
public class LoginRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String deviceIdRequester;

    @Column(nullable = false)
    private LoginRequestStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant expiresAt;

    @Column
    private String deviceIdApprove;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
