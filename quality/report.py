#!/usr/bin/env python3
"""Builds QUALITY.md from quality/results/*/*.json, the run patches and docs/gallery/.

    python3 quality/report.py
"""
import glob, json, os, re

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
Q = os.path.join(REPO, "quality")
BRANCHES = ["02-vanilla", "03-MCP", "04-skills", "05-tools-hooks", "06-custom-skills", "08-devloop"]
REQ = [
    ("r1_viewNamedHelloAgentWorldWithRoute", "View `HelloAgentWorld…` with `@Route`"),
    ("r2_viewListedInRenderedSideNavigation", "Entry in the rendered side navigation"),
    ("r3_textFieldAndShowButton", "One text field and a *Show* button"),
    ("r4_buttonIsPrimary", "Button is primary"),
    ("r5_greetingTextIsHelloNameExclamation", "Notification text is exactly `Hello Sebastian!`"),
    ("r6_notificationIsSuccess", "Notification uses the success variant"),
    ("r7_notificationBottomRight", "Notification at `BOTTOM_END`"),
    ("r8_emptyInputShowsNoGreeting", "Empty input: no greeting"),
    ("r9_emptyInputGivesVisibleFeedback", "Empty input: visible feedback (field invalid or error notification)"),
]
EXTRA = [
    ("b1_blankInputShowsNoGreeting", "Whitespace-only input: no greeting"),
    ("b2_nameIsTrimmed", "Name is trimmed (`  Sebastian  ` → `Hello Sebastian!`)"),
]
CHECKS = [
    ("deprecations", "Deprecated API in agent code (javac)"),
    ("theme", "`vaadin-agent-tools` `check-aura-usage` / `check-theme-mixing`"),
    ("lumo", "Lumo API or `--lumo-*` tokens"),
    ("inline_style", "`getStyle().set(…)`"),
    ("execute_js", "`executeJs` / `callJsFunction`"),
    ("raw_element", "`getElement().setProperty/setAttribute/…`"),
    ("string_sizes", "Sizes as strings (`setWidth(\"16em\")`)"),
]

def load():
    return {b: [json.load(open(f)) for f in sorted(glob.glob(f"{Q}/results/{b}/run-*.json"))] for b in BRANCHES}

def mark(n, total=3):
    return ("✅" if n == total else ("⚠️" if n else "❌")) + f" {n}/{total}"

def view_lines(b, run):
    text = open(f"{Q}/runs/{b}/{run}.patch").read()
    out, on = [], False
    for l in text.splitlines():
        if l.startswith("+++ b/"):
            on = "src/main" in l and "HelloAgentWorld" in l
        elif on and l.startswith("+"):
            out.append(l[1:])
    return "\n".join(out)

def describe(b, run):
    v = view_lines(b, run)
    route = (re.search(r'@Route\(\s*"([^"]*)"', v) or [None, "?"])[1]
    base = (re.search(r"extends (\w+)", v) or [None, "?"])[1]
    return {"route": "/" + route, "base": base,
            "enter": "addClickShortcut" in v,
            "required": bool(re.search(r"setRequired(IndicatorVisible)?\(true\)", v)),
            "error_notification": "NotificationVariant.ERROR" in v,
            "loc": len([l for l in v.splitlines() if l.strip()])}

