# Vaadin Agent Demo — `02-vanilla`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## Setup on this branch

**Plain Claude Code, no Vaadin context** — both Vaadin plugins off, no MCP server.
The agent can read and write files, run Maven and check its own tests, but works
from training data alone and has to rediscover the Vaadin 25 API by itself.

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
| `views/MessageViewTest.java` | Six browserless tests — `./mvnw test` green, and the app boots with `/` returning HTTP 200. |

Without Vaadin context the agent spent most of its tool calls reading the Vaadin 25
API straight out of the jars in `~/.m2` to find the right constants, and landed on a
flat `views/` package rather than the feature-based structure Vaadin recommends.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5 (1M context) |
| Wall-clock time | 15m 56s |
| Tokens in / out | 1.1k / 24.1k |
| Cache read / write | 4.2m / 88.8k |
| Cost | 3.60 $ |
| Tool calls | ~38 |

> Numbers from `/cost` in the Claude Code session.
