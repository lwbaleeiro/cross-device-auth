# Multi-Device QR Code Authentication System

This project is a multi-device authentication system inspired by the login flow of apps like Spotify and WhatsApp Web. It allows users to authenticate using QR Codes and manage multiple active devices simultaneously.

## Key Features

- QR Code-based login flow between trusted devices
- Multi-device session management (login, logout, approval)
- JWT-based stateless authentication
- Device recognition and activity tracking (`lastUsedAt`)
- Spring Boot, Java 21, Redis, WebSocket support
- Clean architecture using Hexagonal (Ports and Adapters)

## Tech Stack

- Java 21 + Spring Boot 3
- Redis (for temporary storage of login requests and device session state)
- WebSocket (for real-time login notifications)
- JWT (JSON Web Tokens for authentication)
- PostgreSQL (or another SQL DB for user/device persistence)

## Architecture

- **Hexagonal Architecture (Ports and Adapters):**
    - Core domain logic is isolated from infrastructure
    - Easy to test and extend (e.g., swap JWT provider, or add device limits)

- **Modules:**
    - `User`: Registration, login, and device association
    - `Device`: Tracks devices per user with `deviceId`, `lastUsedAt`
    - `Auth`: Handles JWT issuance and validation
    - `LoginRequest`: Temporary state when a device requests login approval
    - `WebSocket`: Used to notify logged-in devices of new login requests

## How it Works

1. **Register or Login from Device A**
    - User logs in from a primary device (e.g., smartphone)
    - A JWT is issued and a `Device` entry is stored

2. **Device B Attempts Login**
    - It sends a login request and receives a `LoginRequest` ID and a QR Code
    - The QR Code is scanned by Device A (already authenticated)

3. **Device A Approves the Login**
    - Device A sends an approval request
    - If approved, Device B receives a JWT and is logged in

4. **All Devices Have Independent JWTs**
    - Each device is treated as a separate authenticated client
    - Devices can be tracked, listed, or invalidated independently

## Running Locally

### Prerequisites

- Java 21+
- Maven or Gradle
- Docker (for Redis/PostgreSQL)

### Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/qr-multidevice-auth.git
cd qr-multidevice-auth
``` 

2. Configure .env or application.yml with your JWT secret, DB credentials, etc.

3. Start Redis (e.g., via Docker):
```dash
docker run --name redis -p 6379:6379 redis
```
   
4. Run the Spring Boot application:

### Folder Structure

```pgsql
    src/
    ├── config      # Spring Security & App configuration
    ├── controller
    ├── dto
    ├── entity
    ├── helpers     # JWT generation & validation
    ├── repository
    ├── service
    ├── websocket   # WebSocket controller for real-time device updates
```

### Security
- JWT expiration and signature verification
- Device-aware token management
- Stateless login validation
- Optional refresh tokens (not yet implemented)

### Future Improvements
- Refresh tokens and session expiration
- Device management UI (revoke, rename)
- Notifications (e.g., push or email on login)
- Rate limiting and brute-force protection
- Logging and observability with Actuator & Prometheus