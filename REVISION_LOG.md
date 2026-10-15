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

## 2026-10-15 — Fixed cross-plugin version resource collision

A GUI run in Fiji produced an output folder named `AnaMorf v4.0.29_Output`, while
the headless runner produced the expected `AnaMorf v4.0.0-SNAPSHOT_Output`.
Root cause: `Batch_Analyser.getVersion()` read the root-level
`/project.properties` resource, and **ADAPT ships the same generic resource name**
(`version=4.0.29`). With both `adapt-4.0.29.jar` and `anamorf-4.0.0-SNAPSHOT.jar`
in Fiji's `plugins/`, the classloader returned whichever `project.properties` came
first in its search order — ADAPT, alphabetically — so AnaMorf titled itself with
ADAPT's version. The headless runner was unaffected because it runs from
`target/classes` and never loads ADAPT.

Fix: moved the filtered template from `src/main/resources/project.properties` to
`src/main/resources/net/calm/anamorf/version.properties` and updated `getVersion()`
to read `/net/calm/anamorf/version.properties`. The package-qualified path is
unique to AnaMorf, so the collision is gone.

**Lesson.** Never put a plugin's version (or any lookup) resource at the classpath
root under a generic name. Two plugins can both legitimately ship `project.properties`
(or `version.properties`, `plugins.config`, etc.), and `ClassLoader.getResource`
is first-match-wins with no ownership guarantee. Scope shared-resource lookups to
the plugin's own package path, or use a clearly namespaced filename.

Verified: `mvn test` 12/12 green; reflection check with `adapt-4.0.29.jar` first
on the classpath returns `4.0.0-SNAPSHOT`; reinstalled jar contains
`net/calm/anamorf/version.properties` and no root `project.properties`.

---

## 2026-10-15 — D6 done: fixed `searchImage` ROI bounds

`searchImage` iterated its raster scan with `y < bounds.height` and
`x < bounds.width`, which is correct only when the ROI starts at (0,0) — i.e. a
full-image ROI. Fixed to `y < bounds.y + bounds.height` and
`x < bounds.x + bounds.width`, so any ROI is handled. AnaMorf itself always passes
a full-image ROI (the `roi` argument is `null` at the sole call site), so there is
no behaviour change; `mvn test` 12/12 green.

---

## 2026-10-15 — D5 done: explicit label-to-flag output bitmask

Replaced the positional output-bitmask mechanism in `Batch_Analyser` — the
`getOptions()` boolean array and `toOutputData(boolean[])` (2^n indexing) — with
`computeOutputData(Properties)`, which maps each `DefaultParams` label directly
to its flag via `|=`. This removes the silent-corruption risk if the array order
ever drifted. `getOptions()` and `toOutputData()` are deleted; the C2 test now
asserts `computeOutputData` against explicit flags. `mvn test` 12/12 green.

---

## 2026-10-14 — Fixed PNG file-filter case-sensitivity

Live-testing in Fiji surfaced "no images of type PNG" for lowercase `Image_*.png`.
Root cause: `getFileExtensions` returned `{"PNG"}` (uppercase) for the default
case, while `FileExtensionFilter` → `FilenameUtils.isExtension` is case-sensitive
(commons-io 2.20.0), so uppercase "PNG" did not match lowercase ".png". The TIF/JPG
cases already returned lowercase, so the default was simply inconsistent.

Fix: `getFileExtensions` now lowercases the default case (`upper.toLowerCase()`),
and the `Batch_AnalyserTest` was updated (`getFileExtensionsLowercasesOtherFormats`).
`mvn test` 13/13 green; re-installed into Fiji.

Also made `install-to-fiji.cmd` clean a lowercase `iaclasslibrary-*.jar` left in
`plugins/` by older builds.

---

## 2026-10-14 — D2 done: decomposed `analyseImage` helpers

Extracted five helpers from `Batch_Analyser.analyseImage` (the ~250-line god
method), prioritising pure math for Phase C testability:

- `static double circularity(pixArea, objectPerim)` — pure.
- `static double lacunarity(variance, meanSq)` — pure.
- `boolean meetsMorphologicalCriteria(objArea, objCirc)` — threshold check.
- `double fourierFractalDimension(pixArea, xSum, ySum, objRoi, imageRes)` —
  also dropped dead `polyproc` draw code and the now-unused `Color` import.
- `double[] boxFractalDimension(binProc, objBox, wholeImage)`.

