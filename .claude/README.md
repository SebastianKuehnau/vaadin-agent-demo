# Claude Code configuration

Committed on purpose: whoever checks out a branch gets that branch's setup without
touching their personal `~/.claude/settings.json`. Project settings override user
settings, so each branch pins exactly what it needs.

## What each branch configures

| Branch | `.claude/settings.json` | Effect |
| --- | --- | --- |
| `02-vanilla` | both plugins `false` | No Vaadin context. The agent works from training data alone. |
| `03-MCP` | plugins `false`, `.mcp.json` wires `https://mcp.vaadin.com/docs` | Vaadin docs MCP, but no curated skills. Tools appear as `mcp__vaadin__*`. |
| `04-skills` | `vaadin-skills` plugin `true` | Adds the Vaadin 25 primer and the `aura-theme` / `frontend-design` / `vaadin-form-layout` skills on top of MCP. |
| `05-tools-hooks` | both plugins `true` + `hooks` + `permissions` | Adds the `vaadin-agent-tools` skills and three enforcing hooks. |

## The three hooks on this branch

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
