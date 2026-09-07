# Vaadin Agent Demo — `03-MCP`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## Setup on this branch

`.mcp.json` wires up the **Vaadin docs MCP server** (`https://mcp.vaadin.com/docs`),
so the agent answers against Vaadin 25.2 docs and component API instead of training
data. No curated skills yet — see [`.claude/README.md`](.claude/README.md) for the
branch matrix.

## The prompt

> Create a new view with a text field and a button. On click, a notification
> with the content of the field should appear. The button should be primary,
> and the notification should be a success message in the bottom right. Empty
> input should be handled. Add the view to the navigation and write a test for it.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `views/MessageView.java` | `TextField` + primary `Button`; shows the field content as a success notification in the bottom right. Empty input marks the field invalid and adds an error notification. |
| `views/MainLayout.java` | `@Layout` `AppLayout` whose `SideNav` is built from `MenuConfiguration.getMenuEntries()`, so every `@Menu` view appears automatically. |
| `views/MessageViewTest.java` | Three browserless tests — `./mvnw test` green. |

The MCP server is what got the Vaadin 25 names right without any guessing:
`ButtonVariant.PRIMARY` and `NotificationVariant.SUCCESS` rather than the
`LUMO_`-prefixed constants of older training data, and
`com.vaadin.browserless.SpringBrowserlessTest` as the test base class. The package
layout is still flat, though — that is not something API docs carry.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5 (1M context) |
| Wall-clock / API time | 4m 43s / 3m 2s |
| Tokens in / out | 1.5k / 12.7k |
| Cache read / write | 1.9m / 60.2k |
| Cost | 1.89 $ |
| Tool calls | ~30 |

> Numbers from `/cost` in the Claude Code session.