The area-counting loop, skeleton-analysis block, and results-writing block remain
inline: they mutate the cropped `binProc`/`objBox` coordinate state and are
riskier to extract. `mvn test` 13/13 green.

---

## 2026-10-14 — D4 done: named `fill()` flood-fill sentinel

`Batch_Analyser.fill()` hardcoded `127` as its flood-fill marker (both the
`setColor(127)` seed and the `pixels[i] == 127` remap test). Extracted a
`FLOOD_FILL_SENTINEL = 127` constant so the magic value is self-documenting and
has one definition point. No behaviour change; `mvn test` 13/13 green.

The same `fill` logic is duplicated in IACL's `BinaryMaker.fill` (out of scope —
we consume it, we don't own it). Noted in `DEVELOPMENT_PLAN.md` D4.

---

## 2026-10-14 — D3 done: removed dead loop in `showGUI()`

`Batch_Analyser.showGUI()` had a `while (!valid)` loop whose body
unconditionally returned (it set `valid = true` then returned either way), so
the "loop" was dead control flow. Reduced to: construct the `UserInterface`,
`setVisible(true)`, `return !gui.exitProgram()`. No behaviour change. `mvn test`
still 13/13 green.

---

## 2026-10-14 — D1 done: removed the `Main` scratchpad

Removed the `Main.java` scratchpad (which ran `Batch_Analyser.run(null)` then
`System.exit(0)`, with commented-out OneDrive experiment paths) and dropped the
now-unreferenced `main-class` property from `pom.xml`. AnaMorf is loaded solely
via its two `plugins.config` commands (the macro layer is the scriptable entry
point and is unchanged). `mvn test` still 13/13 green. `AGENTS.md` updated to
remove the `Main` row and state there is no standalone entry point.

---

## 2026-10-14 — AnalyzeSkeleton overlap reviewed (B7 note, planning only)

Reviewed [fiji/AnalyzeSkeleton](https://github.com/fiji/AnalyzeSkeleton) (GPL-3.0,
unmaintained since 2022) against AnaMorf's aim. Conclusion: overlap is confined to
the single skeleton-analysis stage — AnaMorf's `HyphalAnalyser`/`SkeletonPruner`
(endpoint/branchpoint counts + branch-length tracing) maps onto AnalyzeSkeleton's
branch/junction/end-point/triple/quadruple-point classification. Everything else
AnaMorf does (segmentation→skeletonisation pipeline, fractal dimension,
lacunarity, curvature, per-object area/circularity) has no counterpart there, and
AnalyzeSkeleton additionally handles 3D and assumes a pre-skeletonised input.

**Relevance to B7:** AnalyzeSkeleton is a candidate **source to vendor** the
deprecated IACL `SkeletonProcessor` logic (its `GraphPruning`/`Vertex`/`Edge`) if
IACL ever removes it — not a dependency. GPL-3.0 is licence-compatible (Decision 1).
Recorded as a B7 note in `DEVELOPMENT_PLAN.md`; no code changed.

---

## 2026-10-13 — C4 scaffolding landed (live-Fiji `bin/` convention)

Created the `bin/` live-test infrastructure, mirroring ADAPT's convention:

- **`bin/local-env.cmd.example`** (committed template) + gitignored
  `bin/local-env.cmd`; `.gitignore` updated to exclude the latter.
- **`bin/install-to-fiji.cmd`** — builds (`mvnw.cmd -q -DskipTests clean
  package`) and installs `anamorf-*.jar` → `Fiji/plugins/` and
  `IAClassLibrary-*.jar` → `Fiji/jars/`.
- **`bin/smoke-test-fiji.cmd`** — headless `Class.forName` load-check of the two
  `plugins.config` classes; asserts `ALL_ANAMORF_PLUGINS_LOADABLE`.
- **`bin/RunAnamorf.java` + `bin/run-anamorf.cmd`** — headless runner: converts
  the RGB regression images to 8-bit grey (`convertToByte(false)`) and drives
  `new Batch_Analyser(true, dir, props).run(null)` in macro mode. Runs against
  `target/classes` + `target/*` (no Fiji install needed for the run itself).
  `javac` against the classpath is clean.
- **`bin/BaselineTool.java` + `compute-baseline.cmd` / `verify-baseline.cmd`** —
  SHA-256 `results.csv`/`CurvatureValues.csv` against a committed manifest.
  `BaselineTool` was self-tested (compute → manifest, verify → `BASELINE_PASS`).

