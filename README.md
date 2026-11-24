# Vaadin Agent Demo — `02-vanilla`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

Plain Claude Code with no Vaadin context: both Vaadin plugins disabled, no MCP server. The floor the other branches are measured against.

## Setup

| | |
| --- | --- |
| Files | `.claude/settings.json` (both plugins `false`, `enableAllProjectMcpServers: false`), `.mcp.json` (empty) |
| MCP servers | none |
| Plugins / skills | none |
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

| | Run 1 ✔ | Run 2 | Run 3 | **Median** |
| --- | ---: | ---: | ---: | ---: |
| Wall-clock | 42s | 39s | 37s | **39s** |
| API time | 36s | 34s | 32s | **34s** |
| Cost | 0.28 $ | 0.30 $ | 0.26 $ | **0.28 $** |
| Turns | 8 | 6 | 6 | **6** |
| Input tokens | 16 | 12 | 12 | **12** |
| Output tokens | 4.3k | 4.2k | 3.9k | **4.2k** |
| Cache read | 186.2k | 147.7k | 137.4k | **147.7k** |
| Cache write | 20.0k | 22.8k | 19.5k | **20.0k** |
| Tool calls | 7 | 5 | 5 | **5** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 7 | 5 | 5 |

## Result

Committed: run 1. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `hello/ui/HelloAgentWorldView.java`
- `hello/ui/HelloAgentWorldViewTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 ✔ | green | 7 | 8 | no |
| Run 2 | green | 7 | 8 | no |
| Run 3 | green | 7 | 8 | no |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- Every file was written through `Bash` heredocs; `Bash` is the only tool used in all three runs (5–7 calls).
- Without docs it looked the API up in the jars: 2–4 calls per run ran `unzip`/`javap` on the browserless-test jars in `~/.m2`.
- It still used the theme-agnostic constants (`ButtonVariant.PRIMARY`, `NotificationVariant.SUCCESS`) and added `@Menu`, so the view appears in the side navigation without a layout edit.
- Most consistent result of all branches: 7 new tests in every run, 0.26–0.30 $.
- It ran `./mvnw test` but never started the app.
