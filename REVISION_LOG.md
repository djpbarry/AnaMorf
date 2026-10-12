# Revision Log

A chronological record of significant development decisions and changes to
AnaMorf, and — more importantly — the mistakes made and lessons learned, so that
the modernisation of this repository does not repeat the missteps already
documented for its upstream dependency
([`IAClassLibrary`](https://github.com/djpbarry/IAClassLibrary/blob/development/REVISION_LOG.md))
and its siblings (`TrackerLibrary`, ADAPT).

AnaMorf is a **Fiji/ImageJ plugin** and a **consumer** of `IAClassLibrary`. It
is not a library, so the "public API" concerns that dominate the sibling logs
apply only lightly here. Its modernisation is instead about build tooling,
robustness, testability, and the migration onto the modernised
`IAClassLibrary v2.x`.

Git commit messages provide the fine-grained record; this file is the distilled,
dated narrative plus the "what not to do again" notes.

## Conventions

- Entries are dated and reference commits and/or `DEVELOPMENT_PLAN.md` phases.
- "Lesson" entries state what went wrong and the rule to apply next time.
- Lessons marked *(inherited)* are carried forward from the sibling
  `REVISION_LOG.md` files, whose modernisation explicitly names AnaMorf's
  dependency chain and intends these rules to guide it.

---

## 2026-10-12 — Phase A complete: build & toolchain (A1–A7)

Started executing `DEVELOPMENT_PLAN.md` Phase A (build, toolchain, CI).

- **A1 complete.** Deleted `build.xml` and `nbproject/` (the malformed legacy
  NetBeans/Ant build), matching IACL Decision 6 and plan Phase A1. Maven is now
  the only build system in the tree.
- **A2 complete.** Added the Maven wrapper (`mvnw`, `mvnw.cmd`,
  `.mvn/wrapper/maven-wrapper.properties`), pinned to Maven 3.9.16, using the
  `only-script` distribution type (self-bootstrapping, no committed
  `maven-wrapper.jar`). Smoke-tested: `mvnw.cmd -version` on JDK 21 resolves and
  runs Maven 3.9.16.
- **A3 complete.** Rewrote `.gitignore` — kept the legacy NetBeans paths for
  existing clones, and added `/target/`, `.idea/`, `*.iml`, `.junie/`, and OS
  files.
- **A5 complete.** Modernised CI (`.github/workflows/maven.yml`): JDK 21
  (`temurin`), `./mvnw verify`, and an `actions/cache` for the Maven repository.
  Actions bumped to `checkout@v4` / `setup-java@v4` / `cache@v4`.
- **A7 complete.** Fixed the malformed `scm` URL (`github.com/github.com/...` →
  `github.com/djpbarry/AnaMorf`) and replaced the `ciManagement` block (`None`)
  with GitHub Actions in `pom.xml`.
- **A4 complete.** Bumped the parent to `pom-scijava:45.1.0`, set
  `scijava.jvm.version=21`, and added an explicit `central` repository (the
  `45.1.0` parent drops the implicit one). Compilation is expected to break from
  here; that break is Phase B's input.
- **A6 complete.** Pinned the IACL dependency to the `v2.0.23` tag (JitPack)
  instead of the 12-character commit `8e10706f5e`; verified the tag exists on
  `djpbarry/IAClassLibrary`.

**Verification.** `./mvnw verify` (JDK 21, IACL `v2.0.23`) completed with BUILD
SUCCESS in ~2 min. The predicted compile break did not occur: the IACL API
surface AnaMorf uses is source-compatible with 2.x. Only warnings surfaced —
deprecation (`OnlyExt`, `SkeletonProcessor`, `FractalEstimator`,
`DataStatistics`, `Revision.getVersionFromPom`), unchecked/raw `ArrayList` usage,
and an annotation-processor `RELEASE_8` vs `-source 21` notice. CI (run #11)
completed green on `development` too, fully meeting Phase A's exit criteria.

### Lesson (new) — the "compile break" was a toolchain mismatch, not an API break

The plan assumed AnaMorf could not compile against IACL 2.x. Once AnaMorf is on
the same toolchain (Java 21 + `pom-scijava:45.1.0`), it compiles cleanly, because
the IACL 2.x break preserved the package root and every API AnaMorf calls (the
deprecated symbols are `@Deprecated`, not removed). **Rule:** "does not compile"
needs a stated toolchain; an API-surface survival check (§1.1) is a stronger
predictor than a version-mismatch heuristic.

(A1 itself was mechanical and already justified by the malformed `build.xml` and
its machine-specific Perl hook; see the 2026-10-11 review and L4/L6.)

---

## 2026-10-12 — Phase B replanned; version → 4.0.0-SNAPSHOT

Re-planned Phase B after discovering the build compiles green (no compile break):

- **B1 is moot** — nothing to "fix what breaks"; the tree already compiles.
- **B3's premise was wrong** — `Batch_Analyser` uses a local hardcoded
  `Revision/Revision.java` (`VERSION=2, revisionNumber=27` → stale
  "AnaMorf v2.027"), not IACL's `Revision.getVersionFromPom`; `javap` confirms
  IACL 2.0.23 has no `Revision.Revision` class.
- **Re-sequenced** to B6 (characterisation) first, then B2, B4, B5, B3, B7.
- **Test data:** 11 legacy regression PNGs committed to
  `src/test/resources/images/`; a JDK 11 (Temurin) install is underway for the
  B6 "before" baseline.

**Decision (version):** bump the project from `3.1.0` to `4.0.0-SNAPSHOT` given
the significant changes ahead; cut `v4.0.0` as the next release.

### Lesson (new) — verify "what actually calls what" before planning a migration

The plan's B3 (and Decision 4) assumed `Revision.getVersionFromPom` was on the
critical path; grep + `javap` showed AnaMorf never calls it. **Rule:** confirm the
real call sites and the real dependency's API surface before writing a migration
step — doc prose can drift from code.

---

## 2026-10-11 — Modernisation kick-off (planning only)

Completed the initial review of AnaMorf and wrote the modernisation plan
(`DEVELOPMENT_PLAN.md`), this log, `RELEASE_NOTES.md`, and `CITATION.cff`.
No code changed yet.

### What the review found

- **AnaMorf is a full major version behind `IAClassLibrary`.** The dependency
  coordinate `com.github.djpbarry:IAClassLibrary:8e10706f5e` resolves — read from
  the packaged `META-INF/maven/.../pom.properties` — to **version 1.0.32**, while
  IACL is now at `2.0.23` (Java 21, `pom-scijava:45.1.0`, GPL-3.0, JUnit 5). The
  coordinate name does not reveal this; the jar is the ground truth.
- **The API surface AnaMorf actually uses survived the 2.x break** (package root
  `net.calm.iaclasslibrary` unchanged; `GenUtils`, `Utilities`, `PropertyWriter`,
  `DataWriter`, `FuzzyThresholder`, `BinaryMaker`, `CurveAnalyser`, `Dijkstra`,
  `Graph`/`Node` all present and source-compatible).
- **Five deprecated symbols are on the critical path:** `OnlyExt`,
  `SkeletonProcessor`, `FractalEstimator`, `DataStatistics`, and
  `Revision.getVersionFromPom` (`@Deprecated` in favour of `Revision.getVersion()`).
- **`FuzzyThresholder.threshold()` changed behaviourally** in IACL 2.x (the
  auto-threshold is now scaled by `binSize`/`histMin`), so pre-processing output
  may drift even though the call site compiles.
- **`build.xml` is malformed** (a stray `</target>` with no opening tag, plus a
  machine-specific Perl version-stamping hook). Maven only.
- **No tests, no tags, no lint/format tooling.** `src/test/java/` is empty.

### The eight resolved decisions (2026-10-11)

Resolved with the maintainer; see `DEVELOPMENT_PLAN.md` section 5 for the full
write-up. In brief:

1. **License — GPL-3.0-or-later** (align with IACL/TrackerLibrary/ADAPT).
2. **Java target — 21**, join the coordinated migration now.
3. **Deprecated IACL symbols — accept the risk for now**, plan alternatives if
   IACL does not un-deprecate before removal.
4. **Output directory naming — rename** (`Revision.getVersionFromPom` →
   `Revision.getVersion()`).
5. **UI — rewrite** (drop the NetBeans `.form`/`GEN`-fenced Swing).
6. **Entry point — no CLI; preserve macro scripting** (remove `Main`, keep the
   `Ext.*` macro functions).
7. **`ResultSummariser` — keep as-is, deferred** (uncertain intent; revisit after
   characterisation).
8. **Citation affiliation — The Francis Crick Institute**.

Still open: the `net.calm` → `io.github.djpbarry` package-root rename (IACL
Decision 7), to be confirmed during Phase G.

### Lesson (new)

The dependency coordinate was misleading. `com.github.djpbarry:IAClassLibrary:8e10706f5e`
looked like a recent pin, but the actual jar was `1.0.32`. **Rule:** when
assessing a JitPack commit-hash pin, read the version from the built artifact
(`jar xf <jar> META-INF/maven/.../pom.properties`), not from the coordinate or
the `pom.xml` `<version>` element.

---

## 2026-10-11 — Downstream consumer identified: TWOMBLI (public API freeze)

Confirmed that AnaMorf is consumed as a library by
[TWOMBLI](https://github.com/FrancisCrickInstitute/TWOMBLI), which pins it at
`com.github.djpbarry:Anamorf:d610176007` and calls it **directly from Java** (not
via the macro layer). This converts several previously "internal" details into an
external contract and changes the plan's risk profile.

What TWOMBLI relies on (frozen):

- `net.calm.anamorf.Batch_Analyser` — constructor `(boolean, File, Properties)`,
  `run(String)`, and the **static** `getProps()`, which TWOMBLI mutates via
  `Batch_Analyser.getProps().setProperty(...)` between successive `run(null)`
  calls (one per curvature window).
- `net.calm.anamorf.params.DefaultParams` — no-arg constructor and every
  `public static final String` label, whose *string values* are the keys in the
  `AnamorfProperties.xml` TWOMBLI ships and loads with `loadFromXML`.
- The output contract: exactly one `*_Output` directory containing `results.csv`,
  plus `*_Output_(n)` directories (one per curvature-window run), from
  `GenUtils.openResultsDirectory`.
- The JitPack coordinate `com.github.djpbarry:Anamorf`, derived from the **GitHub
  repo name**, and the pinned commit `d610176007`.

Plan consequences recorded in `DEVELOPMENT_PLAN.md` §1.5 and `AGENTS.md`:

1. Phase E1 ("de-static `props`") must keep `getProps()` static or be done in
   lockstep with TWOMBLI.
2. The G2 package-root rename (`net.calm` → `io.github.djpbarry`) must include
   TWOMBLI.
3. No ADAPT-style output restructure without a TWOMBLI-compatible shim.
4. The Decision-4 output-folder rename is still safe (TWOMBLI matches the `Output`
   suffix, not the version prefix) and is actually a fix — `getVersionFromPom`
   returns an error string when the JitPack-built jar is run from a working
   directory with no `pom.xml`.

### Lesson (new)

"Not referenced inside this repo" is **not** the test for a plugin's public
surface. AnaMorf's `Batch_Analyser`/`DefaultParams` are imported by an external
project, so their signatures, label strings, and output directory layout are a
contract even though nothing in AnaMorf itself declares them as such. **Rule:**
before any "internal" rename, de-static, or output restructure, check downstream
consumers (TWOMBLI) — not just the repo's own call sites.

---

## Lessons learned (mistakes to avoid)

Carried forward from the sibling logs and re-stated only as they apply to AnaMorf.

### L1 — Decide the Java target before touching build/CI *(inherited)*

The siblings reversed a Java 11 → Java 21 decision mid-stream. AnaMorf has now
settled this (Java 21, join now — Decision 2), but the coordinated bump must be
a single, planned change (Phase A4/A5), not an incremental drift.

**Rule:** confirm the cross-project Java / parent-POM / TrackMate version and
dependency pins in writing before any build or CI edit.

### L2 — Use consistent semver tag names *(inherited)*

AnaMorf has **no `vX.Y.Z` tags**; its version is a bare `3.1.0`. The siblings
standardised on `vX.Y.Z`.

**Rule:** adopt `vX.Y.Z` from the first tag onward (see Phase F4).

### L3 — Resolve the licence before tagging *(inherited)*

AnaMorf had the exact contradiction the siblings fixed: GPLv3 source headers but
BSD-2 `pom.xml`. Resolved to GPL-3.0-or-later (Decision 1).

**Rule:** align `pom.xml` (`<licenses>`, `license.licenseName`,
`license.copyrightOwners`) with the root `LICENSE` before tagging; treat the
source-header tidy-up as a separate, deferred item.

### L4 — "No references in this repo" does not apply, but "commented code is stale" does *(inherited, adapted)*

AnaMorf is a plugin, not a library, so the "external consumers" concern is moot.
But `Main.java` is almost entirely commented-out experiments and
`ResultSummariser` is half disabled — both are stale and misleading.

**Rule:** strip commented/experimental code (recoverable from git) rather than
leaving it to mislead; where a block was disabled deliberately, record *why*
before changing it.

### L5 — Check the parent-POM move against the repository list *(inherited)*

`pom-scijava:45.1.0` drops the implicit Maven Central, so a consumer on that
parent must declare `central` explicitly. AnaMorf's current `pom.xml` does not
declare `central` at all — this will bite in Phase A4.

**Rule:** when moving to `pom-scijava:45.1.0`, declare `central` first in
`<repositories>`.

### L6 — The `main-class` was a scratchpad, not a CLI *(new, AnaMorf-specific)*

`net.calm.anamorf.Main` runs `Batch_Analyser.run(null)` then `System.exit(0)`;
the rest is commented-out OneDrive experiments. It was declared `main-class`
but is not a usable entry point. The real scriptable surface is the ImageJ
`Ext.*` macro layer.

**Rule:** do not treat `Main` as a CLI, and do not let it regress the macro
entry point (Decision 6).
