# Vaadin Agent Demo — `04-skills`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## Setup on this branch

The **Vaadin Skills plugin** on top of the docs MCP server. It adds a Vaadin 25
primer and task-specific skills (`aura-theme`, `frontend-design`,
`vaadin-form-layout`), so the agent starts from Vaadin's own guidance instead of
discovering it by search — see [`.claude/README.md`](.claude/README.md).

## The prompt

> Create a new view with a text field and a button. On click, a notification
> with the content of the field should appear. The button should be primary,
> and the notification should be a success message in the bottom right. Empty
> input should be handled. Add the view to the navigation and write a test for it.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `message/ui/MessageView.java` | `TextField` + primary `Button`; shows the field content as a success notification in the bottom right. Empty input marks the field invalid and adds an error notification. |
| `base/ui/MainLayout.java` | `@Layout` `AppLayout` whose `SideNav` is built from `MenuConfiguration.getMenuEntries()`, so every `@Menu` view appears automatically. |
| `message/ui/MessageViewTest.java` | Five browserless tests — `./mvnw test` green. |

The primer is what the API docs alone do not carry: **feature-based packaging**
(hence `base/ui` and `message/ui` instead of a flat `views/`) and the warning that
`--lumo-*` properties fail silently under this project's Aura theme.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5 (1M context) |
| Wall-clock / API time | 14m 2s / 2m 49s |
| Tokens in / out | 550 / 12.9k |
| Cache read / write | 1.8m / 68.8k |
| Cost | 1.90 $ |
| Tool calls | ~16 |

> Numbers from `/cost` in the Claude Code session.
