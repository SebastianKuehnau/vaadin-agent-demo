# Claude Code configuration

Committed on purpose: whoever checks out a branch gets that branch's setup without
touching their personal `~/.claude/settings.json`. Project settings override user
settings, so each branch pins exactly what it needs.

## What each branch configures

Each row adds to the row above it, unless noted otherwise.

| Branch | Agent configuration | Effect |
| --- | --- | --- |
| `00-init` | none — no `.claude/` | The bare Vaadin skeleton, before any agent setup. |
| `01-chat-client` | none — no `.claude/` | Work happened in the claude.ai chat UI; the code was pasted into the project by hand. Claude Code was not involved, so there is nothing to configure. |
| `02-vanilla` | both plugins `false`, empty `.mcp.json`, `enableAllProjectMcpServers` | No Vaadin context. The agent works from training data alone. |
| `03-MCP` | `.mcp.json` wires `vaadin` → `https://mcp.vaadin.com/docs`, switched on with `enabledMcpjsonServers`; plugins stay `false` | Vaadin docs MCP, but no curated skills. Tools appear as `mcp__vaadin__*`. |
| `04-skills` | `extraKnownMarketplaces` (`vaadin/agent-marketplace`) + `vaadin-skills` `true`; **`.mcp.json` removed** | The plugin ships its own MCP server, so the hand-wired one is redundant. Adds the Vaadin 25 primer and the `aura-theme` / `frontend-design` / `vaadin-form-layout` skills. Tools are now `mcp__plugin_vaadin-skills_*`. |
| `05-tools-hooks` | `vaadin-agent-tools` `true` as well, plus `hooks` and `permissions.allow` (19 entries) | Adds the agent-tools skills, the three hooks below, and an allowlist that removes approval round-trips for the read-only Vaadin tools and `./mvnw`. |
| `06-custom-skills` | + `.claude/skills/vaadin-view-conventions/SKILL.md` | Project-local taste: how a view in *this* codebase is supposed to look. A plugin skill carries someone else's best practice; this one is versioned with the code it governs. |
| `07-figma` | + `figma@claude-plugins-official` (installed by hand — **not** in `enabledPlugins`) + `.claude/skills/figma-to-vaadin/SKILL.md` + `.sbx/kit/spec.yaml` | Design-to-code from Figma. Both additions have to be made by hand: the plugin is not committed, and the skill is written for this project. See the root [`README.md`](../README.md) for the workflow and its limits. |

Two of these are deliberately *not* committed as enabled state:

- **The Figma plugin.** The two Vaadin plugins come from a marketplace this repo
  declares, so `enabledPlugins` can switch them on for anyone who checks out the
  branch. `figma@claude-plugins-official` is installed per machine instead, because
  it needs an interactive Figma login that no committed file can carry.
- **`.mcp.json` on `04-skills` and later.** It is absent or empty on purpose: the
  docs tools, the curated skills and the hooks all arrive through
  `.claude/settings.json`, not through a hand-wired MCP server.

## The three hooks (from `05-tools-hooks` onward)

| Event | Script | Effect |
| --- | --- | --- |
| `SessionStart` | `hooks/project-context.sh` | Injects Vaadin version, Java version, active theme and test base class from `pom.xml`. |
| `PostToolUse` on `Write\|Edit\|Bash` | `hooks/vaadin-conventions.sh` | Blocks Lumo-only API (`*Variant.LUMO_*`) and `--lumo-*` CSS in this Aura-themed project. |
| `Stop` | `hooks/verify-tests.sh` | Runs `./mvnw test` when Java sources changed, and blocks the turn if it is not green. |

## Two things that are easy to get wrong

**A skill is not a hook.** Skills run only when the agent chooses to invoke them —
nothing forces it. The Aura/Lumo check is therefore a shell hook, not the
`vaadin-check-theme-mixing` skill.

**Auto mode bypasses `Write` and `Edit`.** In auto mode the agent writes files through
the shell (`cat > file <<EOF`), so a hook matching only `Write|Edit` never fires. Hence
the `Bash` matcher, and hooks that scan the tree rather than parse the tool payload.

**Note:** a global `~/.gitignore_global` rule excluding `.claude` would hide all of this
from git, so each branch's `.gitignore` re-includes it.
