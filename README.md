# financial-api

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=flat-square&logo=spring)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![Pluggy](https://img.shields.io/badge/Pluggy-Open%20Finance-7B5EA7?style=flat-square)

REST backend for the personal financial control application. Responsible for business logic, data persistence, and integration with the Pluggy API (Open Finance).

---

## Responsibilities

This API is not just a CRUD layer. It acts as the intelligence between raw banking data arriving from Pluggy and the organized information the frontend consumes:

- **Authenticate** with the Pluggy API to obtain a temporary access token.
- **Import bank accounts** from a Pluggy Item (connection) and persist them locally.
- **Sync transactions** per imported account, applying deduplication via `pluggyTransactionId`.
- **Categorize transactions** automatically based on description and account type.
- **Expose REST endpoints** for the frontend: dashboard summary, period-filtered transactions, category CRUD.

---

## Architecture

The application follows a classic Spring layered architecture:

```
controller/         <- Receives HTTP requests, validates and delegates
    `-- dto/        <- Response objects (JPA entities are never exposed directly)
service/            <- Business logic: import, sync, categorization
    `-- dto/        <- External data mapping (Pluggy API responses)
domain/             <- JPA entities: Account, Transaction, Category
repository/         <- Spring Data JPA interfaces for database access
config/             <- CORS and RestClient configuration
```

### Pluggy Integration

Pluggy is an **Open Finance** platform that centralizes access to data from multiple Brazilian financial institutions. The authentication flow works as follows:

1. Before any Pluggy operation, `PluggyAuthService` exchanges the `clientId` and `clientSecret` for a **temporary API Key** via `POST /auth`.
2. That token is then sent in the `X-API-KEY` header on every subsequent request.
3. Accounts are fetched via `GET /accounts?itemId={itemId}`, where `itemId` is the identifier of the banking connection created by the user in the Pluggy Connect widget.
4. Transactions are fetched via `GET /transactions?accountId={pluggyAccountId}`.

> **Why not OAuth?** Pluggy uses a stateless API Key model, refreshed per request. This simplifies the integration since there is no refresh token or session state to manage.

---

## Prerequisites

| Tool | Minimum version | Purpose |
|---|---|---|
| **Java (JDK)** | 21 | Compile and run the application |
| **Maven** | Included via `./mvnw` | Manage dependencies and build |
| **Docker** | Any recent version | Run PostgreSQL in a container |
| **Pluggy account** | — | Obtain `clientId` and `clientSecret` |

---

## Environment Variables

Create a `.env` file at the root of the `api/` folder:

```env
# Pluggy credentials (obtain at https://dashboard.pluggy.ai)
PLUGGY_CLIENT_ID=your_client_id_here
PLUGGY_CLIENT_SECRET=your_client_secret_here

# Origins allowed by CORS (frontend address)
ALLOWED_ORIGINS=http://localhost:3000
```

> Database variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`) have defaults defined in `application.yml` matching the Docker container credentials. Only override them if using an external database (e.g. Neon.tech, Supabase).

The relevant `application.yml` configuration:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/finance_dev}
    username: ${DB_USER:dev_user}
    password: ${DB_PASSWORD:dev_password}

pluggy:
  api-url: https://api.pluggy.ai
  client-id: ${PLUGGY_CLIENT_ID}
  client-secret: ${PLUGGY_CLIENT_SECRET}

app:
  allowed-origins: ${ALLOWED_ORIGINS}
```

---

## Running the Application

Spring Boot is configured with the `spring-boot-docker-compose` dependency, which **automatically starts the PostgreSQL container** when the application boots. No need to run `docker compose up` manually.

```bash
# From the api/ folder
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## API Endpoints

| Method | Route | Description |
|---|---|---|
| `GET` | `/api/accounts` | List all imported accounts |
| `POST` | `/api/accounts/import/{itemId}` | Import accounts from a Pluggy Item |
| `GET` | `/api/transactions` | List transactions filtered by `startDate` and `endDate` |
| `POST` | `/api/transactions/sync/{pluggyAccountId}` | Sync transactions for an account with Pluggy |
| `PATCH` | `/api/transactions/{id}/category` | Update the category of a transaction |
| `GET` | `/api/dashboard/summary` | Return income, expense, and balance summary |
| `GET` | `/api/categories` | List transaction categories |
| `POST` | `/api/categories` | Create a new category |
| `PUT` | `/api/categories/{id}` | Update an existing category |
| `DELETE` | `/api/categories/{id}` | Delete a category |

---

## Database

The schema is managed by **Hibernate** with `ddl-auto: update`. Tables are created or updated automatically on first run based on JPA entities — no manual SQL scripts required.

Core tables:

- **`accounts`** — bank accounts imported from Pluggy
- **`transactions`** — synced transactions, deduplicated by `pluggy_transaction_id`
- **`categories`** — user-defined categories for classifying transactions
