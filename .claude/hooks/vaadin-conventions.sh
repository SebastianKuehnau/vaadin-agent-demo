#!/usr/bin/env bash
# PostToolUse guard: reject Lumo-only API and CSS variables in an Aura-themed project.
#
# Scans the source tree rather than the tool payload, so it fires no matter how the
# file was written -- Write/Edit, or a shell heredoc in auto mode.
set -uo pipefail
cd "${CLAUDE_PROJECT_DIR:-.}" 2>/dev/null || exit 0

scan() {
  command grep -RIn --binary-files=without-match \
    --exclude-dir=generated --exclude-dir=target \
    -E "$1" src/main/java src/main/resources src/main/frontend 2>/dev/null
}

# Only enforce when the app actually loads Aura.
[ -n "$(scan 'Aura\.STYLESHEET|aura\.css' | head -1)" ] || exit 0

violations=$(scan '[A-Za-z]+Variant\.LUMO_[A-Z_]+|--lumo-[a-z-]+|\bLumo\.(LIGHT|DARK)\b')
[ -n "$violations" ] || exit 0

jq -n --arg v "$violations" '{
  decision: "block",
  reason: ("This project loads the Aura theme, but Lumo-only API or CSS custom properties are present:\n\n" + $v + "\n\nAura and Lumo are not interchangeable: --lumo-* resolves to nothing under Aura, and LUMO_ variant constants emit Lumo theme names. Use the theme-agnostic variant constants (ButtonVariant.PRIMARY, NotificationVariant.SUCCESS) and --vaadin-* or --aura-* custom properties instead.")
}'
