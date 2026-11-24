# Claude Code configuration -- `02-vanilla`

The baseline: **no Vaadin-specific context at all.** The agent works from training data
alone, so this branch shows what that costs.

- `enabledPlugins` turns both Vaadin plugins **off**. They are installed at user scope
  (`claude plugin list` -> `Scope: user`), so without this file they would be active here
  too and the branch would not be vanilla.
- `.mcp.json` is present but empty (`{"mcpServers": {}}`) -- no MCP server is wired up.
- No hooks, no permission allowlist.

Project settings override user settings, which is what makes the opt-out work.
Compare with `03-MCP`, `04-skills` and `05-tools-hooks`.

**Note:** a global `~/.gitignore_global` rule excluding `.claude` would hide this
directory from git, so `.gitignore` re-includes it.
