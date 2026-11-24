# Refresh report

Date: 2026-11-24. Inventory and plan: [`REFRESH-PLAN.md`](REFRESH-PLAN.md). Every
branch as it was before the refresh is tagged `archive/pre-refresh/<branch>`.

## Outcome

| Branch | Status |
| --- | --- |
| `baseline` | New. Vaadin 25.3.6, Spring Boot 4.2.0, Maven 3.9.16 (wrapper 3.3.4), `MainLayout` only, `ApplicationTest` (1 test). `./mvnw verify` green, `/` loads. |
| `01-chat-client` | Dependencies updated (Vaadin 25.3.6, Spring Boot 4.2.0, wrapper 3.3.4). `./mvnw verify` green (3 tests). Not measured; numbers marked historical. |
| `02-vanilla` … `06-custom-skills`, `08-devloop` | Rewritten: setup commit (baseline + own config) → result commit (median-cost run, unedited) → docs commit (README + `runs/`). 3 runs each, 18/18 runs finished, 18/18 `./mvnw clean verify` green. |
| `07-figma` | Untouched (archive tag only). Marked not comparable on `main`. |
| `main` | Code state of `baseline` plus README, screenshot, plan and this report. |

## Deviations from the plan

1. **No `--strict-mcp-config`** (agreed before the start). It also suppresses MCP
   servers that plugins bring along, so `vaadin-skills`' bundled docs server would have
   been missing on 04/05/06/08. Instead `ENABLE_CLAUDEAI_MCP_SERVERS=false` keeps the
   account's claude.ai connectors out. Project `.mcp.json` servers load through the
   branch's own `enabledMcpjsonServers` (only `03-MCP` has one); `--mcp-config` was
   therefore not passed.
2. **Permission mode set explicitly** to `bypassPermissions` for every run (agreed).
   The user-level default does not apply under `--setting-sources project`. As a
   consequence the permission allowlists on 05/06/08 had no effect, and Claude Code
   also printed "Ignoring 19 permissions.allow entries … workspace has not been
   trusted" for those branches.
3. **Fresh clone per run instead of `git reset --hard && git clean -fdx`.** Same
   effect (clean tree on the setup commit, `~/.m2` kept), plus a unique path per run so
   no project memory can carry over. A test in a `git worktree` had shown the main
   checkout's project skills leaking into the session, hence clones.
4. **`--include-hook-events`** added to the command line so hook executions are
   visible in the stream. It changes the output only, not the agent's context.
5. **`< /dev/null`** on every run, because `claude -p` otherwise waits 3 s for stdin.
6. **Extra commit per branch.** Besides setup and result commit, each measured branch
   has a third commit with README and `runs/`, so the result commit contains only the
   agent's output.
7. **Baseline also removes `NotifyView`** + test (generated code that was on `main`)
   and `.sbx/` / `docs/`. `ApplicationTest` (Spring context smoke test) stays; test
   counts on the branches therefore include 1 baseline test (06/08: 6).
8. **`main` loses its agent configuration.** The plan gives `main` the code state of
   `baseline`, which has none; the per-branch `.claude/README.md` remains on the
   branches.

## Environment changes that the runs could see

- **Chromium (Debian package, 156) installed** via apt before the runs, for checking
  `/` on baseline and for the README screenshot. It was on the `PATH` for all 18 runs.
  Only `06-custom-skills` and `08-devloop` used it (all six runs). The browser was
  not part of the original sandbox, so this is an equal condition for all branches,
  but not the environment the earlier measurements had. Playwright for Python was installed in a separate
  venv in the scratchpad and was not on the agents' `PATH`.
- **`vaadin-agent-tools` 0.3.2 installed** from the branch's own marketplace (it was
  missing); `vaadin-skills` was already at the latest version (0.3.0). Both are
  installed at user scope but only enabled by each branch's project settings.
- **`~/.m2`, `~/.vaadin/node-v24.22.3`, HotswapAgent 2.0.3 and the
  `flow-devloop-daemon` jar were pre-warmed** (`./mvnw verify`, `spring-boot:run`,
  `vaadin:install-dev-cli`, one `vaadin-dev start/stop`).

## Skipped steps

- `01-chat-client`: no measurement (as planned).
- `07-figma`: no change (as planned).
- Versions of plugins/skills/MCP servers: nothing to raise beyond the installation
  above. `mvn vaadin:install-dev-cli` on 25.3.6 produced no change to `.vaadin/` or the
  `vaadin-devloop` skills on 08.
- Stale allowlist entries on 05/06/08 (`mcp__plugin_vaadin-skills_javadoc__*`, a server
  the current plugin no longer ships) were left as part of the branch configuration.

## Observations

- **Configuration took effect on every branch.** Each run's `init` message showed the
  expected MCP servers (`[]` on 02, `vaadin` on 03, `plugin:vaadin-skills:vaadin` on
  04/05/06/08), plugins and skills; user settings, the global `CLAUDE.md` (checked in a
  test run) and claude.ai connectors were absent. Built-in skills of Claude Code 2.1.331
  (`run`, `verify`, `dataviz` …) were present on every branch; `06` run 2 used `run`.
- **The `vaadin-agent-tools` `PostToolUse` hook barely ran.** Its matcher is
  `Edit|Write|MultiEdit`; the agent wrote files through `Bash` heredocs in 8 of 9 runs
  on 05/06/08. Only `08` run 3 used `Write`, and only there did it fire.
- **No hook ever blocked.** `vaadin-conventions.sh` found no Lumo API, and the `Stop`
  test gate was green at the end of every run on 05/06/08.
- **The agent left nothing outside the repo that was measured**, but `06` runs 1 and 2
  installed npm packages under `/tmp` for Playwright scripts. Leftover app and daemon
  processes were stopped between runs by the harness.
- **Baseline check found stale classes.** The first start of `baseline` still showed
  `NotifyView` from `target/classes`; after `./mvnw clean` `/` showed Vaadin's "No views
  found" page (HTTP 200). Runs used fresh clones, so they were not affected.
- **The first commit on `01-chat-client` picked up dev-loop daemon files** from an
  untracked `.vaadin/`; amended before pushing.
