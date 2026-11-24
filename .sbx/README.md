# Docker sandbox kit

`kit/spec.yaml` is a [Docker Sandboxes](https://docs.docker.com/ai/sandboxes/) kit
(`schemaVersion: "2"`, `kind: mixin`) that runs Claude Code in a container behind a
network allowlist instead of on the host:

```bash
sbx run claude --kit ./.sbx/kit .
```

The agent configuration is *not* in the kit. `.claude/` is committed and the
workspace is mounted read-write, so the hooks, the permissions allowlist and the
`vaadin-view-conventions` skill are already inside the sandbox — and the base
`claude` kit accepts the trust dialog for the workspace, so they take effect
without a prompt. The kit only adds what that configuration depends on:

| What it adds | Why |
| --- | --- |
| `claude plugin install vaadin-skills` and `vaadin-agent-tools` | `.claude/settings.json` only *enables* them; a fresh container still has to fetch them. |
| `./mvnw -B -ntp test` at create time | The `Stop` hook runs `./mvnw -o test` — **offline**. A fresh sandbox has an empty `~/.m2`, so without pre-warming that hook would block every turn. |
| `permissions.network.allow` | Maven Central plus `maven.vaadin.com` (the pom's `vaadin-directory` repository), GitHub for the plugin installs, and `vaadin.com` / `mcp.vaadin.com` / `www.javadocs.dev` for the plugins' MCP servers. |
| `JAVA_HOME` | The base image ships a JDK but leaves it unset. |

No `apt-get` step is needed: the image already ships `jq` (which every hook builds
its JSON reply with) and `unzip`, and `/usr/lib/jvm/default-java` is JDK 25.

> Tests only — no browser (the tests are browserless) and no npm
> (`build-frontend` binds to `prepare-package`). To reach the app from the host,
> add a top-level `ports: [{container: 8080, name: app}]` plus
> `registry.npmjs.org`, `nodejs.org` and `tools.vaadin.com`.
