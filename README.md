# PantryPulse Backend (Java Spring Boot)

- Spring Boot 3 (Java 17) with Gradle (Kotlin DSL)
- JWT auth (`/api/auth/login`), protected endpoints under `/api/**`
- PostgreSQL + Redis via docker-compose
- Entities: Site, Status, InventoryItem (simplified)
- Swagger UI at `/swagger-ui.html`

## Run locally
```bash
docker compose up --build
```
