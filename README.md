# Vaadin Agent Demo — `06-custom-skills`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

Everything from `05-tools-hooks` plus the project-local skill `.claude/skills/vaadin-view-conventions/SKILL.md`, which names `MessageView` as the reference view.

## Setup

| | |
| --- | --- |
| Files | as `05-tools-hooks`, plus `.claude/skills/vaadin-view-conventions/SKILL.md`, `.mcp.json` (empty), `.sbx/kit/spec.yaml` (Docker-sandbox kit, not read by the agent) |
| MCP servers | `plugin:vaadin-skills:vaadin` |
| Plugins / skills | `vaadin-skills` 0.3.0, `vaadin-agent-tools` 0.3.2 |
| Hooks | same three hooks as `05-tools-hooks` |
| Permissions | `--permission-mode bypassPermissions` (same for every branch) |
| Reference view | **yes** — `message/ui/view/MessageView.java` + `MessageViewTest` (5 tests) are in the setup commit, because the skill's first step is to read MessageView. Other branches started without it. |

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
| Wall-clock | 1m 02s | 1m 15s | 1m 07s | **1m 07s** |
| API time | 45s | 1m 01s | 55s | **55s** |
| Cost | 0.37 $ | 0.53 $ | 0.54 $ | **0.53 $** |
| Turns | 12 | 21 | 16 | **16** |
| Input tokens | 22 | 42 | 34 | **34** |
| Output tokens | 5.3k | 6.6k | 6.1k | **6.1k** |
| Cache read | 294.5k | 631.5k | 550.3k | **550.3k** |
| Cache write | 25.2k | 33.4k | 38.7k | **33.4k** |
| Tool calls | 10 | 18 | 14 | **14** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 7 | 12 | 9 |
| `Read` | 2 | 2 | 1 |
| `Skill` | 1 | 2 | 1 |
| `mcp__plugin_vaadin-skills_vaadin__get_java_symbol` | 0 | 1 | 2 |
| `ToolSearch` | 0 | 1 | 1 |
| ↳ Skill `vaadin-view-conventions` | 1 | 1 | 1 |
| ↳ Skill `run` | 0 | 1 | 0 |
| Hook `SessionStart:startup` | 1 | 1 | 1 |
| Hook `PostToolUse:Bash` | 6 | 12 | 7 |
| Hook `Stop` | 1 | 1 | 1 |

## Result

Committed: run 2. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `hello/ui/view/HelloAgentWorldView.java`
- `hello/ui/view/HelloAgentWorldViewTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 | green | 7 | 13 | yes — `spring-boot:run`, Playwright script, screenshots |
| Run 2 ✔ | green | 7 | 13 | yes — `spring-boot:run`, Playwright script, screenshots |
| Run 3 | green | 7 | 13 | yes — `spring-boot:run`, headless Chromium screenshot |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- `vaadin-view-conventions` was invoked in all three runs; run 2 also invoked the built-in `run` skill.
- Only branch besides 08 that started the app — in every run (`./mvnw spring-boot:run`); step 4 of the conventions skill reads "Verify visually". Runs 1 and 2 wrote Node Playwright scripts against the system Chromium to click through the view.
- The output mirrors the reference view: package `hello/ui/view/`, `VerticalLayout`, `@Menu(order = 2)` after Message, exactly 7 new tests in every run.
- Most expensive and slowest branch: median 0.53 $ and 1m 07s, widest token spread of all branches (325k–672k total tokens).
- `MessageView` was not modified in any run.
