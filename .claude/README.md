# Claude Code configuration -- `04-skills`

One step up from `03-MCP`: **the `vaadin-skills` plugin instead of a hand-wired MCP
server.**

- `extraKnownMarketplaces` registers the marketplace, so the plugin resolves on a fresh
  checkout without the reader having to add it first.
- `vaadin-skills` is **on**. It supplies the same docs MCP server as `03-MCP` plus the
  javadoc MCP server, the Vaadin 25 primer, and the `aura-theme` / `frontend-design` /
  `vaadin-form-layout` skills. The primer is what carries project conventions -- feature
  based packaging, Aura vs. Lumo custom properties -- that plain doc search does not
  surface on its own.
- `vaadin-agent-tools` stays **off**; it arrives in `05-tools-hooks`.
- No `.mcp.json` needed any more -- the plugin brings its own servers.
- Still no hooks: everything here is context the agent *may* use, not a rule it *must*
  follow. That distinction is the point of `05-tools-hooks`.

**Note:** a global `~/.gitignore_global` rule excluding `.claude` would hide this
directory from git, so `.gitignore` re-includes it.
