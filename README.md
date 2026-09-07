# Vaadin Agent Demo — `05-tools-hooks`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## Setup on this branch

The Vaadin Skills and `vaadin-agent-tools` plugins, three enforcing **hooks** and a
permissions allowlist, all in `.claude/settings.json`. There is no `.mcp.json` here —
the docs tools come with the plugins. See [`.claude/README.md`](.claude/README.md)
for what each hook does and why a hook is not a skill.

## The prompt

> Create a new view with a text field and a button. On click, a notification
> with the content of the field should appear. The button should be primary,
> and the notification should be a success message in the bottom right. Empty
> input should be handled. Add the view to the navigation and write a test for it.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `message/ui/view/MessageView.java` | `TextField` + primary `Button`; shows the field content as a success notification in the bottom right. Empty input marks the field invalid instead of notifying. |
| `base/ui/MainLayout.java` | `@Layout` `AppLayout` whose `SideNav` is built from `MenuConfiguration.getMenuEntries()`, so every `@Menu` view appears automatically. |
| `message/ui/view/MessageViewTest.java` | Five browserless tests — `./mvnw test` green. |

The `SessionStart` hook supplied the two facts that cost the earlier branches the
most searching — the active theme is Aura, the test base class is
`SpringBrowserlessTest` — before the first tool call. The app was also started and
checked in Chrome: the drawer entry renders, and the notification card carries
`theme="success"` in `slot="bottom-end"`.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5 (1M context) |
| Wall-clock / API time | 10m 36s / 6m 17s |
| Tokens in / out | 624 / 24.4k |
| Cache read / write | 6.5m / 211.7k |
| Cost | 6.01 $ |
| Tool calls | ~60 |

> Numbers from `/cost` in the Claude Code session. The hooks are not what makes this
> the most expensive run — verifying every signature against the jars and then
> checking the result in a real browser is.
