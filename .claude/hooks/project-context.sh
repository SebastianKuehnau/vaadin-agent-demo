#!/usr/bin/env bash
# SessionStart: hand the agent the project's Vaadin facts up front, so it does not
# have to discover the version, theme and test base class by searching.
set -uo pipefail
cd "${CLAUDE_PROJECT_DIR:-.}" 2>/dev/null || exit 0
[ -f pom.xml ] || exit 0

tag() { command grep -m1 -oE "<$1>[^<]+</$1>" pom.xml | command sed -E "s|</?$1>||g"; }
vaadin=$(tag vaadin.version)
java=$(tag java.version)
boot=$(command grep -A3 '<artifactId>spring-boot-starter-parent' pom.xml \
       | command grep -m1 -oE '<version>[^<]+' | command sed 's|<version>||')

theme=Lumo
command grep -qRI 'Aura\.STYLESHEET' src/main/java 2>/dev/null && theme=Aura

testbase="none configured"
command grep -q 'browserless-test-spring' pom.xml \
  && testbase="com.vaadin.browserless.SpringBrowserlessTest (no browser, no server)"

jq -n --arg v "$vaadin" --arg j "$java" --arg b "$boot" --arg t "$theme" --arg tb "$testbase" '
{ hookSpecificOutput: {
    hookEventName: "SessionStart",
    additionalContext: (
      "Project facts read from pom.xml by the SessionStart hook:\n"
      + "- Vaadin \($v), Java \($j), Spring Boot \($b)\n"
      + "- Active theme: \($t). Use --vaadin-* or --\($t|ascii_downcase)-* custom properties, and theme-agnostic variant constants (ButtonVariant.PRIMARY, never LUMO_PRIMARY).\n"
      + "- Browserless test base class: \($tb)\n"
      + "- Feature-based packages under src/main/java/dev/vaadin/agentdemo/. Navigation is generated from @Menu via MenuConfiguration in base/ui/MainLayout.java, so a new @Route + @Menu view needs no layout edit."
    ) } }'
