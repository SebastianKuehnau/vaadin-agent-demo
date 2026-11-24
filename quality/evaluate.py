#!/usr/bin/env python3
"""Quality evaluation of the benchmark runs.

    python3 quality/evaluate.py <workdir> [branch/run-n ...]

For every run listed in quality/runs/manifest.txt (or the ones given), the script
1. rebuilds the run's working tree: clone of this repo at the run's setup commit plus
   quality/runs/<branch>/run-<n>.patch (the agent's unedited output),
2. compiles with javac deprecation warnings (on by default in this pom) and keeps the
   warnings that point into files the agent wrote,
3. runs the independent acceptance suite quality/acceptance/HelloAgentWorldAcceptanceTest
   inside that tree,
4. runs the theme checks of vaadin-agent-tools (check-aura-usage, check-theme-mixing),
5. scans the lines the agent added for Vaadin workarounds.
Results go to quality/results/<branch>/run-<n>.json.

Needs: JDK 25, a warm ~/.m2 (runs Maven offline), and the vaadin-agent-tools binary
(VAADIN_AGENT_TOOLS, default: the Claude Code plugin cache).
"""
import glob, json, os, re, shutil, subprocess, sys, xml.etree.ElementTree as ET

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
Q = os.path.join(REPO, "quality")
TOOLS = os.environ.get("VAADIN_AGENT_TOOLS") or (sorted(glob.glob(os.path.expanduser(
    "~/.claude/plugins/cache/vaadin-marketplace/vaadin-agent-tools/*/bin/vaadin-agent-tools"))) or [None])[-1]

# Patterns on lines the agent added (.java / .css). Each is a workaround where Vaadin
# has typed API, or Lumo-only styling in this Aura project.
PATTERNS = {
    "lumo": r"LUMO_|--lumo-|\bLumo\.",
    "inline_style": r"getStyle\(\)\s*\.\s*set",
    "execute_js": r"executeJs|callJsFunction",
    "raw_element": r"getElement\(\)\s*\.\s*(setProperty|setAttribute|appendChild|executeJs)",
    "string_sizes": r"\.set(Width|Height|MinWidth|MaxWidth|MinHeight|MaxHeight)\(\s*\"",
}

def sh(cmd, cwd, timeout=900):
    p = subprocess.run(cmd, cwd=cwd, shell=True, capture_output=True, text=True, timeout=timeout)
    return p.returncode, p.stdout + p.stderr

def manifest():
    out = []
    for line in open(os.path.join(Q, "runs", "manifest.txt")):
        b, run, setup = line.split()
        out.append((b, run, setup.split("=")[1]))
    return out

def evaluate(workdir, b, run, setup):
    w = os.path.join(workdir, f"{b}-{run}")
    shutil.rmtree(w, ignore_errors=True)
    rc, out = sh(f"git clone -q '{REPO}' '{w}'", REPO)
    rc2, out2 = sh(f"git checkout -q {setup}", w)
    if rc or rc2 or sh("git rev-parse HEAD", w)[1].strip() != setup:
        raise RuntimeError(f"{b}/{run}: could not check out {setup}: {out}{out2}")
    patch = os.path.join(Q, "runs", b, f"{run}.patch")
    rc, out = sh(f"git apply --binary '{patch}'", w)
    if rc:
        raise RuntimeError(f"{b}/{run}: patch does not apply: {out}")
    text = open(patch).read()
    files = re.findall(r"^\+\+\+ b/(\S+)", text, re.M)
    added = []  # (file, line) of added lines
    cur = None
    for l in text.splitlines():
        if l.startswith("+++ b/"):
            cur = l[6:]
        elif l.startswith("+") and not l.startswith("+++") and cur and cur.endswith((".java", ".css")):
            added.append((cur, l[1:]))
    r = {"branch": b, "run": run, "files": files,
         "mainlayout_changed": any(f.endswith("base/ui/MainLayout.java") for f in files)}

    # 2. deprecation warnings in agent-written files
    rc, out = sh("./mvnw -o -B clean test-compile", w)
    r["compiles"] = rc == 0
    dep = []
    for m in re.finditer(r"\[WARNING\] (\S+\.java):\[(\d+),\d+\] (.*(?:deprecated|removal).*)", out):
        path = os.path.relpath(m.group(1), w)
        if path in files:
            sym = re.sub(r" in \S+ has been deprecated.*| has been deprecated.*", "", m.group(3)).strip()
            dep.append({"file": path, "line": int(m.group(2)), "symbol": sym,
                        "for_removal": "removal" in m.group(3)})
    r["deprecations"] = dep

    # 3. acceptance suite
    dst = os.path.join(w, "src/test/java/dev/vaadin/agentdemo/acceptance")
    os.makedirs(dst, exist_ok=True)
    shutil.copy(os.path.join(Q, "acceptance", "HelloAgentWorldAcceptanceTest.java"), dst)
    sh("./mvnw -o -B test -Dtest=HelloAgentWorldAcceptanceTest -Dsurefire.failIfNoSpecifiedTests=false", w)
    checks = {}
    for x in glob.glob(os.path.join(w, "target/surefire-reports/TEST-*Acceptance*.xml")):
        for tc in ET.parse(x).getroot().iter("testcase"):
            bad = tc.find("failure") if tc.find("failure") is not None else tc.find("error")
            checks[tc.get("name")] = {"pass": bad is None,
                                      "message": None if bad is None else (bad.get("message") or "")[:200]}
    r["acceptance"] = dict(sorted(checks.items()))

    # 4. theme checks
    r["theme"] = {}
    if TOOLS:
        for tool in ("check-aura-usage", "check-theme-mixing"):
            rc, out = sh(f"'{TOOLS}' {tool} --json .", w)
            try:
                j = json.loads(out[out.index("{"):])
                r["theme"][tool] = {"ok": j.get("ok"), "findings": j.get("findings", []),
                                    "themes": j.get("themesLoaded")}
            except ValueError:
                r["theme"][tool] = {"ok": None, "error": out[:200]}

    # 5. workaround patterns in added lines
    r["patterns"] = {k: [f"{f}: {line.strip()[:120]}" for f, line in added if re.search(p, line)]
                     for k, p in PATTERNS.items()}
    return r

def main():
    workdir = os.path.abspath(sys.argv[1])
    only = set(sys.argv[2:])
    os.makedirs(workdir, exist_ok=True)
    for b, run, setup in manifest():
        if only and f"{b}/{run}" not in only:
            continue
        r = evaluate(workdir, b, run, setup)
        os.makedirs(os.path.join(Q, "results", b), exist_ok=True)
        json.dump(r, open(os.path.join(Q, "results", b, f"{run}.json"), "w"), indent=2)
        acc = r["acceptance"]
        print(f"{b}/{run}: acceptance {sum(c['pass'] for k, c in acc.items() if k.startswith('r'))}/"
              f"{sum(1 for k in acc if k.startswith('r'))}, extras {sum(c['pass'] for k, c in acc.items() if k.startswith('b'))}/"
              f"{sum(1 for k in acc if k.startswith('b'))}, deprecations {len(r['deprecations'])}, "
              f"theme ok {all(t.get('ok') for t in r['theme'].values())}, "
              f"patterns {sum(len(v) for v in r['patterns'].values())}", flush=True)

if __name__ == "__main__":
    main()
