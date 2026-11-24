# Vaadin Agent Demo — `04-skills`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

The `vaadin-skills` plugin: Vaadin skills plus the same hosted docs MCP server, bundled with the plugin.

## Setup

| | |
| --- | --- |
| Files | `.claude/settings.json` (marketplace `vaadin/agent-marketplace`, `vaadin-skills: true`, `vaadin-agent-tools: false`) |
| MCP servers | `plugin:vaadin-skills:vaadin` (bundled with the plugin) |
| Plugins / skills | `vaadin-skills` 0.3.0 (skills `aura-theme`, `vaadin-browserless-testing`, `vaadin-form-layout`, `vaadin-frontend-design`, `vaadin-usage-review`) |
| Hooks | none |
| Permissions | `--permission-mode bypassPermissions` (same for every branch) |
| Reference view | none |

Every run: `claude -p "<prompt>" --model claude-opus-5-5 --output-format stream-json --verbose --include-hook-events --setting-sources project --permission-mode bypassPermissions`, with `ENABLE_CLAUDEAI_MCP_SERVERS=false`, in a fresh clone of the setup commit. The `init` message of each run lists exactly the servers and plugins above (see `runs/`).

## Versions

Vaadin 25.3.6 · Spring Boot 4.2.0 · Java 25 · Claude Code 2.1.331 · model `claude-opus-5-5` · measured 2026-11-24

## Prompt

> Create a new view names HelloAgentWorld with a text field for a name and a show
> button. On click, a notification with the content of the field with the Prefix
> "Hello" and the suffix "!" should appear. The button should be primary, and the
> notification should be a success message in the bottom right. Empty input should
> be handled. Add the view to the navigation and write a test for it.

## Measurements

Three runs, each in a new session on a fresh working tree. ✔ = run whose code is committed (median cost).

| | Run 1 | Run 2 | Run 3 ✔ | **Median** |
| --- | ---: | ---: | ---: | ---: |
| Wall-clock | 1m 02s | 1m 04s | 1m 26s | **1m 04s** |
| API time | 58s | 57s | 1m 18s | **58s** |
| Cost | 0.48 $ | 0.55 $ | 0.49 $ | **0.49 $** |
| Turns | 13 | 17 | 16 | **16** |
| Input tokens | 26 | 30 | 30 | **30** |
| Output tokens | 6.7k | 6.6k | 6.4k | **6.6k** |
| Cache read | 359.6k | 490.9k | 441.6k | **441.6k** |
| Cache write | 33.9k | 40.3k | 33.6k | **33.9k** |
| Tool calls | 11 | 15 | 14 | **14** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 5 | 7 | 7 |
| `mcp__plugin_vaadin-skills_vaadin__get_java_symbol` | 4 | 5 | 5 |
| `ToolSearch` | 1 | 2 | 1 |
| `Skill` | 1 | 1 | 1 |
| ↳ Skill `vaadin-skills:vaadin-browserless-testing` | 1 | 1 | 1 |

## Result

Committed: run 3. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `hello/ui/HelloAgentWorldView.java`
- `hello/ui/HelloAgentWorldPage.java`
- `hello/ui/HelloAgentWorldTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 | green | 6 | 7 | no |
| Run 2 | green | 6 | 7 | no |
| Run 3 ✔ | green | 7 | 8 | no |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- Every run invoked exactly one skill, `vaadin-skills:vaadin-browserless-testing`; none of the theming or layout skills was used.
- That skill shaped the tests: all three runs wrote a page-object helper (`HelloAgentWorldPage`) next to `HelloAgentWorldTest`.
- Most expensive of the branches that did not start the app: median 0.49 $ and 1m 04s, with 4–5 `get_java_symbol` calls per run.
- Feature package with `ui` sub-package (`hello/ui/`) in all three runs. Run 3 (committed) chose the route `hello` instead of `hello-agent-world`.
- No run started the app.
