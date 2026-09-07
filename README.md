# Vaadin Agent Demo — `06-custom-skills`

One branch of the Vaadin agent demo: the same feature request runs on every branch,
with more Vaadin-specific context each time. **The `main` branch README compares
them all** — start there for the overview.

Run it: `./mvnw spring-boot:run`, then open http://localhost:8080.

## Setup on this branch

Everything from `05-tools-hooks` — both Vaadin plugins, the permissions allowlist,
the three hooks — plus one addition. The earlier branches added *knowledge* (docs
MCP, Vaadin skills) and *enforcement* (hooks); this one adds **taste**:

`.claude/skills/vaadin-view-conventions/SKILL.md` points at `MessageView` as the
reference implementation, then states the rules: `@Route` + `@PageTitle` + `@Menu`
(a view without `@Menu` counts as unfinished), build in the constructor, `var` for
locals, typed APIs (`setWidth(16, Unit.EM)`) instead of string literals, no
`getStyle().set(...)` for layout, handlers as named `on<Source><Event>` methods
wired by method reference, tests that derive their expectations from
`MenuConfiguration` instead of hardcoding titles, and `single()` over `first()` so
an ambiguous query fails loudly.

A plugin skill ships someone else's best practice; this one encodes *ours*, and it
is versioned with the code it governs.

## The prompt

Same task as the other branches, phrased as a greeting variant so the result sits
next to the existing view instead of replacing it:

> Create a new view names HelloAgentWorld with a text field for a name and a show
> button. On click, a notification with the content of the field with the Prefix
> "Hello" and the suffix "!" should appear. The button should be primary, and the
> notification should be a success message in the bottom right. Empty input should
> be handled. Add the view to the navigation and write a test for it.

## What Claude produced

Paths relative to `src/{main,test}/java/dev/vaadin/agentdemo/`:

| File | Purpose |
| --- | --- |
| `base/ui/MainLayout.java` | `@Layout` `AppLayout` whose `SideNav` is built from `MenuConfiguration.getMenuEntries()`. The navbar shows the current view's name. |
| `message/ui/view/MessageView.java` | The reference view from the previous branch: `TextField` + primary `Button`, success notification in the bottom right. |
| `helloagentworld/ui/view/HelloAgentWorldView.java` | Same shape, applied to a greeting: `Hello <name>!` as a success notification at `BOTTOM_END`. Blank input marks the field invalid ("Please enter a name") and focuses it; typing clears the error. |
| `base/ui/MainLayoutTest.java` | Parameterized over both views. The side-nav item is located by the route it links to, derived from `MenuEntry` rather than written out as a string. |
| `message/ui/view/MessageViewTest.java` | Five browserless tests. |
| `helloagentworld/ui/view/HelloAgentWorldViewTest.java` | Seven browserless tests: menu registration, greeting text, position, success variant, primary button, empty/blank input, whitespace trimming, error clearing. |

The skill's effect is visible in the diff: the new view mirrors the reference view
down to the validation strategy, and the agent updated an existing test that the new
`@Menu` entry would have broken — because the skill told it that `single()` is a
feature, not an obstacle.

`./mvnw test` — 16 tests green. The agent also ran the app and checked it in Chrome:
the drawer entry renders, empty input shows the field error, and clicking **Show**
with a name produces the `Hello Agent!` success notification in the bottom right.

## What it cost

| Metric | Value |
| --- | --- |
| Model | Claude Opus 5 (1M context) |
| Wall-clock / API time | 5m 34s / 4m 37s |
| Tokens in / out | 596 / 19.3k |
| Cache read / write | 4.2m / 168.4k |
| Cost | 4.27 $ |
| Tool calls | ~45 |

> Numbers from `/cost` in the Claude Code session.