**Remaining (needs local env):** create `bin/local-env.cmd` (FIJI_DIR/JAVA_HOME),
then `install-to-fiji` → `run-anamorf` → `compute-baseline` to seed the golden
manifest from the B6 output, and `verify-baseline` as the future gate.

### Resolved — baseline portability (directory-label row removed)

The live run (`run-anamorf.cmd`, Fiji at
`C:\Users\davej\fiji-latest-portable-nojava\Fiji`, JDK 21 at
`C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot`) completed and
produced a full `results.csv` (masks/skeletons/curve maps + `properties.xml`).
The first data row of `results.csv` was the **machine-specific absolute work
directory path** — `Batch_Analyser.run` called
`resultsTable.addLabel(currentDirectory.getAbsolutePath())`, so that label (with
all-zero metrics) was the directory path, not an image name.

**Fix (chosen over the stopgap):** removed the directory-label row from
`Batch_Analyser.run` (`incrementCounter()` + `addLabel(getAbsolutePath())`
deleted), so `results.csv` now starts with the header and then real per-image
data. This is safe — every image writes its own label during analysis, and the
directory row only carried `NaN`/zeros. Reverted the `RunAnamorf`/
`run-anamorf.cmd` `C:\anamorf-live` stopgap (a fixed Windows drive was never a
real fix). Verified determinism: two headless runs produced an identical
`results.csv` (SHA-256 `7dbd6502…`), so the baseline is now portable.

---

## 2026-10-13 — C3 landed: headless ImageJ-primitive tests

Added three headless tests exercising the `ij.process`/`ij.measure` plain-class
surface (no Fiji/GUI bootstrap needed):

- **`Batch_AnalyserIOTest.saveResultsWritesIso8859Csv`** — builds a `ResultsTable`
  (with a `µ` heading), assigns it to `Batch_Analyser.resultsTable`, calls
  `saveResults`, and asserts the `results.csv` schema plus the ISO-8859-1 `µ`
  (0xB5) byte (not UTF-8 `0xC2 0xB5`).
- **`HyphalAnalyserTest.analysesStraightLineLengthTipsAndHgu`** — a synthetic
  11-pixel straight skeleton → length 11, 2 tips, 0 branchpoints, HGU 5.5.
- **`SkeletonPrunerTest.traceBranchReturnsLinePath`** — `traceBranch` on a straight
  line returns the expected 11-pixel path.

`mvn test` → 13/13 green (10 C2 + 3 C3). Confirms `ResultsTable`/`ByteProcessor`
and IACL's `SkeletonProcessor`/`Dijkstra` are plain classes that run headlessly —
so C3 stays in JUnit rather than shifting to the live-Fiji runner (the C3 note's
"straddle the line" concern was resolved in favour of JUnit).

---

## 2026-10-13 — C4 re-planned to follow ADAPT's live-Fiji convention (planning only)

Reviewed ADAPT's `bin/` "live test" convention and re-scoped AnaMorf's C4 to
match it instead of a fragile JUnit end-to-end test:

- **`bin/local-env.cmd.example`** (committed) + gitignored `local-env.cmd`
  (`FIJI_DIR`/`JAVA_HOME`), `call`ed by every script.
- **`bin/install-to-fiji.cmd`** — build + install plugin → `Fiji/plugins/`,
  IACL → `Fiji/jars/`.
- **`bin/smoke-test-fiji.cmd`** — headless `Class.forName` load-check of the two
  `plugins.config` classes.
- **headless runner** — `Batch_Analyser.run(null)` in macro mode over the
  regression images → `results.csv`.
- **`bin/compute-baseline.cmd` / `verify-baseline.cmd`** + a `BaselineTool` —
  SHA-256 `results.csv` (+ `CurvatureValues.csv`) against a committed manifest,
  mirroring ADAPT's `bin/BaselineTool.java`.

Also noted that ADAPT keeps JUnit strictly pure-logic and pushes ImageJ-adjacent
coverage into the live-Fiji layer; AnaMorf's C3 (plain `ResultsTable`/
`ByteProcessor`) straddles that line and will shift to the runner if it needs
`ij.IJ`/AWT. C1/C2 remain unchanged (pure logic in `mvn test`).

---

## 2026-10-13 — C1 + C2 landed: JUnit 5 harness + pure-logic tests

