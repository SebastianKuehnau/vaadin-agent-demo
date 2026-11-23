# Vaadin Agent Demo — `08-devloop`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080 — or, the way the
agent does it, `.vaadin/vaadin-dev start`.

## Setup on this branch

Everything from `06-custom-skills` — both Vaadin plugins, the permissions allowlist,
the three hooks, the project-local `vaadin-view-conventions` skill — plus the
**Vaadin dev loop** (Vaadin 25.3):

- `com.vaadin:vaadin-dev` in `pom.xml`.
- `mvn vaadin:install-dev-cli`, run once, wrote the CLI to `.vaadin/` and the agent
  instructions to `.claude/skills/vaadin-devloop/` and `.agents/skills/vaadin-devloop/`.
  All of it is committed, like `mvnw`. HotswapAgent lands in `~/.vaadin/devloop`,
  outside the project.
- `Bash(.vaadin/vaadin-dev *)` in the permissions allowlist.

The dev loop keeps the app running in a daemon. After an edit the agent runs
`.vaadin/vaadin-dev apply`, which compiles, hot-swaps the classes into the running JVM
and reports a verdict by exit code — no restart and no full Maven cycle per check. See
[Dev Loop CLI](https://vaadin.com/docs/latest/flow/configuration/live-reload/dev-loop-cli).

This is also the first branch on **Vaadin 25.3.5 and Spring Boot 4.2.0**. Branches
`01` to `06` were measured on Vaadin 25.2.6 and Spring Boot 4.1.0.

## The prompt

The `06-custom-skills` prompt, with `MessageView` in place as the reference view,
plus one sentence asking for the dev loop:

> Create a new view names HelloAgentWorld with a text field for a name and a show
> button. On click, a notification with the content of the field with the Prefix
> "Hello" and the suffix "!" should appear. The button should be primary, and the
> notification should be a success message in the bottom right. Empty input should
> be handled. Add the view to the navigation and write a test for it.
> **Use the Vaadin dev loop (.vaadin/vaadin-dev) to start the app and verify the
> change.**

The last sentence is needed. A first run without it (Claude Sonnet 5.5, 55 s,
0.32 $) ignored the installed skill, verified with `./mvnw test` only and never
started the app.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `hello/ui/view/HelloAgentWorldView.java` | `TextField` "Name" + primary "Show" button. `Hello <name>!` (trimmed) as a success notification at `BOTTOM_END`. Blank input marks the field invalid ("Please enter a name") and focuses it; typing clears the error. `@Menu` order 2. |
| `hello/ui/view/HelloAgentWorldViewTest.java` | Seven browserless tests: menu entry, greeting text, position, success variant, trimming, primary button, empty/blank input, error clearing. |
| `base/ui/MainLayoutTest.java` | Extended so its parameterized navbar-title checks cover the new view. |

`./mvnw test` — 17 tests green. The agent started the app with
`.vaadin/vaadin-dev start`; `apply` hot-swapped the new class with exit code 0 and no
restart. It then checked the result in headless Chromium: both drawer entries render,
empty input shows the field error, and **Show** with "Agent" produces the
`Hello Agent!` success notification in the bottom right.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5.5 |
| Vaadin / Spring Boot | 25.3.5 / 4.2.0 |
| Wall-clock / API time | 1m 59s / 1m 40s |
| Tokens in / out | 32 / 9.4k |
| Cache read / write | 1.1m / 75.3k |
| Cost | 1.01 $ |
| Turns | 21 |

> Numbers from `claude -p --output-format json` (`duration_ms`, `duration_api_ms`,
> `total_cost_usd`), not `/cost`. One run, a newer model and a newer Vaadin version
> than `01` to `06` — compare with care.
