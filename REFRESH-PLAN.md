# Refresh plan — Phase 0 inventory

Date: 2026-11-24. Source: `origin/*` as of the `archive/pre-refresh/<branch>` tags
(pushed before any change).

## Global findings

- **main** carries the *full* configuration of `08-devloop` (plugins, hooks,
  `vaadin-view-conventions`, `vaadin-devloop`, `.vaadin/`, `.agents/`, `.sbx/`) and
  generated code (`notify/ui/view/NotifyView` + test). It is not a clean base.
- **Infrastructure, kept on every branch:** `pom.xml` (incl. the optional
  `vaadin-dev` dependency, which is standard Vaadin 25 dev mode, not the dev loop),
  `mvnw*`, `.mvn/`, `Dockerfile`, `LICENSE.md`, `Application.java`,
  `application.properties`, `styles.css`, `src/main/frontend/index.html`,
  `ApplicationTest` (Spring context smoke test, 1 test).
- **Kept as shared starting point (plan rule):** `base/ui/MainLayout.java` +
  `META-INF/resources/base/ui/main-layout.css` (generated in an earlier run, but the
  plan keeps MainLayout so "add to navigation" starts equal everywhere).
- **Plugins** (marketplace `vaadin/agent-marketplace`): `vaadin-skills` 0.3.0
  (skills + bundled MCP server `https://mcp.vaadin.com/docs`), `vaadin-agent-tools`
  0.3.2 (skills + its own `PostToolUse` hook). Both installed at user scope but only
  *enabled* by project settings; runs use `--setting-sources project`.

## Per branch

| Branch | Configuration files | Generated code from earlier runs | References existing files? |
| --- | --- | --- | --- |
| `01-chat-client` | none | `GreetingView`, `GreetingViewTest` (flat package, no layout) | — |
| `02-vanilla` | `.claude/settings.json` (both plugins `false`, `enableAllProjectMcpServers: false`), `.mcp.json` (empty), `.claude/README.md` (docs) | `views/MainLayout`, `views/MessageView`, `views/MessageViewTest` | no |
| `03-MCP` | `.claude/settings.json` (plugins `false`, `enabledMcpjsonServers: ["vaadin"]`), `.mcp.json` (`vaadin` → `https://mcp.vaadin.com/docs`), `.claude/README.md` | `views/MainLayout`, `views/MessageView`, test | no |
| `04-skills` | `.claude/settings.json` (marketplace, `vaadin-skills: true`, `vaadin-agent-tools: false`), `.claude/README.md` | `base/ui/MainLayout`, `message/ui/MessageView`, test | no |
| `05-tools-hooks` | `.claude/settings.json` (both plugins, 3 hooks, permissions allowlist), `.claude/hooks/{project-context,vaadin-conventions,verify-tests}.sh`, `.claude/README.md` | `base/ui/MainLayout`, `message/ui/view/MessageView`, test | `project-context.sh` states that navigation is built in `base/ui/MainLayout.java` (true for baseline) |
| `06-custom-skills` | = `05` + `.claude/skills/vaadin-view-conventions/SKILL.md`, `.mcp.json` (empty), `.sbx/kit/spec.yaml` | `HelloAgentWorldView`, `MainLayoutTest`, `HelloAgentWorldViewTest`, `main-layout.css` | **yes — skill step 1 reads `message/ui/view/MessageView.java`, tests use `navigate(MessageView.class)`** |
| `07-figma` | out of scope, left untouched | — | — |
| `08-devloop` | = `06` + `.claude/skills/vaadin-devloop/`, `.agents/skills/vaadin-devloop/`, `.vaadin/{vaadin-dev,.cmd,.ps1,.gitignore}`, permission `Bash(.vaadin/vaadin-dev *)`, `.sbx/README.md` | `hello/ui/view/HelloAgentWorldView` + test, `MainLayoutTest`, restored `MessageView` + test | **yes — same `vaadin-view-conventions` skill → MessageView** |

## Decisions derived from this

- Reference-view rule applies to **06 and 08**: both get `message/ui/view/MessageView`
  + `MessageViewTest` (from `origin/08-devloop`) in the setup commit.
- `08-devloop`: refresh `.vaadin/` and the `vaadin-devloop` skills via
  `mvn vaadin:install-dev-cli` on the new Vaadin version.
- `.sbx/` (Docker-sandbox kit) is not agent configuration; it stays only on the
  branches that had it (06, 08).
- Stale allowlist entries (`mcp__plugin_vaadin-skills_javadoc__*`) are left as they
  are — they belong to the branch configuration, and runs use bypass permissions.

## Agreed deviations from the brief (confirmed by the user)

- No `--strict-mcp-config`: it also drops plugin MCP servers (vaadin-skills' docs
  server). Instead `ENABLE_CLAUDEAI_MCP_SERVERS=false` keeps account connectors out.
- `--permission-mode bypassPermissions` for every run (user-level default does not
  apply under `--setting-sources project`).
- Runs happen in fresh clones, not git worktrees (worktrees leaked the main
  checkout's project skills in a test).
