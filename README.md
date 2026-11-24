# Vaadin Agent Demo — `03-MCP`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

The Vaadin docs MCP server (`https://mcp.vaadin.com/docs`), wired through `.mcp.json`. No skills, no hooks.

## Setup

| | |
| --- | --- |
| Files | `.claude/settings.json` (plugins `false`, `enabledMcpjsonServers: ["vaadin"]`), `.mcp.json` (`vaadin` → `https://mcp.vaadin.com/docs`, HTTP) |
| MCP servers | `vaadin` (connected in every run) |
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

| | Run 1 | Run 2 ✔ | Run 3 | **Median** |
| --- | ---: | ---: | ---: | ---: |
| Wall-clock | 37s | 42s | 47s | **42s** |
| API time | 32s | 38s | 43s | **38s** |
| Cost | 0.28 $ | 0.35 $ | 0.42 $ | **0.35 $** |
| Turns | 7 | 10 | 11 | **10** |
| Input tokens | 14 | 20 | 22 | **20** |
| Output tokens | 3.9k | 4.4k | 5.1k | **4.4k** |
| Cache read | 145.4k | 216.0k | 260.1k | **216.0k** |
| Cache write | 21.7k | 27.4k | 33.1k | **27.4k** |
| Tool calls | 6 | 9 | 10 | **9** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 3 | 3 | 3 |
| `mcp__vaadin__get_java_symbol` | 1 | 3 | 2 |
| `ToolSearch` | 1 | 2 | 2 |
| `mcp__vaadin__search_vaadin_docs` | 1 | 1 | 1 |
| `mcp__vaadin__get_full_document` | 0 | 0 | 2 |

## Result

Committed: run 2. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `hello/HelloAgentWorldView.java`
- `hello/HelloAgentWorldViewTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 | green | 6 | 7 | no |
| Run 2 ✔ | green | 5 | 6 | no |
| Run 3 | green | 5 | 6 | no |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- 2–5 MCP calls per run, mostly `get_java_symbol`; no run inspected jars in `~/.m2` (02-vanilla: 2–4 such calls per run).
- The MCP server did not make the run cheaper than 02-vanilla here: median 0.35 $ vs. 0.28 $, with more turns (10 vs. 6).
- Cost rose with the number of lookups: run 1 (2 MCP calls) 0.28 $, run 3 (5 MCP calls) 0.42 $.
- Run 3 added an extra `BrowserlessContextConfiguration` class; the other two produced only view and test.
- Flat packages (`hello/`, `helloagentworld/`) without a `ui` sub-package. No run started the app.
