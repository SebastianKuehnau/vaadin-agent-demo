---
name: vaadin-view-conventions
description: Conventions for new Vaadin views in this project — structure,
  navigation, styling, validation, theming, tests. Always use when a view, a
  layout, a form or a dialog is created or changed.
---

# View conventions

## Procedure

1. Read `dev/vaadin/agentdemo/message/ui/view/MessageView.java` as the reference.
2. Implement.
3. Write a test.
4. Verify visually.

## Required

- `@Route`, `@PageTitle` and `@Menu` — without `@Menu` the view counts as unfinished.
- Build in the constructor; only make fields `private final` if they are also read or set outside the constructor.
- Local variables with `var`.
- Theming via `addThemeVariants`, otherwise a CSS definition in the corresponding style file (for the view or for the component).
- Typed APIs instead of string literals: `setWidth(16, Unit.EM)`, not `setWidth("16em")`.

## Title and navigation

`MainLayout` shows the name of the current view in the navbar — from the
`@Menu` entry, falling back to `MenuConfiguration.getPageHeader(...)`.

- Views do **not** add a heading (`H1`/`H2`) of their own carrying the view name.
  The title comes from `@Menu` or `@PageTitle`, otherwise it appears twice.
- The navigation is generated from `@Menu`. A new view needs no change to
  `MainLayout`.

## Styling

No `getStyle().set(...)` for layout or typography. Instead:

1. `addClassName("view-title")` in Java.
2. CSS in `src/main/resources/META-INF/resources/<java-package-path>/<name>.css`
   — `base/ui/MainLayout.java` → `base/ui/main-layout.css`.
3. Add that file at the top of `META-INF/resources/styles.css` via `@import`.
   Only this guarantees the load order relative to the Aura stylesheet
   (`Application` loads `Aura.STYLESHEET` before `styles.css`).

Custom properties:

- Structure (padding, gap, sizes): theme-agnostic `--vaadin-*`.
- Typography and theme-specific values: `--aura-*`.
- No hardcoded value without a comment that justifies it.

`getStyle().set(...)` remains allowed only for values computed at runtime —
with a comment.

## Event handling

Handlers are named methods, wired up by method reference:

    show = new Button("Show notification", this::onShowButtonClicked);
    message.addValueChangeListener(this::onMessageValueChange);

- Naming scheme `on<Source><Event>`.
- The parameter is called `event` — no IDE-generated type names.
- The handler decides, the business logic lives in its own method
  (`onShowButtonClicked` validates, `showNotification(text)` displays).

## Tests

- `@SpringBootTest` plus `extends SpringBrowserlessTest` — no browser,
  no server.
- `navigate(MessageView.class)`, then `find(H1.class).single()`.
- `MainLayout` contributes components of its own to the tree — among them an
  `H1` for the application name. So narrow queries on generic types:
  `find(H1.class).withClassName("view-title").single()`.
  Never use `first()` instead of `single()` to work around ambiguity —
  `single()` exposes the mistake, `first()` hides it.
- Derive expectations from the same source as the implementation: get the
  menu title via `MenuConfiguration.getMenuEntries()`, do not write it as a
  string literal in the test.
- Give an assertion message that states the expected behaviour in words.

## Forbidden

- Raw `Div`/`Element` DOM where a Vaadin component exists.
- Mixing Lumo and Aura — this project uses Aura.
- Determining APIs by searching `~/.m2`. Use the Vaadin MCP.
