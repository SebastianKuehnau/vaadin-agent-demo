#!/usr/bin/env python3
"""Screenshot gallery of the benchmark runs.

    <venv-with-playwright>/bin/python quality/gallery.py <workdir> [branch/run-n ...]

Uses the working trees that quality/evaluate.py rebuilt in <workdir>. For each run it
starts the app (`./mvnw spring-boot:run`, dev tools off so the dev-mode badge does not
cover the view), opens the generated view in headless Chromium at 1400x680 and takes
two screenshots with the same interaction everywhere:

  <branch>-<run>-name.jpg   name "Sebastian" entered, Show clicked
  <branch>-<run>-empty.jpg  field left empty, Show clicked

Images go to docs/gallery/. Needs Playwright for Python and a Chromium
(CHROMIUM, default /usr/bin/chromium).
"""
import glob, os, re, signal, subprocess, sys, time, urllib.request
from playwright.sync_api import sync_playwright

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(REPO, "docs", "gallery")
CHROMIUM = os.environ.get("CHROMIUM", "/usr/bin/chromium")

def route_of(tree):
    f = [p for p in glob.glob(f"{tree}/src/main/java/**/*.java", recursive=True)
         if re.search(r"HelloAgentWorld", os.path.basename(p), re.I) and "@Route" in open(p).read()][0]
    m = re.search(r'@Route\(\s*(?:value\s*=\s*)?"([^"]*)"', open(f).read())
    if m:
        return m.group(1)
    name = re.sub(r"(View|Route)$", "", os.path.basename(f)[:-5])
    return re.sub(r"(?<!^)(?=[A-Z])", "-", name).lower()

def wait_up(log, timeout=240):
    t = time.time()
    while time.time() - t < timeout:
        if os.path.exists(log) and "Application running at" in open(log, errors="replace").read():
            return True
        time.sleep(2)
    return False

def shoot(page, url, value, path):
    page.goto(url)
    page.wait_for_selector("vaadin-text-field")
    page.wait_for_timeout(1200)
    field = page.locator("vaadin-text-field input").first
    if value:
        field.fill(value)
    page.locator("vaadin-button").filter(has_text=re.compile("show", re.I)).first.click()
    page.wait_for_timeout(1200)
    page.screenshot(path=path, type="jpeg", quality=82)

def main():
    work = os.path.abspath(sys.argv[1]); only = set(sys.argv[2:])
    os.makedirs(OUT, exist_ok=True)
    for tree in sorted(glob.glob(f"{work}/0*-run-*")):
        key = os.path.basename(tree)               # e.g. 02-vanilla-run-1
        b, run = key.rsplit("-run-", 1)
        if only and f"{b}/run-{run}" not in only:
            continue
        log = f"{tree}/gallery-app.log"
        if os.path.exists(log):
            os.remove(log)
        app = subprocess.Popen(
            "./mvnw -B -ntp -o -q spring-boot:run "
            "'-Dspring-boot.run.arguments=--vaadin.launch-browser=false --vaadin.devmode.devTools.enabled=false'",
            cwd=tree, shell=True, stdout=open(log, "w"), stderr=subprocess.STDOUT, start_new_session=True)
        try:
            if not wait_up(log):
                print(f"{key}: app did not start"); continue
            url = f"http://localhost:8080/{route_of(tree)}"
            with sync_playwright() as p:
                br = p.chromium.launch(executable_path=CHROMIUM, args=["--no-sandbox"])
                page = br.new_page(viewport={"width": 1400, "height": 680})
                shoot(page, url, "Sebastian", f"{OUT}/{b}-run-{run}-name.jpg")
                shoot(page, url, "", f"{OUT}/{b}-run-{run}-empty.jpg")
                br.close()
            print(f"{key}: ok ({url})", flush=True)
        finally:
            os.killpg(app.pid, signal.SIGTERM)
            for _ in range(30):
                try:
                    urllib.request.urlopen("http://localhost:8080/", timeout=1)
                    time.sleep(1)
                except Exception:
                    break

if __name__ == "__main__":
    main()
