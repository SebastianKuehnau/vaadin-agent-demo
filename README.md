# Vaadin Agent Demo — `baseline`

The common starting point of every measured branch. **The `main` branch README
compares them all** — start there for the overview.

- Vaadin 25.3.6, Spring Boot 4.2.0, Java 25, Maven 3.9.16 (wrapper 3.3.4).
- `base/ui/MainLayout.java` builds the side navigation from `@Menu`, so every branch
  starts from the same layout when the prompt says "add the view to the navigation".
- No views, no agent configuration (`.claude/`, `.mcp.json`, `.agents/`, `.vaadin/`).
  `/` shows Vaadin's "No views found" page.
- One test: `ApplicationTest`, a Spring context smoke test.

Each benchmark branch is `baseline` plus its own configuration (setup commit) plus
the agent's output (result commit).

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```