Started Phase C. Added the JUnit 5 harness and the first real tests.

- **C1** — `junit-jupiter-api` + `junit-jupiter-engine` (scope `test`, no
  `<version>`; parent-managed). Confirmed the parent wires surefire + the JUnit
  Platform + JaCoCo (`JUnitPlatformProvider` auto-detected). `mvn test` runs.
- **Testability refactors** — widened `Batch_Analyser.getFileExtensions` /
  `getVersion` and `ResultSummariser.removeZeros` to package-private, and
  extracted `static int toOutputData(boolean[])` out of `setOutputs`.
- **C2** — 10 tests:
  - `getFileExtensions` (TIF/TIFF, JPG/JPEG aliasing + passthrough)
  - `getOptions` positional `boolean[]` ↔ flag ordering
  - `toOutputData` bitmask (empty, mixed, full = 1023) + flag constants
  - `getVersion` (filtered `project.properties`)
  - `removeZeros` positive-only compaction

`mvn test` → 10/10 green. Note: the `Batch_Analyser` static initialiser prints
`IJ.log("Initialising properties...")` to stdout headlessly (harmless; the
static-`props` init is E1's problem, not a test concern).

---

## 2026-10-13 — Phase C planned (planning only)

Replaced the sparse Phase C in `DEVELOPMENT_PLAN.md` with an ordered execution
plan, grounded in what is actually testable in AnaMorf:

- **C1** — JUnit 5 harness (parent-managed, no `<version>`).
- **C2** — pure logic (no ImageJ): `getFileExtensions`, `getOptions` +
  `toOutputData` bitmask, `getVersion`, `ResultSummariser.removeZeros`.
- **C3** — headless `ij.process`/`ij.measure` primitives: `saveResults`,
  `SkeletonPruner`, `HyphalAnalyser` on synthetic skeletons.
- **C4** — full headless pipeline golden diff (repeatable B6), possibly as a
  committed `bin/` runner.

Notes captured for execution: tests live in `net.calm.anamorf` so they can reach
package-private members; three `private` methods (`getFileExtensions`,
`getVersion`, `removeZeros`) plus an extracted `toOutputData` need widening to
package-private. `ij.process`/`ij.measure` classes are plain objects (no headless
ImageJ bootstrap needed for C3); C4 needs `new ij.ImageJ(NO_SHOW)` and is the
flaky/high-effort tier.

---

## 2026-10-13 — B5 done: `GenVariables.ISO` → `StandardCharsets`

Replaced the `GenVariables.ISO` charset in `Batch_Analyser.saveResults` with
`java.nio.charset.StandardCharsets.ISO_8859_1` and removed the now-unused
`GenVariables` import. No behaviour change (IACL 2.x already exposes `GenVariables.ISO`
as `StandardCharsets.ISO_8859_1`). `mvn -q -DskipTests compile` green.

This clears the last mechanical deprecation migration: the only deprecated IACL
symbols still referenced are `SkeletonProcessor` and `FractalEstimator`, which
B7 keeps on the critical path by decision (no drop-in replacement yet).

---

## 2026-10-13 — B4 done: `ResultSummariser` off `DataStatistics`

Migrated `ResultSummariser` off the deprecated `IAClasses.DataStatistics` onto
`org.apache.commons.math3.stat.descriptive.DescriptiveStatistics`:

- Rewrote `printStats`/`removeZeros` to mean + population std-dev only
  (confidence-interval row stays removed, per Decision 7).
- Removed the Tier 3.2 dead code: the commented per-column lookups and
  `printStats` calls, the CI row, and the now-unused static `alpha` field (the
  `ResultSummariser(double)` constructor is retained as a no-op for API
  compatibility).
- Preserved population std-dev semantics: `DataStatistics.getStdDev()` divides by
  `N` (`sqrt(sumvar/N)`), whereas `DescriptiveStatistics.getStandardDeviation()`
  divides by `N-1`, so the code uses `Math.sqrt(getPopulationVariance())`.
- Fixed a latent inconsistency: the old `calcMean(data)` iterated the *full*
  column array while `calcStdDev` used `dataLength`, so the HGU "Mean" row was
  over all values but the "Standard Deviation" row over only the positive
  ("clean") ones. Both now use the same subset.

`mvn -q -DskipTests compile` green. `ResultSummariser` remains dead code (no call
sites in this repo), so this has no runtime effect; it is purely dependency
cleanup ahead of Phase C.

---

## 2026-10-13 — B2 done + version resource actually created

Closed two Phase B items left dangling by `aaa520e` / `4986b6e`:

- **B2 — `OnlyExt` → `FileExtensionFilter`.** `Batch_Analyser.analyseFiles` now
  builds a `net.calm.iaclasslibrary.IO.File.FileExtensionFilter` via a new
  `getFileExtensions(String)` helper, which reproduces `OnlyExt`'s
  `TIF`/`TIFF` and `JPG`/`JPEG` special-casing with symmetric aliasing
  (`TIF`/`TIFF` → `{"tif","tiff"}`, `JPG`/`JPEG` → `{"jpg","jpeg"}`), as decided
  on 2026-10-12. Removed the `IAClasses.OnlyExt` import. (`4986b6e` had only
  documented this decision; the code was still using the deprecated `OnlyExt`.)
- **Version resource.** `src/main/resources/project.properties`
  (`version=${project.version}`) was absent even though `aaa520e` claimed to add
  it, so `Batch_Analyser.getVersion()` returned `"unknown"` (→ `AnaMorf vunknown`).
  Created the file; Maven resource filtering now emits `version=4.0.0-SNAPSHOT`
  (verified in `target/classes/project.properties`).

`mvn -q -DskipTests compile` and `mvn -q verify` both green.

### Lesson (new) — a "resource filtering" change needs the template in the tree

`aaa520e` added the pom.xml `<resources><filtering>` config and the Java reader,
but never committed the `version=${project.version}` template, so the feature
silently degraded to `"unknown"` until the missing file was noticed. **Rule:** a
resource-filtering change is three pieces — the filtering config, the
`${...}` template file, and the reader — and the template must actually be
present (not just referenced) before the step is "done".

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
- **B2 approach:** migrate `OnlyExt` → `FileExtensionFilter` with symmetric
  `TIF`/`TIFF` + `JPG`/`JPEG` aliasing. Deferred: Bio-Formats-based discovery,
  with a headless caveat (readers are probably headless-safe inside an ImageJ/FIJI
  plugin, but standalone headless scripts have failed; TWOMBLI runs headless).
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

## 2026-10-12 — B6 characterisation: no numerical drift

Ran AnaMorf headlessly (macro mode) on the 11 regression images, once with the
pre-Phase-A build (IACL 1.0.32, JDK 11) and once with the current build
(IACL 2.0.23, JDK 21), and diffed `results.csv`.

- **Result: byte-identical** (both MD5 `586ddd9c…`). No numerical drift from the
  IACL 1.0.32 → 2.0.23 bump, nor from the parent/ImageJ/bio-formats/Java changes.
- Confirms the `FuzzyThresholder` `binSize`/`histMin` change is a no-op for 8-bit
  input (`binSize=1`, `histMin=0`), as the plan's "shortcut" hypothesised.
- Note: the test images are RGB; AnaMorf requires greyscale, so they were
  converted RGB → 8-bit grey before the run.

### Lesson (new) — characterise with a real before/after, not a shortcut

The plan offered a "shortcut" (infer no-drift from `binSize=1`). The full run was
cheap — a JDK 11 worktree plus a ~30-line headless runner — and gave a definitive,
reproducible answer rather than an inference. **Rule:** when the correctness risk
is real, build the actual "before" and "after" and diff; the shortcut is a
fallback, not a first choice.

### Housekeeping (pending)

- The `before` build lives in a throwaway worktree (`git worktree remove` once
  B6 is closed).
- Scratch files under `C:/Users/barryd/b6/` are outside the repo, not committed.

---

## 2026-10-12 — B3 versioning: resource-filtered version

Implemented the versioning re-scope (Decision 9):

- Bumped `pom.xml` to `4.0.0-SNAPSHOT`.
- Deleted the stale local `src/main/java/Revision/Revision.java` (hardcoded
  `VERSION=2, revisionNumber=27` → "AnaMorf v2.027").
- Added `src/main/resources/project.properties`
  (`version=${project.version}`) with Maven resource filtering enabled.
- `Batch_Analyser.title` now reads that resource → `"AnaMorf v4.0.0-SNAPSHOT"`.

Chose option (c) resource filtering over (b) manifest version because (b) returns
null outside a jar, whereas (c) works from the jar and `target/classes`, and it
keeps `pom.xml` as the single source of truth. Build verified green.

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
