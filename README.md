# Vaadin Agent Demo — `01-chat-client`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080/greeting.

## Setup on this branch

The **claude.ai chat interface** — the prompt went into the browser, the resulting
code was copied into the project by hand. No Claude Code, no Vaadin MCP server, no
plugins. This is the baseline the other branches are measured against.

## The prompt

> Create a new view with a text field and a button. On click, a notification
> with the content of the field should appear. The button should be primary,
> and the notification should be a success message in the bottom right. Empty
> input should be handled. Add the view to the navigation and write a test for it.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `GreetingView.java` | `TextField` + primary `Button`; shows the field content as a success notification in the bottom right. Empty input marks the field invalid instead of notifying. |
| `GreetingViewTest.java` | Three browserless tests. |

Two things the chat route did not deliver:

- **No layout.** The view carries `@Menu`, but nothing renders a navigation, so the
  menu entry is invisible. The other branches create a `MainLayout` for it.
- **`@Route("greeting")`** leaves `/` unmapped — the app 404s on its start page.

Everything landed in a flat package (`dev.vaadin.agentdemo`) instead of the
feature-based structure Vaadin recommends, and had to be pasted and compiled by hand.

## What it cost

No numbers: the session ran in the claude.ai chat UI, not in Claude Code, so `/cost`
never saw it — the usage falls under the subscription.

The real cost here is not tokens anyway. It is the manual loop: copy the code out of
the browser, create the files, fix package and imports, compile, repeat.
