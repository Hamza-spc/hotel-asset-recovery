# Hotel Asset Recovery

Staff-first lost-and-found platform for hotels. Housekeeping logs found items, the front desk files loss reports on behalf of guests, and duty managers review matches.

This is a **modular monolith**: one Spring Boot application with enforced module boundaries, not a pile of microservices.

**Repository:** [github.com/Hamza-spc/hotel-asset-recovery](https://github.com/Hamza-spc/hotel-asset-recovery)

## Status

Phase 1 — platform skeleton. Login works. Inventory, maps, Kafka consumers, and matching come next.

## Stack

- Java 21, Spring Boot 4.1, Spring Modulith 2.1
- Angular 21 staff app
- Keycloak (OAuth2/OIDC) with roles `HOUSEKEEPING`, `FRONT_DESK`, `DUTY_MANAGER`
- Docker Compose: PostgreSQL 16, Kafka, Redis, MinIO, Prometheus, Grafana, Nginx. PostGIS is added in Phase 3.

## Local run

Use Java 21. Homebrew Maven may pick a newer JDK; point `JAVA_HOME` at JDK 21.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

docker compose -f infra/docker-compose.yml up -d

cd backend && ./mvnw spring-boot:run
cd frontend && npm start
```

Open [http://localhost:4200](http://localhost:4200). Keycloak is on port 8081, Grafana on 3000 (`admin` / `admin`), MinIO console on 9001. Compose Postgres is on **5433** so it does not collide with a local Homebrew Postgres on 5432.

### Demo users (local only)

| Username     | Password     | Role           |
| ------------ | ------------ | -------------- |
| housekeeper  | housekeeper  | HOUSEKEEPING   |
| frontdesk    | frontdesk    | FRONT_DESK     |
| manager      | manager      | DUTY_MANAGER   |

Keycloak admin console: `admin` / `admin`.

## Modules

`identity` · `inventory` · `claims` · `location` · `matching` · `notification` · `audit`

Boundaries are verified by `ModularityTests`. Other modules may only use a module's public types or listen to its events.

## Layout

```
backend/   Spring Boot API
frontend/   Angular staff UI
infra/      Compose, Keycloak realm, Nginx, Prometheus, Grafana
```

## License

MIT
