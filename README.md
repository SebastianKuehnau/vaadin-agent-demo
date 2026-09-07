# Vaadin Agent Demo

A Spring Boot + Vaadin 25 project used as a testbed for one question: **how much
does an AI coding agent need to know to build a Vaadin view correctly?**

The same feature request is run once per branch, each time with more
Vaadin-specific context — chat UI, docs MCP, skills, hooks, a project-local
conventions skill, Figma. Every branch README records what came out and what it
cost. This page is the overview.

![Screenshot of the running app: the "Vaadin Agent Demo" navbar with a drawer
listing Message and Hello Agent World, a Message text field containing
"Sebastian" next to a primary "Show notification" button, and the resulting
notification in the bottom right corner](docs/images/example-view.png)

*The generated `MessageView` plus the layout around it. Nothing under
`dev/vaadin/agentdemo/` was written by hand.*

## Run it

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Then open **http://localhost:8080** — the first start takes ~30 s while Maven
downloads dependencies. Production build: `./mvnw package && java -jar target/*.jar`.

> Java changes need a restart with `spring-boot:run`. For live reload, install the
> **Vaadin plugin** for your IDE and launch through it — see the
> [Quickstart](https://vaadin.com/quickstart). It is also what
> [Copilot](https://vaadin.com/docs/latest/tools/copilot)'s AI edits require.

---

## The benchmark prompt

Branches `01` through `05` got the same request, verbatim:

> Create a new view with a text field and a button. On click, a notification
> with the content of the field should appear. The button should be primary,
> and the notification should be a success message in the bottom right. Empty
> input should be handled. Add the view to the navigation and write a test for it.

`06-custom-skills` ran the same task as a `HelloAgentWorld` variant — name field,
`Hello <name>!` — so its result sits next to the existing view instead of
replacing it. `07-figma` ran a different task (a customer management screen from a
Figma file) and is therefore not part of the cost comparison below.

---

## Your options for agentic Vaadin development

Each row adds to the one above it. Check out the branch to get exactly that setup —
`.claude/` is committed, so nothing touches your personal `~/.claude/settings.json`.

| Branch | What it adds | Reach for it when |
| --- | --- | --- |
| `01-chat-client` | claude.ai in the browser, code pasted into the project by hand | Nothing to install. Fine for a snippet; the copy-paste loop dominates anything larger. |
| `02-vanilla` | Claude Code in the terminal — file access, Maven, tests, but no Vaadin context | The floor. The agent can verify its own work, but rediscovers the Vaadin 25 API from the jars in `~/.m2`. |
| `03-MCP` | The **Vaadin docs MCP server** (`https://mcp.vaadin.com/docs`) | Always. The cheapest single upgrade: real Vaadin 25 API and docs instead of training-data guesses. |
| `04-skills` | The **`vaadin-skills` plugin** — Vaadin 25 primer plus `aura-theme`, `frontend-design`, `vaadin-form-layout` | You want Vaadin's own guidance on structure and theming, which API docs do not carry. |
| `05-tools-hooks` | **`vaadin-agent-tools`**, three **hooks** (project facts, Aura/Lumo check, test gate) and a permissions allowlist | A rule has to hold even when the agent forgets it. A hook always fires; a skill only might. |
| `06-custom-skills` | A **project-local skill**, `.claude/skills/vaadin-view-conventions/SKILL.md` | Your codebase has conventions of its own — a reference view, a package layout, a test style — and they should be versioned with the code. |
| `07-figma\*` | The **`figma` plugin** plus a project `figma-to-vaadin` skill | The design already exists in Figma and should become a Vaadin view. |

\* Doesn't work with Docker Sandbox at the moment
  
See [`.claude/README.md`](.claude/README.md) for what each branch pins in
`settings.json`, and what the three hooks actually do.

## What it cost

| Branch | Wall-clock | Cost | Tests |
| --- | --- | --- | --- |
| `01-chat-client` | — | not measured | 3 |
| `02-vanilla` | 15m 56s | 3.60 $ | 6 |
| `03-MCP` | 4m 43s | 1.89 $ | 3 |
| `04-skills` | 14m 2s | 1.90 $ | 5 |
| `05-tools-hooks` | 10m 36s | 6.01 $ | 5 |
| `06-custom-skills` | 5m 34s | 4.27 $ | 16\* |

\* Two views are under test on `06`, not one, plus a parameterized layout test
covering both. Test counts are not a quality ranking on their own: `02-vanilla`
wrote the most tests and needed the most tokens to get there.

- **The chat UI has no measurable token cost** — it runs under the subscription. It
  also produced the least: no layout, so its `@Menu` entry has nothing to render it,
  and `/` stays a 404.
- **The MCP server pays for itself.** Without it the agent burns tokens
  rediscovering the API — nearly twice the cost of `03-MCP` for a result that still
  lands in a flat `views/` package.
- **Skills cost about the same as MCP alone** and add feature-based packaging and
  theme awareness.
- **`05-tools-hooks` is the expensive one, and the hooks are not the reason.** The
  cost sits in what the harness made affordable: verifying every signature against
  the jars, then starting the app and checking the result in a real browser — the
  only run in the table where anyone actually saw the feature work.

> Numbers from `/cost` in each session. Wall-clock includes thinking, tool calls
> and the agent waiting on Maven.

---

## Run the agent in a Docker sandbox

`.sbx/kit/` is a [Docker Sandboxes](https://docs.docker.com/ai/sandboxes/) kit that
runs Claude Code in a container behind a network allowlist instead of on the host:

```bash
sbx run claude --kit ./.sbx/kit .
```

The agent configuration is not in the kit — `.claude/` is committed and the
workspace is mounted read-write, so the hooks and skills are already inside. See
[`.sbx/README.md`](.sbx/README.md) for what the kit adds and why.

## Learn more

- [Vaadin Quickstart](https://vaadin.com/quickstart) — the 5-minute getting-started path
- [Components](https://vaadin.com/docs/latest/components) — 50+ UI components, all callable from Java
- [Vaadin MCP setup](https://vaadin.com/docs/latest/building-apps/mcp) — wire the docs server into your assistant
- [Full documentation](https://vaadin.com/docs)
