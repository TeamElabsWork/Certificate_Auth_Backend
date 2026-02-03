# Certificate Auth Backend

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-purple.svg)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Security](https://img.shields.io/badge/Security-Dual%20Token-red.svg)](https://spring.io/projects/spring-security)

## 📖 Project Overview

**Certificate Auth Backend** is a high-security, scalable REST API engineered for **controlled digital certificate issuance**. Unlike standard generic backends, this system implements a strict **hierarchical trust model** and a **stateless-yet-revocable** security architecture.

It is designed to solve two core problems:
1.  **Trust Chain Management**: Preventing unauthorized users from issuing certificates by enforcing an **Invite-Only / Organization-Based** signup flow.
2.  **Secure Verification**: Providing public, obfuscated verification links for certificates without exposing internal database IDs.

## 🏗️ Architectural Deep Dive

This project tackles complexity through specific architectural patterns:

### 1. Hierarchical Trust & Invite-Only Flow
The system does *not* allow open signups. Access is controlled via a strict hierarchy:
*   **Super Admin** creates an **Organisation**.
*   **Super Admin** generates a unique, one-time `Auth Code` for that Organisation.
*   **Issuers (Users)** can only sign up if they possess a valid `Auth Code`.
*   This ensures that every registered user is visibly tied to a verified Organisation, preventing fraudulent certificate issuance.

### 2. Dual-Token Security Architecture (Stateless + Revocable)
To balance performance (statelessness) with security (control), we implement a hybrid auth model:
*   **Access Token (JWT)**: Short-lived (~15 min), stateless. Used for ensuring high-performance API access without database lookups for every request.
*   **Refresh Token (Opaque/Hashed)**: Long-lived, stored in the **Database** (hashed) and linked to the user.
    *   **Why?**: If an Access Token is stolen, it expires quickly. If a Refresh Token is stolen, the user/admin can **revoke** it (delete from DB), instantly locking out the attacker.
    *   **Implementation**: We use SHA-256 hashing for storing refresh tokens, ensuring that even a database leak does not expose active user sessions.

### 3. ID Obfuscation via Hashids
Public verification URLs (`/api/v1/certificate/verify/{token}`) DO NOT expose primary key IDs (e.g., `1`, `2`, `3`).
*   **Problem**: Sequential IDs allow attackers to "scrape" all certificates by simply incrementing numbers.
*   **Solution**: We use **Hashids** with a custom salt to encode internal IDs into short, unique, non-sequential strings (e.g., `X9k2Ma`). This makes the verification URLs "guess-proof."

---

## 🚀 Features

-   **Admin Module**: Organization management and invite code generation.
-   **Secure Authentication**:
    -   Signup (Token-gated).
    -   Login (Returns Access + Refresh tokens).
    -   Token Refresh (Rotates tokens for security).
-   **Certificate Lifecycle**:
    -   Issuance (Linked to authenticated Issuer).
    -   Verification (Public, unauthenticated endpoint).
    -   Retrieval (My Certificates / All Certificates).
-   **Database**: PostgreSQL with JPA and fully transactional services.

---

## 🛠️ Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/) (JVM) - Chosen for null-safety and conciseness.
-   **Framework**: [Spring Boot 3.4](https://spring.io/projects/spring-boot) - The backbone of the application.
-   **Persistence**: Spring Data JPA + [PostgreSQL](https://www.postgresql.org/).
-   **Security**: Spring Security + `jjwt` (Java JWT) + `BCrypt`.
-   **Utilities**: `Hashids` (ID obfuscation).
-   **Build Tool**: Gradle (Kotlin DSL).

---

## ⚙️ Setup & Configuration

### Prerequisites
*   **Java 17+**
*   **PostgreSQL** (running locally or cloud)

### Environment Variables
Configure these in your IDE or environment:

| Variable | Use Case | Example |
| :--- | :--- | :--- |
| `NEON_DB_URL` | DB Connection String | `jdbc:postgresql://localhost:5432/cert_db` |
| `NEON_DB_USERNAME` | DB User | `postgres` |
| `NEON_DB_PASSWORD` | DB Password | `password` |
| `HASH_SALT` | Salt for Hashids | `MySecretSalt_Change_This` |
| `JWT_SECRET_BASE64` | Secret for signing JWTs | `(Base64 Encoded String)` |
| `PORT` | Server Port (Optional) | `8080` |

---

## 🔌 Complete API Reference

### 1. Admin Module (`/api/v1/admin`)
*Used by system administrators to onboard organizations.*

| Method | Endpoint | Description | Payload / Params | Response |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/organisations/add` | onboard a new Organization | `{ "name": "Google", "domain": "tech" }` | Organisation Object |
| **GET** | `/authCode/generate/{id}` | Generate an invite code for an specific Org | Path Param: `id` (e.g., `1`) | `String` (The Code) |
| **GET** | `/organisations` | List all organizations | - | `List<Organisation>` |

### 2. Authentication Module (`/api/v1/auth`)
*Used by Issuers to register and log in.*

| Method | Endpoint | Description | Payload | Response |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/signup` | Register new user **(Requires Invite Code)** | `{ "name": "...", "userEmail": "...", "userPassword": "...", "signupToken": "<code>" }` | `{ "name": "...", "email": "..." }` |
| **POST** | `/login` | Authenticate & get tokens | `{ "userEmail": "...", "userPassword": "..." }` | `{ "accessToken": "...", "refreshToken": "..." }` |
| **POST** | `/refresh` | Rotate expired Access Token | `{ "refreshToken": "..." }` | `{ "accessToken": "...", "refreshToken": "..." }` |

### 3. Certificate Module (`/api/v1/certificate`)
*Used for issuing and verifying certificates.*

| Method | Endpoint | Auth Required? | Description | Payload |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/create` | ✅ YES | Issue a new certificate | `{ "name": "John Doe", "teamName": "Team A", "eventName": "Hackathon", "rollNumber": "123" }` |
| **GET** | `/verify/{token}` | ❌ NO | **Public verification** | Path Param: `token` (e.g., `Wa9k3`) |
| **GET** | `/` | ✅ YES | Get certs issued by **me** | - |
| **GET** | `/all` | ✅ YES | Get **all** certs in system | - |

---

## 🤝 Contributing

This project creates a secure backbone for certificate systems. Improvements to the **Admin Dashboard** or **Email Notification Service** are welcome!

1.  Fork the repo.
2.  Create your feature branch (`git checkout -b feature/amazing-feature`).
3.  Commit your changes.
4.  Push to the branch.
5.  Open a Pull Request.
