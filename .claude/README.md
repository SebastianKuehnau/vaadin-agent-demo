# Claude Code configuration -- `03-MCP`

One step up from `02-vanilla`: **the Vaadin docs MCP server, and nothing else.**

- `.mcp.json` wires the docs server directly (`https://mcp.vaadin.com/docs`, HTTP), and
  `enabledMcpjsonServers` pre-approves it so no trust prompt appears on first use.
- `enabledPlugins` keeps both Vaadin plugins **off** on purpose. The `vaadin-skills`
  plugin bundles the MCP server *together with* the primer and skills, so using the
  plugin here would smuggle in the very thing `04-skills` is meant to add. Wiring the
  server by hand keeps the two steps separate.
- Consequence you can see in the transcript: tools are named `mcp__vaadin__*` here,
  versus `mcp__plugin_vaadin-skills_vaadin__*` from `04-skills` onward.
- No hooks, no permission allowlist.

**Note:** a global `~/.gitignore_global` rule excluding `.claude` would hide this
directory from git, so `.gitignore` re-includes it.
