#!/usr/bin/env bash
# Stop: run the browserless tests when Java sources changed since the last run,
# and report the result. Verification does not depend on the agent remembering to do it.
#
# Truth comes from Maven's exit code plus the surefire XML reports, not from log
# lines -- Maven's own verbosity flags suppress the "Tests run:" summary.
set -uo pipefail
cd "${CLAUDE_PROJECT_DIR:-.}" 2>/dev/null || exit 0
[ -x ./mvnw ] || exit 0

reports=target/surefire-reports
if [ -d "$reports" ] && [ -z "$(find src -name '*.java' -newer "$reports" -print -quit 2>/dev/null)" ]; then
  exit 0   # nothing changed since the last run
fi

log=$(mktemp); trap 'rm -f "$log"' EXIT
# Reports from test classes that no longer exist would otherwise be counted too.
rm -rf "$reports"
./mvnw -o test >"$log" 2>&1; rc=$?

# Sum tests/failures/errors across every surefire report.
counts=$(command grep -ho 'tests="[0-9]*"\|failures="[0-9]*"\|errors="[0-9]*"\|skipped="[0-9]*"' \
           "$reports"/TEST-*.xml 2>/dev/null |
         awk -F'"' '{t[$1]+=$2} END {printf "%d %d %d %d", t["tests="], t["failures="], t["errors="], t["skipped="]}')
read -r total fail err skip <<<"${counts:-0 0 0 0}"

if [ "$rc" -ne 0 ] || [ "$total" -eq 0 ] || [ "$fail" -gt 0 ] || [ "$err" -gt 0 ]; then
  [ "$total" -eq 0 ] && why="no tests ran" \
    || why="$fail failures, $err errors out of $total tests"
  jq -n --arg w "$why" --arg o "$(tail -40 "$log")" '{
    systemMessage: ("Stop hook: ./mvnw test did NOT pass -- " + $w),
    decision: "block",
    reason: ("The Stop hook ran ./mvnw test and it did not pass (" + $w + "). Fix this before finishing:\n\n" + $o)
  }'
else
  jq -n --arg t "$total" --arg s "$skip" \
    '{systemMessage: ("Stop hook: ./mvnw test green -- " + $t + " tests, 0 failures, " + $s + " skipped")}'
fi
