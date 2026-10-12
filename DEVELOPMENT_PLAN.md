# AnaMorf Modernisation & IAClassLibrary Compatibility Plan

Status: **Phase A complete; Phase B replanned.** `./mvnw verify` succeeds on
JDK 21 against IACL `v2.0.23` — the predicted compile break did not occur. Phase B
is re-scoped (below) to deprecation cleanup + numerical characterisation, and the
project version will bump to `4.0.0-SNAPSHOT`. The maintainer decisions that gate
execution are resolved (2026-10-11, 2026-10-12) and recorded in section 5.

Scope: bring AnaMorf back into compatibility with the current IAClassLibrary and
modernise the project on the same terms, and in the same style, as
IAClassLibrary's own `DEVELOPMENT_PLAN.md`.

## 1. Compatibility assessment (the headline finding)

**AnaMorf does not currently compile against IAClassLibrary 2.x, and the failure
is total rather than incremental.**

| | AnaMorf | IAClassLibrary |
|---|---|---|
| Version | `3.1.0` | `2.0.23` |
| Parent | `pom-scijava:35.0.0` | `pom-scijava:45.1.0` |
| Java target | 8 (`.idea` says `target="8"`) / 11 (CI) | 21 |
| Dependency coordinate | `com.github.djpbarry:IAClassLibrary:8e10706f5e` (JitPack, 12-char commit) | `net.calm:iaclasslibrary:2.0.23` |
| Actual pinned commit | `1.0.32` (read from the packaged `pom.properties`) | 2.0.23 |
| License | BSD-2 in `pom.xml`, GPLv3 in most source headers | GPL-3.0-or-later (reconciled) |

The only manifestly **source-compatible** facts are that the package root is still
`net.calm.iaclasslibrary` and every class/Method AnaMorf imports still exists in
2.0.23. Everything else about the build is incompatible.

### 1.1 What still exists and is source-compatible

Verified against `master` (2.0.23) sources. Signatures AnaMorf relies on, all
**unchanged**:

| Symbol | Used at | Status |
| --- | --- | --- |
| `UtilClasses.GenUtils.openResultsDirectory` | `Batch_Analyser.run` | unchanged |
| `UtilClasses.GenUtils.logError(Exception, String)` | throughout | unchanged |
| `UtilClasses.Utilities.getFolder` | `Batch_Analyser.run` | unchanged |
| `UtilClasses.Utilities.checkBounds` | `Batch_Analyser.analyseImage` | unchanged |
| `UtilClasses.GenVariables.ISO` | `Batch_Analyser.saveResults` | unchanged, now `StandardCharsets.ISO_8859_1` |
| `IO.PropertyWriter.{save,load}Properties` | `Batch_Analyser.run`, `AnaMorfMacroExecutor.initialise` | unchanged |
| `IO.DataWriter.saveValues(ArrayList<ArrayList<Double>>, ...)` | `Batch_Analyser.run` | unchanged |
| `Thresholding.FuzzyThresholder(ImageProcessor, Method, double)` / `.threshold()` | `Batch_Analyser.preProcessImage` | unchanged |
| `Binary.BinaryMaker.checkIfBinary(ImagePlus)` | `Batch_Analyser.analyseFile` | unchanged |
| `Curvature.CurveAnalyser.calcCurvature(int[][], int, boolean, ArrayList<ArrayList<Double>>)` | `Batch_Analyser.generateCurveMap` | unchanged |
| `Graph.Dijkstra.calculateShortestPathFromSource(Graph, Node)` | `HyphalAnalyser` | unchanged |
| `Graph.Graph`, `Graph.Node`, `getAdjacentNodes()` returning `Map<Node, short[][]>` | `HyphalAnalyser` | unchanged |
| `IAClasses.Pixel`, `IAClasses.SkeletonProcessor` | `HyphalAnalyser`, `SkeletonPruner` | exists, but see 1.2 |
| `IAClasses.DSPProcessor` | `Batch_Analyser` (fractal dimensions) | exists, but see 1.2 |

Also unchanged: `IAClasses.FractalEstimator` — still present but now `@Deprecated`.

### 1.2 Deprecations that will bite

IAClassLibrary 2.x adopted "deprecate, then remove": `IAClasses` classes that are
not load-bearing are marked `@Deprecated` and are slated for removal in a later
major version. AnaMorf uses five of them:

