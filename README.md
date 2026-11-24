# Vaadin Agent Demo — `08-devloop`

One branch of the Vaadin agent demo. **The `main` branch README compares all branches.**
Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## What this branch adds

Everything from `06-custom-skills` plus the Vaadin dev loop: `.vaadin/vaadin-dev` and the `vaadin-devloop` skill, installed by `mvn vaadin:install-dev-cli`.

## Setup

| | |
| --- | --- |
| Files | as `06-custom-skills`, plus `.vaadin/vaadin-dev{,.cmd,.ps1}`, `.vaadin/.gitignore`, `.claude/skills/vaadin-devloop/SKILL.md`, `.agents/skills/vaadin-devloop/`, allowlist entry `Bash(.vaadin/vaadin-dev *)`, `.sbx/README.md` |
| MCP servers | `plugin:vaadin-skills:vaadin` |
| Plugins / skills | `vaadin-skills` 0.3.0, `vaadin-agent-tools` 0.3.2 |
| Hooks | same three hooks as `05-tools-hooks` |
| Permissions | `--permission-mode bypassPermissions` (same for every branch) |
| Reference view | **yes** — same as `06-custom-skills`: `MessageView` + `MessageViewTest` (5 tests) in the setup commit. |

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
| Wall-clock | 1m 19s | 49s | 56s | **56s** |
| API time | 41s | 42s | 48s | **42s** |
| Cost | 0.37 $ | 0.34 $ | 0.36 $ | **0.36 $** |
| Turns | 13 | 13 | 15 | **13** |
| Input tokens | 20 | 20 | 22 | **20** |
| Output tokens | 5.0k | 5.0k | 5.9k | **5.0k** |
| Cache read | 287.5k | 284.7k | 328.5k | **287.5k** |
| Cache write | 29.8k | 29.1k | 29.4k | **29.4k** |
| Tool calls | 10 | 10 | 12 | **10** |

Wall-clock and API time as reported by Claude Code (`duration_ms`, `duration_api_ms`). Tokens summed over all models of the session. Raw streams: `runs/run-<n>.jsonl`, evaluation: `runs/summary.json`.

## Calls

| Tool | Run 1 | Run 2 | Run 3 |
| --- | ---: | ---: | ---: |
| `Bash` | 7 | 7 | 7 |
| `Skill` | 2 | 2 | 2 |
| `Read` | 1 | 1 | 1 |
| `Write` | 0 | 0 | 2 |
| ↳ Skill `vaadin-view-conventions` | 1 | 1 | 1 |
| ↳ Skill `vaadin-devloop` | 1 | 1 | 1 |
| Hook `SessionStart:startup` | 1 | 1 | 1 |
| Hook `PostToolUse:Bash` | 7 | 7 | 7 |
| Hook `Stop` | 1 | 1 | 1 |
| Hook `PostToolUse:Write` | 0 | 0 | 4 |

## Result

Committed: run 3. Files (relative to `src/{main,test}/java/dev/vaadin/agentdemo/`):

- `hello/ui/view/HelloAgentWorldView.java`
- `hello/ui/view/HelloAgentWorldViewTest.java`

Checked independently after each run (`./mvnw clean verify`, outside the agent):

| Run | verify | New tests | All tests | App started / browser check by the agent |
| --- | --- | ---: | ---: | --- |
| Run 1 | green | 7 | 13 | yes — `vaadin-dev start` + `apply`, headless Chromium screenshot |
| Run 2 | green | 7 | 13 | yes — `vaadin-dev start` + `apply`, headless Chromium screenshot |
| Run 3 ✔ | green | 7 | 13 | yes — `vaadin-dev start` + `apply`, headless Chromium screenshot |

The view of the committed run is in the side navigation: checked by starting the app and loading the view's route in headless Chromium (`vaadin-side-nav-item` "Hello Agent World" present).

## Observations

- Both project skills were invoked in every run: `vaadin-view-conventions` and `vaadin-devloop`. The benchmark prompt does not mention the dev loop.
- Every run started the app with `.vaadin/vaadin-dev start`, applied the edit with `apply` and took a headless Chromium screenshot — no `spring-boot:run`.
- Same visual check as 06-custom-skills, but cheaper and faster: median 0.36 $ / 56s vs. 0.53 $ / 1m 07s, with a tight cost spread (0.34–0.37 $).
- No MCP call in any run; the conventions skill and the reference view were enough for the API.
- Run 3 (committed) is the only run in this round that used `Write` instead of `Bash` for files — and the only one where a `PostToolUse:Write` hook fired (4 hook starts for 2 `Write` calls: the project hook and the `vaadin-agent-tools` hook).
