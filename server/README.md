# Gainful Server

Ktor backend for the Gainful stock tracking app.

## Tech Stack

- Kotlin + Ktor (Netty engine)
- Exposed 1.3.1 (DSL) + PostgreSQL
- Koin (DI), JWT (auth), `kotlin.uuid.Uuid` (IDs)
- H2 for testing, PostgreSQL for production
- Swagger UI at `/swagger`

## Quick Start

```bash
# Create database
createdb gainful

# Run server
./gradlew :server:run

# Seed mock data
./server/seed.sh
```

Server starts at `http://localhost:8080`.

## Environment Variables

All config lives in `application.conf`, overridable via env vars:

| Variable   | Default                                | Description          |
|------------|----------------------------------------|----------------------|
| `PORT`     | `8080`                                 | Server port          |
| `DB_URL`   | `jdbc:postgresql://localhost:5432/gainful` | PostgreSQL URL   |
| `DB_USER`  | system username (`$USER`)              | Database user        |
| `DB_PASSWORD` | `""`                               | Database password    |
| `JWT_SECRET` | `gainful-dev-secret-key-...`         | JWT signing secret   |
| `UPLOAD_DIR` | `data/uploads/avatars`              | Avatar storage path  |

## API

All endpoints are prefixed with `/api`. Auth endpoints are public; others require `Authorization: Bearer <token>`.

### Auth
- `POST /api/auth/register` — Register
- `POST /api/auth/login` — Login (returns JWT)

### User
- `GET /api/users/me` — Get profile
- `PUT /api/users/me` — Update profile
- `POST /api/users/avatar` — Upload avatar (multipart)
- `GET /api/users/sessions` — List sessions
- `DELETE /api/users/sessions` — Revoke other sessions

### Transactions
- `GET /api/transactions` — List all
- `POST /api/transactions` — Create
- `DELETE /api/transactions/{id}` — Delete

### Static Files
- `GET /avatars/{filename}` — Avatar file access

### Docs
- Swagger UI: `http://localhost:8080/swagger`
- OpenAPI spec: `http://localhost:8080/openapi/documentation.yaml`

## Project Structure

```
server/
├── src/main/kotlin/com/yoke/gainful/server/
│   ├── Application.kt          # Entry point (module())
│   ├── config/                 # AppConfig, DatabaseFactory, KoinModule
│   ├── db/                     # Exposed table definitions (Users, Transactions, UserSessions)
│   ├── model/dto/              # Request/Response DTOs
│   ├── plugins/                # Ktor plugins (Security, Routing, Serialization, StatusPages)
│   ├── routes/                 # Route handlers (AuthRoutes, UserRoutes, TransactionRoutes)
│   ├── security/token/         # TokenConfig, TokenClaim, TokenService, JwtTokenService
│   ├── service/                # Business logic (Auth, User, Session, Transaction, Avatar)
│   └── util/                   # PasswordUtils
├── src/main/resources/
│   ├── application.conf        # HOCON config (database, jwt, upload)
│   └── openapi/documentation.yaml
└── src/test/                   # Unit tests (service + route tests with H2)
```

## Testing

```bash
./gradlew :server:test
```