| Symbol | AnaMorf usage | IACL status | Risk |
| --- | --- | --- | --- |
| `IAClasses.OnlyExt` | `Batch_Analyser.analyseFiles` — the image filename filter | `@Deprecated` | **High** — it is on the critical path, and it is the *deprecated one of three* near-identical filters |
| `IAClasses.SkeletonProcessor` | `HyphalAnalyser`, `SkeletonPruner` — endpoint/branchpoint detection | `@Deprecated` | **High** — core skeleton analysis |
| `IAClasses.FractalEstimator` | `Batch_Analyser` — 2-D/3-D fractal estimates | `@Deprecated` | Medium |
| `IAClasses.DSPProcessor` | `Batch_Analyser` — Fourier fractal dimension | **Not** deprecated (load-bearing per IACL Decision 4) | Low |
| `IAClasses.Pixel` | `HyphalAnalyser` | **Not** deprecated (load-bearing) | Low |
| `IAClasses.DataStatistics` | `ResultSummariser.printStats` | `@Deprecated` | Medium — IACL B4 names this exact class as a downstream migration blocker |
| `UtilClasses.GenVariables` charsets | `Batch_Analyser.saveResults` | Not deprecated, but now just `StandardCharsets` | Low |

IACL's own decision record explicitly says the replacement for the deprecated
filename filter is `IO.File.FileExtensionFilter`, and that `IAClasses.DataStatistics`
should become `org.apache.commons.math3.stat.descriptive.DescriptiveStatistics`.

### 1.3 Value drift, not just API drift

Two changes alter results rather than breaking the build:

- **`Revision.getVersionFromPom` is `@Deprecated`.** `Batch_Analyser` builds its
  output directory name from it (`title = String.format("AnaMorf v%s", ...)`). The
  replacement is `Revision.getVersion()`, which reads `Implementation-Version`
  from the JAR manifest. Because `title` is used for the **output directory name**,
  switching changes on-disk folder names (`AnaMorf v3.1.0`), so it is a
  user-visible change and should be a deliberate, documented one.
- **IACL `FuzzyThresholder.threshold()` changed.** It now computes the threshold
  as `round(autoThreshold * binSize + histMin)` — i.e. it is **calibrated for
  images with a non-unit bin size** — and returns `fuzzyBinary` seeded from a
  `binary.threshold(t2)` pass. `Batch_Analyser.preProcessImage` passes
  `currentProcessor.duplicate()` (a `ShortProcessor` after the `TypeConverter`
  step). This path should be **characterised with regression images** before and
  after the dependency bump; results may not be bit-identical even though the
  call site is.
- **`HGU_HEAD` is still `"HGU (microns)"`** in IACL 2.x, so `ResultSummariser`'s
  `results.getColumnIndex(Batch_Analyser.HGU_HEAD)` lookup still resolves. Note
  that IACL 2.0.23 renamed some *trajectory* outputs to `snake_case`; AnaMorf's
  headings are unaffected, but treat heading strings as a coupled contract.

### 1.4 The build-level blocker

AnaMorf resolves IAClassLibrary through JitPack at a 12-character commit
(`8e10706f5e`). IAClassLibrary 2.x requires:

- **Java 21** (`scijava.jvm.version=21`; "Consumers must also target Java 21").
- Parent `pom-scijava:45.1.0`. Bumping this also drags in TrackMate 8.0.0 and
  ImageJ 2.12.0 transitively, which can move ImageJ behaviour underneath AnaMorf.