def main():
    R = load()
    L = []
    L.append("# Quality of the generated code\n")
    L.append("Companion to the cost table in the [README](README.md). Same 18 runs (6 branches × 3), "
             "evaluated after the fact by scripts the agents never saw. Every number below can be "
             "reproduced with `quality/evaluate.py`, `quality/gallery.py` and `quality/report.py`; "
             "each run's unedited output is in `quality/runs/<branch>/run-<n>.patch`.\n")
    L.append("## Summary\n")
    hdr = "| Check | " + " | ".join(f"`{b}`" for b in BRANCHES) + " |"
    L += [hdr, "| --- |" + " :---: |" * len(BRANCHES)]
    req_pass = {b: sum(all(r["acceptance"].get(k, {}).get("pass") for k, _ in REQ) for r in R[b]) for b in BRANCHES}
    L.append("| All 9 prompt requirements met | " + " | ".join(mark(req_pass[b]) for b in BRANCHES) + " |")
    ex = {b: sum(all(r["acceptance"].get(k, {}).get("pass") for k, _ in EXTRA) for r in R[b]) for b in BRANCHES}
    L.append("| Both extras (not asked for) | " + " | ".join(mark(ex[b]) for b in BRANCHES) + " |")
    dep = {b: sum(not r["deprecations"] for r in R[b]) for b in BRANCHES}
    L.append("| No deprecated API | " + " | ".join(mark(dep[b]) for b in BRANCHES) + " |")
    th = {b: sum(all(t.get("ok") and not t.get("findings") for t in r["theme"].values()) for r in R[b]) for b in BRANCHES}
    L.append("| Aura only, no theme findings | " + " | ".join(mark(th[b]) for b in BRANCHES) + " |")
    pt = {b: sum(not any(r["patterns"].values()) for r in R[b]) for b in BRANCHES}
    L.append("| No workarounds (inline style, JS, raw element, string sizes) | " + " | ".join(mark(pt[b]) for b in BRANCHES) + " |")
    ml = {b: sum(not r["mainlayout_changed"] for r in R[b]) for b in BRANCHES}
    L.append("| `MainLayout` left untouched | " + " | ".join(mark(ml[b]) for b in BRANCHES) + " |")
    L.append("\n✅ all three runs · ⚠️ some runs · ❌ no run\n")

    L.append("## 1. Acceptance: does the view do what the prompt asks?\n")
    L.append("`quality/acceptance/HelloAgentWorldAcceptanceTest.java` is a browserless test written "
             "independently of the agents. It is copied into each run's tree and finds the view by name, "
             "so it does not depend on the package, route or field names the agent chose. Before use it "
             "was checked against seven deliberately broken variants of a view (no `@Menu`, no `!`, no "
             "success variant, no primary, wrong position, no empty check, no invalid flag); each one made "
             "exactly the matching checks fail.\n")
    hdr = "| Requirement | " + " | ".join(f"`{b}`" for b in BRANCHES) + " |"
    L += [hdr, "| --- |" + " :---: |" * len(BRANCHES)]
    for k, label in REQ + EXTRA:
        extra = " *(extra)*" if k.startswith("b") else ""
        L.append(f"| {label}{extra} | " + " | ".join(mark(sum(r["acceptance"].get(k, {}).get("pass", False) for r in R[b])) for b in BRANCHES) + " |")
    fails = [(b, r["run"], k, v["message"]) for b in BRANCHES for r in R[b] for k, v in r["acceptance"].items() if not v["pass"]]
    L.append("")
    if fails:
        L.append("Failures:\n")
        L += [f"- `{b}` {run}: `{k}` — {m}" for b, run, k, m in fails]
    else:
        L.append("No check failed in any of the 18 runs.")
    L.append("")

    L.append("## 2. Vaadin API and theme\n")
    L.append("Deprecations come from `javac` (`./mvnw clean test-compile`, deprecation warnings are on in "
             "this pom), limited to files the agent wrote. Theme checks are the `vaadin-agent-tools` CLI "
             "(0.3.2) run as an outside linter on every branch, whether or not the branch uses the plugin. "
             "Workaround patterns are matched on the lines the agent added.\n")
    hdr = "| Check | " + " | ".join(f"`{b}`" for b in BRANCHES) + " |"
    L += [hdr, "| --- |" + " :---: |" * len(BRANCHES)]
    for k, label in CHECKS:
        cells = []
        for b in BRANCHES:
            if k == "deprecations":
                n = [len(r["deprecations"]) for r in R[b]]
                cells.append("✅ 0" if not any(n) else "❌ " + " / ".join(map(str, n)))
            elif k == "theme":
                ok = sum(all(t.get("ok") and not t.get("findings") for t in r["theme"].values()) for r in R[b])
                cells.append(mark(ok))
            else:
                n = [len(r["patterns"][k]) for r in R[b]]
                cells.append("✅ 0" if not any(n) else "❌ " + " / ".join(map(str, n)))
        L.append(f"| {label} | " + " | ".join(cells) + " |")
    L.append("\nCounts per run (run 1 / 2 / 3) where a check found something.\n")
    deps = [(b, r["run"], d) for b in BRANCHES for r in R[b] for d in r["deprecations"]]
    if deps:
        L.append("Deprecated API found:\n")
        seen = {}
        for b, run, d in deps:
            seen.setdefault((b, d["symbol"], d["for_removal"]), set()).add(run)
        for (b, sym, rem), runs in sorted(seen.items()):
            L.append(f"- `{b}` ({', '.join(sorted(runs))}): `{sym}` in the test class"
                     f"{' — **marked for removal**' if rem else ''}")
        L.append("")

    L.append("## 3. How the solutions differ\n")
    L.append("Not graded — the prompt leaves these open. Taken from the view class of each run.\n")
    L.append("| Branch | Run | Route | Layout | Enter submits | Required indicator | Error notification | View LOC |")
    L.append("| --- | --- | --- | --- | :---: | :---: | :---: | ---: |")
    yn = lambda x: "yes" if x else "—"
    for b in BRANCHES:
        for r in R[b]:
            d = describe(b, r["run"])
            L.append(f"| `{b}` | {r['run'][-1]} | `{d['route']}` | `{d['base']}` | {yn(d['enter'])} | {yn(d['required'])} | {yn(d['error_notification'])} | {d['loc']} |")
    L.append("")

    L.append("## 4. Screenshot gallery\n")
    L.append("Every run, same interaction, headless Chromium at 1400×680 with the dev tools turned off. "
             "Left: name `Sebastian` entered and *Show* clicked. Right: *Show* clicked with an empty field. "
             "`06` and `08` also list *Message* in the navigation because they started with the reference view.\n")
    for b in BRANCHES:
        L.append(f"### `{b}`\n")
        L.append("| Run | Name entered | Empty input |")
        L.append("| --- | --- | --- |")
        for r in R[b]:
            n = r["run"]
            a, e = f"docs/gallery/{b}-{n}-name.jpg", f"docs/gallery/{b}-{n}-empty.jpg"
            if os.path.exists(os.path.join(REPO, a)):
                L.append(f"| {n[-1]} | ![{b} {n}: greeting]({a}) | ![{b} {n}: empty input]({e}) |")
            else:
                L.append(f"| {n[-1]} | *(no screenshot — app did not start)* | |")
        L.append("")
    open(os.path.join(REPO, "QUALITY.md"), "w").write("\n".join(L) + "\n")
    print("\n".join(L[:30]))

if __name__ == "__main__":
    main()
