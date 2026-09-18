# Hotel Asset Recovery

Staff-first lost-and-found platform for hotels. Housekeeping logs found items, the front desk files loss reports on behalf of guests, and duty managers review matches.

This is a **modular monolith**: one Spring Boot application with enforced module boundaries, not a pile of microservices.

## Status

Phase 0 — repository baseline.

## Planned stack

- Java 21, Spring Boot 4, Spring Modulith, Spring Security (OAuth2 Resource Server)
- Angular staff app with Keycloak login
- PostgreSQL (PostGIS + pgvector), Kafka, MinIO, Redis
- OpenTelemetry, Prometheus, Grafana
- Docker Compose for the full local platform

## Local run (coming in Phase 1)

```bash
docker compose -f infra/docker-compose.yml up -d
```

## License

MIT