- The IACL Maven Central note: `pom-scijava:45.1.0` **drops the implicit Maven
  Central repository**, so a consumer on that parent must declare `central`
  explicitly (IACL's `pom.xml` does exactly this). AnaMorf's `pom.xml` does not
  declare `central` at all.
- Tagged releases now exist (`v2.0.23`), so the pinned commit SHA should become a
  tag.

**Consequence:** this is not a version-bump task. It is a coordinated Java 21
modernisation, and it should be sequenced so that API migration and toolchain
migration do not land in the same change.

### 1.5 Downstream consumer: TWOMBLI (public API freeze)

AnaMorf is consumed as a library by
[TWOMBLI](https://github.com/FrancisCrickInstitute/TWOMBLI), which pins it at
`com.github.djpbarry:Anamorf:d610176007` and calls it **directly from Java** (not
via the macro layer). This turns several "internal" details into an external
contract that the plan must respect. The full frozen surface is documented in
`AGENTS.md`; the headline items are:

- **Java API** — `net.calm.anamorf.Batch_Analyser` (constructor
  `(boolean, File, Properties)`, `run(String)`, static `getProps()`) and
  `net.calm.anamorf.params.DefaultParams` (no-arg constructor + every
  `public static final String` label).
- **Property keys** — TWOMBLI ships an `AnamorfProperties.xml` keyed by the exact
  label strings; renaming a label silently breaks its parameter wiring.
- **Output contract** — TWOMBLI globs for one `*_Output` dir + `results.csv`, then
  `*_Output_(n)` dirs (one per curvature-window run). `GenUtils.openResultsDirectory`
  naming, the `results.csv` filename, and the per-run directory semantics are
  load-bearing.
- **JitPack coordinate** — `com.github.djpbarry:Anamorf` is derived from the
  **repo name**, not the `pom.xml` `groupId`/`artifactId` (`net.calm:anamorf`).
  TWOMBLI also pins commit `d610176007`.

**Plan impact:** (1) Phase E1 "de-static `props`" must keep `getProps()` static or
be done in lockstep with TWOMBLI; (2) the G2 namespace rename must include TWOMBLI;
(3) no ADAPT-style output restructure without a TWOMBLI-compatible shim; (4) the
Decision-4 output-folder *rename* is safe (TWOMBLI matches on the `Output` suffix,
not the version prefix) and actually a fix — see 1.3's `Revision` note.

## 2. Inventory of what needs to change at each tier

### Tier 0 — everything that physically cannot compile today

| # | Item | Location | Fix |
| --- | --- | --- | --- |
| 0.1 | Java target 8/11 | `.idea/compiler.xml`, CI workflow, `pom.xml` | Move to 21 |
| 0.2 | Parent `pom-scijava:35.0.0` | `pom.xml:10` | Move to `45.1.0` |
| 0.3 | Missing implicit Maven Central | `pom.xml` `<repositories>` | Add explicit `central` (IACL's L10–L12 lesson) |
| 0.4 | IACL pinned at commit SHA | `pom.xml:132` | Pin the `v2.0.23` tag |
| 0.5 | Maven runs on the system JDK 8 here | environment | Add a Maven wrapper (`mvnw`), as IACL did in A1 |
| 0.6 | `mvn` not on `PATH` in this workspace | environment | Use `./mvnw` |

### Tier 1 — mechanical hygiene (the IACL Phase A equivalent)

| # | Item | Location |
| --- | --- | --- |
| 1.1 | Delete the broken NetBeans Ant build | `build.xml`, `nbproject/` |
| 1.2 | `.gitignore` covers only the legacy NetBeans paths | `.gitignore` |
| 1.3 | CI builds a single job on JDK 11 with a system `mvn` | `.github/workflows/maven.yml` |
| 1.4 | `ciManagement` claims `None` while CI exists | `pom.xml:105-107` |
| 1.5 | Malformed SCM URL (`github.com/github.com/...`) | `pom.xml:97` |
| 1.6 | BSD-2 declared, GPLv3 headers in source | `pom.xml:51-56`, `pom.xml:112` |
| 1.7 | `src/main/java/Revision/` is an empty leftovers directory | delete |
| 1.8 | `target/` and `.idea/` untracked-but-present | `.gitignore` |

### Tier 2 — API migration away from deprecated IACL symbols

| # | Item | Location | Replacement |
| --- | --- | --- | --- |
| 2.1 | `OnlyExt` | `Batch_Analyser.analyseFiles` | `IO.File.FileExtensionFilter` (or a local `FilenameFilter`) |
| 2.2 | `ResultSummariser` + `IAClasses.DataStatistics` | `ResultSummariser.java` | `DescriptiveStatistics` (already a direct dependency) |
| 2.3 | `Revision.getVersionFromPom` | `Batch_Analyser:96` | `Revision.getVersion()`; decide the output-directory naming story first |
| 2.4 | `SkeletonProcessor` | `HyphalAnalyser`, `SkeletonPruner` | No drop-in replacement; either vendor the logic or plan on it being removed in IACL 3.x |
| 2.5 | `FractalEstimator` | `Batch_Analyser` | Assess in place; it is `@Deprecated` with no named successor |
| 2.6 | `GenVariables.ISO` | `Batch_Analyser.saveResults` | `StandardCharsets.ISO_8859_1` |

### Tier 3 — dead code and scratchpads (the IACL D1 equivalent)

| # | Item | Location |
| --- | --- | --- |
| 3.1 | `Main.main` is a scratchpad: it runs `run(null)` then `System.exit(0)`, with commented-out experiments pointing at the original author's OneDrive paths | `Main.java:14-27` |
| 3.2 | `ResultSummariser` is half commented out — per-column lookups and the confidence-interval row are disabled, and it now iterates *every* column treating only HGU as "clean data" | `ResultSummariser.java:63-70, 84-91, 119` |
| 3.3 | `build.xml`'s hardcoded `C:\Users\barryd\...\perl.exe` version-stamping block | `build.xml` |
| 3.4 | `UserInterface.isCurvature()` is `static` while every sibling getter is an instance method; the GUI event handlers reference it statically | `UserInterface.java:903` |
| 3.5 | Setter boilerplate: one-line getters that exist only to copy a field | `UserInterface.java:784-947` |

### Tier 4 — structural issues AnaMorf shares with IACL

These are the same classes of problem IACL's Phase G targets, so the fixes and the
lessons transfer directly.

| # | Item | Location | Note |
| --- | --- | --- | --- |
| 4.1 | `Batch_Analyser.props` is a **static mutable** `Properties`, written from a static initialiser and from `AnaMorfMacroExecutor` | `Batch_Analyser.java:102-110, 870-872` | Directly analogous to IACL's `RegionGrower.terminal`/`intermediate`. One mutable parameter set per JVM. |
| 4.2 | `showGUI()` contains a `while (!valid)` loop whose body unconditionally returns | `Batch_Analyser.java:365-381` | The loop is dead; the method is a plain dialog call |
| 4.3 | `ASetOutputs()` (documented as `setOutputs()`) derives a bitmask by treating `getOptions()` **positionally** as powers of two | `Batch_Analyser.java:383-391`, `857-868` | Silent corruption if either array is reordered |
| 4.4 | `FOREGROUND = 0` / `BACKGROUND = 255` | `Batch_Analyser.java:90` | Counter-intuitive polarity; IACL's `FractalEstimator` uses the *same* convention but `SkeletonPruner` **overrides** it based on `isInvertedLut()` |
| 4.5 | `fill(...)` hardcodes the magic marker value `127` as a flood-fill sentinel | `Batch_Analyser.java:799-829` | Identical code exists in `BinaryMaker.fill` — an image with a genuine 127 pixel would confuse it |
| 4.6 | `analyseImage` is a ~250-line god method mixing measurement, morphology filtering, cropping, skeletonisation and plotting | `Batch_Analyser.java:509-767` | The D2 equivalent |
| 4.7 | `searchImage` walks a rectangular ROI but assigns `bounds.height`/`bounds.width` as loop limits while iterating with `offset = y * width` | `Batch_Analyser.java:434-453` | Correct only for a full-image ROI |
| 4.8 | Commented-out `ensureGreyValues` call and a `//grab extents of objects` style of leftover | `Batch_Analyser.java:423-425` | D1 sweep |
| 4.9 | NetBeans-generated Swing code inside `//GEN-BEGIN`/`//GEN-END` fences in two `.java` + two `.form` files | `UserInterface`, `ExitDialog` | Hand-edits inside the fences are overwritten on regeneration |

### Tier 5 — testing, docs, packaging

| # | Item |
| --- | --- |
| 5.1 | `src/test/java/` exists but is **empty**; no test dependencies or commands |
| 5.2 | README is two short paragraphs; no build/consume section |
| 5.3 | No `Revision`-equivalent for the plug-in version, no `AGENTS.md` prior to this effort |
| 5.4 | No Javadoc publishing workflow |
| 5.5 | No release/tagging convention (`Revision.getVersionFromPom` aside) |

## 3. Proposed phases

Sequenced on the IACL model: mechanical and low-risk first, structural changes
only once there are tests to prove behavioural equivalence, cosmetic last. Each
phase is independently shippable.

### Phase A — Build, toolchain, and CI (unblocks everything)

1. ✅ **A1** Delete `build.xml` and `nbproject/` (IACL Decision 6, which deleted
   exactly these from IACL). **Done 2026-10-12.**
2. ✅ **A2** Add a Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) pinned to a
   Maven 3.9.x, and use `./mvnw` everywhere. **Done 2026-10-12 (Maven 3.9.16,
   `only-script` distribution type).**
3. ✅ **A3** Rewrite `.gitignore`: keep the legacy NetBeans paths for compatibility
   with existing clones, add `/target/`, `.idea/`, `*.iml`, `.junie/`, OS files.
   **Done 2026-10-12.**
4. ✅ **A4** Bump the parent to `pom-scijava:45.1.0` and set
   `scijava.jvm.version=21`. **Also add an explicit `central` repository** —
   `pom-scijava:45.1.0` drops the implicit one. **Done 2026-10-12.**
5. ✅ **A5** Bump CI to JDK 21, use `./mvnw verify`, add the Maven cache, and split
   `build` and `test` once Phase C lands. **Done 2026-10-12.**
6. ✅ **A6** Pin the IACL dependency to a **tag** (`v2.0.23`) rather than the
   12-character commit `8e10706f5e`. Keep JitPack; IACL is published there.
   **Done 2026-10-12.**
7. ✅ **A7** Fix the `scm` URL typo and the `ciManagement` block while in `pom.xml`.
   **Done 2026-10-12.**

**Do A4 and A6 last within Phase A.** *(2026-10-12 note: after A4+A6 the build
compiled green — the predicted break did not materialise; see REVISION_LOG.)*

**Exit criteria:** `./mvnw verify` green on JDK 21 against IACL 2.0.23; CI green.
*(Met locally and on CI 2026-10-12.)*

### Phase B — IACL 2.x API compatibility (re-scoped 2026-10-12)

The predicted compile break did not occur (see §1.1 and REVISION_LOG): on Java 21
/ `pom-scijava:45.1.0` AnaMorf compiles cleanly against IACL 2.0.23. **B1 is
therefore moot**, and the remaining work is deprecation cleanup + numerical
characterisation.

Confirmed deprecated/redundant IACL symbols actually in use: `OnlyExt`
(`Batch_Analyser:233`), `DataStatistics` (`ResultSummariser:116`),
`GenVariables.ISO` (`Batch_Analyser:208`), `SkeletonProcessor` (`HyphalAnalyser`,
`SkeletonPruner`), `FractalEstimator` (`Batch_Analyser:701,704`).
`Revision.getVersionFromPom` is **not** called — AnaMorf uses a local
`Revision/Revision.java` (hardcoded `VERSION=2, revisionNumber=27` → a stale
`AnaMorf v2.027`); IACL 2.0.23 has no `Revision.Revision` class.

Ordered by risk-to-correctness:

1. **B6 — Characterise the dependency bump (do first).**
   `FuzzyThresholder.threshold()` now scales the auto-threshold by
   `binSize`/`histMin` (see 1.3). Diff `results.csv` on a fixed image set:
   "before" (IACL 1.0.32) vs "after" (2.0.23). **Open:** no regression images in
   the repo, and the "before" state needs the pre-A6 commit on the old toolchain
   (parent 35.0.0 + IACL `8e10706f5e`, Java 11). Possible shortcut:
   `preProcessImage` converts to a `ShortProcessor` with `binSize=1`, so the
   scaling may be a no-op — verify against the 2.0.23 source.
2. **B2 — `OnlyExt` → `IO.File.FileExtensionFilter` (2.1).** Mechanical but
   highest-risk (critical path). `FileExtensionFilter` takes `String[]` (not a
   single `String`) and uses `FilenameUtils.isExtension`, so it does **not** carry
   `OnlyExt`'s `TIF`/`TIFF` and `JPG`/`JPEG` special-casing. Verify against a dir
   containing `.tiff`/`.jpeg`.
3. **B4 — `ResultSummariser` off `DataStatistics` → `DescriptiveStatistics`
   (2.2).** Rewrite `printStats`/`removeZeros` to mean + std-dev only (Decision 7;
   confidence interval stays disabled), together with the Tier 3.2 dead-code
   cleanup.
4. **B5 — `GenVariables.ISO` → `StandardCharsets.ISO_8859_1` (2.6).** Trivial,
   zero risk.
5. **B3 — Versioning (re-scoped).** Replace the stale local `Revision` with an
   accurate source. **Decision 9 (2026-10-12): bump to `4.0.0-SNAPSHOT`.** Open
   mechanism: (a) fix the local constants, (b) read
   `Package.getImplementationVersion()`, or (c) Maven resource-filter the
   `pom.xml` version. The output-directory name changes as a result, but TWOMBLI
   matches the `Output` suffix (not the version prefix), so it is safe.
6. **B7 — Deprecated-symbol policy.** `SkeletonProcessor` and `FractalEstimator`
   have no drop-in replacement. **Decision 3 (Q1):** accept the risk — keep them on
   the critical path, and record a plan to find alternatives (vendor or migrate)
   if IACL removes them. `OnlyExt`/`DataStatistics` still migrate in B2/B4.

**Exit criteria:** compiles and runs against IACL 2.0.23 with no deprecated
symbols on the critical path or a recorded decision for each remaining one
(`SkeletonProcessor`, `FractalEstimator`), and results verified by B6.

### Phase C — Tests (the biggest maintainability win)

AnaMorf has *nothing*, so this is a larger lift than it was for IACL, which
already had 37 tests by the time it modernised.

1. **C1** Add JUnit 5 (`junit-jupiter-api`/`-engine`, parent-managed) and make
   `./mvnw test` meaningful.
2. **C2** Start with the pure logic that needs no ImageJ runtime:
   - `ResultSummariser.printStats`/`removeZeros` (once B4 lands)
   - `Batch_Analyser.setOutputs`/`getOptions` — pin the positional bitmask
     contract (4.3) with a test, which is the cheapest way to stop it rotting
   - `SkeletonPruner` branch tracing
   - `HyphalAnalyser` length/tip/branch counting on synthetic skeletons
3. **C3** Golden-file tests for IACL interop: `IO.DataWriter.saveValues` and
   `PropertyWriter` output schemas, so silent schema drift is caught.
4. **C4** A small regression-image set plus a harness that runs the pipeline
   headlessly and diffs `results.csv`. This is what makes B6 repeatable rather
   than a one-off.

**Exit criteria:** `./mvnw test` runs real assertions and covers the pipeline's
numerical output.

### Phase D — Code hygiene, mirroring IACL D1/D2

1. **D1** Delete the scratchpad: remove `Main.java` entirely and drop the
   `main-class` property (no standalone entry point). The ImageJ macro surface
   (`AnaMorfMacroExecutor` + `AnaMorf_Macro_Extensions`) is the scriptable entry
   point and is preserved/enhanced, not replaced. Remove the `ResultSummariser`
   dead code (3.2) and the `build.xml` Perl hook (3.3) — the latter falls out of
   A1.
2. **D2** Decompose `Batch_Analyser.analyseImage` (4.6) into
   single-responsibility helpers, extracting the pure math first so Phase C can
   cover it. Run this **before** Phase E so the threading work is not churning
   the same code twice — the same ordering IACL chose (G7).
3. **D3** Remove the dead `while (!valid)` loop in `showGUI()` (4.2).
4. **D4** Give `fill(...)`'s sentinel a named constant, or set the flood-fill
   colour to a value outside the image's range (4.5). Note the duplicated copy in
   `BinaryMaker.fill`.
5. **D5** Make `setOutputs` self-describing — replace positional indexing with an
   explicit label-to-flag map (4.3). The C2 test locks the current behaviour first.
6. **D6** Review `searchImage`'s bounds handling (4.7) and either fix it or
   document that only full-image ROIs are supported.

**Exit criteria:** no commented-out code, no dead control flow, no god methods.

### Phase E — Structural modernisation (the IACL G1/G2 equivalent)

1. **E1** De-static `props` **with a backward-compatible accessor.**
   `Batch_Analyser.props` is a `static Properties` shared per JVM. TWOMBLI mutates
   it via the static `Batch_Analyser.getProps().setProperty(...)` between runs, so
   the `getProps()` static method must survive (even if it delegates to a static
   holder). Any instance-scoping of `props` has to be coordinated with TWOMBLI and
   the macro layer, both of which write through `getProps()` **before** the
   `Batch_Analyser` is constructed. This is the direct analogue of IACL's
   `RegionGrower` work, but the consumer coupling makes it a breaking change if
   done naively.
2. **E2** If any per-image work is parallelised (currently it is not — images are
   processed in a serial loop), adopt IACL's `MultiThreadedProcess.runWorkers`
   idiom rather than hand-rolling threads. If it is not parallelised, do not add
   concurrency just for its own sake.
3. **E3** **Decision (Q4): rewrite the UI.** Drop the NetBeans `.form`/`GEN`-fenced
   Swing (`UserInterface`, `ExitDialog`) in favour of a hand-managed layout,
   following ADAPT's M5 step 3. Keep the same fields and tab structure so the
   parameter surface is unchanged.
4. **E4** Only now consider modern language features (`switch` expressions,
   pattern matching, `var`). IACL sequenced this last (G6) because doing it
   earlier churns code the structural phases then rewrite.

### Phase F — Documentation, packaging, and release

1. **F1** Rewrite `README.md` on the IACL template: what it is, build
   (`./mvnw verify`), how it is consumed (FIJI/ImageJ, and as a JitPack
   dependency), and a package map.
2. **F2** Keep `AGENTS.md` in sync as each phase lands — IACL treats this as a
   per-change obligation, and it is what makes the next migration cheap.
3. **F3** Maintain the three-file source of truth — `DEVELOPMENT_PLAN.md` (what
   is done/next), `REVISION_LOG.md` (the engineering narrative, decisions and
   lessons), and `RELEASE_NOTES.md` (the user-facing changelog) — updated in the
   same pass as any repo change, per the sibling convention.
4. **F4** Adopt Conventional Commits and a `vX.Y.Z` tag per release (IACL
   Decision 2). The next release is `v4.0.0` (Decision 9), cut after Phase B;
   phases that follow are patches/minors.
5. **F5** Optional: a Javadoc publishing workflow, once F1/F3 exist.

### Phase G — Upstream coordination

AnaMorf is a consumer, but it sits on the same author's shelf as IACL,
`TrackerLibrary`, `AdaptDataProcessing` and ADAPT. IACL's plan names a coordinated
Java 21 / TrackMate 8.x / `io.github.djpbarry` namespace migration across all of
them. Two things follow:

1. **G1 — Decision (Q5): join now.** AnaMorf adopts Java 21 + `pom-scijava:45.1.0`
   + TrackMate 8.0.0 in lockstep with the siblings, rather than trailing on the
   old IACL 1.0.32. This is folded into Phase A4/A5.
2. **G2 — namespace rename (still open, now includes TWOMBLI).** IACL Decision 7
   proposes renaming the package root `net.calm` → `io.github.djpbarry` across the
   whole suite. This was *not* part of the resolved Q5 answer. Because TWOMBLI
   imports `net.calm.anamorf.*` directly, this rename **must include TWOMBLI** as a
   lockstep party (and is a breaking change for its `com.github.djpbarry:Anamorf`
   coordinate). Confirm with the maintainer before Phase E whether AnaMorf
   participates, and if so do the rename across AnaMorf + TWOMBLI + the siblings
   together, not solo.

## 4. Sequencing rationale

Ordering follows IACL's G7 reasoning, adapted:

1. **Phase A** first because nothing else can be verified until the build is on a
   toolchain that can consume IACL 2.x.
2. **Phase B** second because the dependency migration (deprecation cleanup) is
   best done as small, mechanical, reviewable changes once the toolchain (A) is
   stable. (The originally-predicted compile break did not occur.)
3. **Phase C** before **Phase D/E** because D2 (god-method decomposition) and E1
   (de-static `props`) need behavioural tests to prove equivalence — IACL
   explicitly deferred its `RegionGrower` decomposition for exactly this reason.
4. **Phase B6 (characterisation) is not optional and is not part of C.** It must
   happen at the same time as B, while the "before" state is still runnable, or
   the evidence is lost.
5. **Phase E4 (language features) last** to avoid churning code that D/E rewrite.
6. **Phase A2 (wrapper) before A5 (CI)** so CI tests the same Maven everyone else
   uses.

## 5. Resolved decisions (2026-10-11, 2026-10-12)

Maintainer decisions that gate execution. Recorded once, authoritative; the
former open questions (Q1–Q8) are closed by these, plus a 2026-10-12 version
decision.

1. **License — GPL-3.0-or-later** *(Q6)*. Correct `pom.xml` from BSD-2 to
   GPL-3.0 (`<licenses>`, `license.licenseName=gpl_v3`,
   `license.copyrightOwners=The Francis Crick Institute`); replace the remaining
   NetBeans "change this header" stubs with the GPL header. Aligns with IACL,
   TrackerLibrary and ADAPT.
2. **Java target — 21, join now** *(Q5)*. Adopt Java 21 + `pom-scijava:45.1.0` +
   TrackMate 8.0.0 in lockstep with the siblings, not trail on IACL 1.0.32.
3. **Deprecated IACL symbols — accept the risk for now** *(Q1)*. Keep
   `SkeletonProcessor` and `FractalEstimator` on the critical path; do the
   mechanical migrations (`OnlyExt` → `FileExtensionFilter`, `DataStatistics` →
   `DescriptiveStatistics`) immediately, and record a plan to find alternatives
   if IACL does not un-deprecate before removal.
4. **Output directory naming — rename** *(Q2; corrected 2026-10-12)*. The original
   mechanism (`Revision.getVersionFromPom` → `Revision.getVersion()`) was wrong:
   AnaMorf uses a local hardcoded `Revision/Revision.java`, not IACL's `Revision`.
   The *intent* stands — make the version string accurate and accept the
   `AnaMorf vX.Y.Z` folder-name change (TWOMBLI matches the `Output` suffix, not
   the version prefix).
5. **UI — rewrite** *(Q4)*. Drop the NetBeans `.form`/`GEN`-fenced Swing for a
   hand-managed layout (ADAPT M5 step 3 pattern); keep the same fields/tabs.
6. **Entry point — no CLI; preserve macro scripting** *(Q7)*. Remove `Main.java`
   and the `main-class` property. The `Ext.*` ImageJ macro functions
   (`AnaMorfMacroExecutor` + `AnaMorf_Macro_Extensions`) are the scriptable
   surface and must be preserved, and enhanced where possible.
7. **`ResultSummariser` — keep as-is, deferred** *(Q3)*. The confidence-interval
   row and per-column lookups stay disabled; revisit after Phase B6 gives a
   known-good baseline. (Maintainer uncertain of intent on this old code.)
8. **Citation affiliation — The Francis Crick Institute** *(Q8)*, for
   `CITATION.cff`.
9. **Version — `4.0.0-SNAPSHOT`** *(2026-10-12)*. Given the significant changes
   ahead, bump the project from `3.1.0` to `4.0.0-SNAPSHOT` (a major version) and
   cut `v4.0.0` as the next release, rather than `3.2.0`.

Still open: the `net.calm` → `io.github.djpbarry` namespace rename (G2) — confirm
with the maintainer during Phase G; it is a breaking cross-suite change and must
not be done solo.

## 6. What NOT to do

Recorded so the same mistakes are not repeated:

- **Do not "upgrade the dependency" as a one-line version bump.** The coordinate,
  the parent, the Java target, and the actual pinned commit are all different
  things, and changing only the version string produces an unresolvable build.
- **Do not bump `pom-scijava` without adding `central`.** IACL lost a release to
  exactly this (its L10–L12).
- **Do not touch `build.xml`** — delete it. Its breakage is a stray `</target>`
  plus a machine-specific Perl hook; IACL reached the same conclusion (Decision 6).
- **Do not treat `target/` as source.** It contains hundreds of dependency JARs
  and is untracked-but-present in this working tree.
- **Do not un-disable `ResultSummariser`'s commented-out code** without a
  known-good baseline; it was disabled deliberately and the intent is uncertain.
- **Do not edit inside `//GEN-BEGIN`/`//GEN-END` fences** (moot once E3 rewrites
  the UI, but true until then).
- **Do not remove or regress the `Ext.*` macro functions** — they are the
  scriptable entry point and must be preserved.
- **Do not break TWOMBLI.** Do not change `Batch_Analyser`'s public constructor /
  `run(String)` / static `getProps()`, the `DefaultParams` label strings, the
  `results.csv` + `*_Output` directory contract, or the repo name behind the
  `com.github.djpbarry:Anamorf` coordinate without coordinating with
  [TWOMBLI](https://github.com/FrancisCrickInstitute/TWOMBLI).
- **Do not assume `Main` accepts arguments** (it is a scratchpad, removed in D1).
