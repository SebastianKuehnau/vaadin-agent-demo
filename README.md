# Vaadin Agent Demo — `05-tools-hooks`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

Both Vaadin plugins (`vaadin-skills`, `vaadin-agent-tools`), three project hooks and a permissions allowlist.

## Setup

| | |
| --- | --- |
| Files | `.claude/settings.json` (both plugins, hooks, allowlist), `.claude/hooks/project-context.sh`, `.claude/hooks/vaadin-conventions.sh`, `.claude/hooks/verify-tests.sh` |
| MCP servers | `plugin:vaadin-skills:vaadin` |
| Plugins / skills | `vaadin-skills` 0.3.0, `vaadin-agent-tools` 0.3.2 (adds skills `vaadin-check-aura-usage`, `vaadin-check-theme-mixing`, `vaadin-create-project` and its own `PostToolUse` hook on `Edit|Write|MultiEdit`) |
| Hooks | `SessionStart` → `project-context.sh` (Vaadin/Java/Boot versions, theme, test base class), `PostToolUse` on `Write|Edit|Bash` → `vaadin-conventions.sh` (blocks `LUMO_*` / `--lumo-*`), `Stop` → `verify-tests.sh` (`./mvnw -o test`) |
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
| Wall-clock | 51s | 58s | 56s | **56s** |
| API time | 43s | 50s | 48s | **48s** |
| Cost | 0.40 $ | 0.39 $ | 0.38 $ | **0.39 $** |
| Turns | 13 | 12 | 11 | **12** |
| Input tokens | 22 | 24 | 20 | **22** |
| Output tokens | 5.3k | 5.8k | 6.0k | **5.8k** |
| Cache read | 272.6k | 290.8k | 262.6k | **272.6k** |
| Cache write | 30.3k | 27.0k | 26.1k | **27.0k** |
| Tool calls | 11 | 10 | 9 | **10** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 5 | 6 | 8 |
| `mcp__plugin_vaadin-skills_vaadin__get_java_symbol` | 3 | 2 | 0 |
| `Skill` | 1 | 1 | 1 |
| `ToolSearch` | 1 | 1 | 0 |
| `mcp__plugin_vaadin-skills_vaadin__search_vaadin_docs` | 1 | 0 | 0 |
| ↳ Skill `vaadin-skills:vaadin-browserless-testing` | 1 | 1 | 1 |
| Hook `SessionStart:startup` | 1 | 1 | 1 |
| Hook `PostToolUse:Bash` | 5 | 6 | 8 |
| Hook `Stop` | 1 | 1 | 1 |

## Result

Committed: run 2. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `helloagentworld/ui/HelloAgentWorldView.java`
- `helloagentworld/ui/HelloAgentWorldPage.java`
- `helloagentworld/ui/HelloAgentWorldTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 | green | 5 | 6 | no |
| Run 2 ✔ | green | 4 | 5 | no |
| Run 3 | green | 4 | 5 | no |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- All project hooks fired: `SessionStart` once, `PostToolUse:Bash` after every `Bash` call (5–8), `Stop` once. None of them blocked.
- The plugin's own `PostToolUse` hook (`Edit|Write|MultiEdit`) never fired: the agent wrote every file with `Bash` heredocs.
- Cheaper than 04-skills with the same plugin skill in use (median 0.39 $ vs. 0.49 $) and the tightest cost spread of all branches (0.38–0.40 $).
- Fewest tests: 4–5 new tests per run (02-vanilla: 7). Run 3 made no MCP call at all.
- No run started the app; the allowlist had no effect because all runs used `bypassPermissions`.
