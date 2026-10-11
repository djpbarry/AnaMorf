# AGENTS.md

Guidance for agents working in this repository.

## What this project is

AnaMorf is a [FIJI/ImageJ](https://fiji.sc) plug-in for analysing the morphology of
filamentous structures (e.g. fungal hyphae). Given a directory of images, it segments
each image, traces each object, and writes tabulated morphology data (projected area,
circularity, total length, endpoints, branchpoints, hyphal growth unit, mean branch
length, lacunarity, box-counting and Fourier fractal dimensions, curvature).

This repository is the standalone project. Users normally consume it through FIJI, and
the authoritative usage documentation lives in the
[wiki](https://github.com/djpbarry/AnaMorf/wiki), not here.

## Build

Maven only. The NetBeans Ant build (`build.xml` + `nbproject/`) is legacy and is
currently broken (see Gotchas).

```bash
mvn package                                  # compile + jar + copy deps into target/
mvn --settings mvn_settings.xml verify       # what CI runs
```

Notes:

- The parent is `org.scijava:pom-scijava:35.0.0`; it supplies plugin versions,
  the `net.imagej:ij` version, code style enforcement, and the license header check.
- `mvn_settings.xml` activates a `github` profile pointing at
  `https://maven.pkg.github.com/djpbarry/*`. Resolving dependencies there requires a
  GitHub personal access token passed as `-Dinternal.repo.password=<PAT>`. Without it,
  builds that need to reach GitHub Packages will fail; this is an external credential
  requirement, not a code problem.
- `maven-dependency-plugin` copies all runtime dependencies into `target/`, so
  `target/` is huge and full of third-party jars. Do not treat it as source and do not
  read it looking for project code.
- CI (`.github/workflows/maven.yml`) runs on JDK 11 with
  `-Dinternal.repo.password="$PAT" --settings mvn_settings.xml verify`.

## Testing

There is **no test suite**. `src/test/java/` exists but is empty, and there are no test
dependencies or test commands configured. Do not invent a `mvn test` workflow; if you
add tests, you are also choosing and configuring the framework.

Verification is therefore manual: compile (`mvn package`) and, if you want to exercise
behaviour, run the plug-in. `net.calm.anamorf.Main` is the `main-class`; its `main`
method opens the interactive (GUI) path and calls `System.exit(0)` at the end.

## Code layout

All production code lives under `src/main/java/net/calm/anamorf/`.

| File | Role |
| --- | --- |
| `Main.java` | Entry point. Historically a scratchpad for launching specific paths. |
| `Batch_Analyser.java` | The heart of the plug-in. Implements `ij.plugin.PlugIn`. Owns the whole pipeline. |
| `UserInterface.java` / `.form` | NetBeans-generated Swing parameter dialog. |
| `ExitDialog.java` / `.form` | NetBeans-generated confirmation dialog. |
| `AnaMorf_Macro_Extensions.java` | ImageJ `PlugIn` that registers macro extensions; its `toString()` doubles as the user-facing help text. |
| `AnaMorfMacroExecutor.java` | Implements `ij.macro.MacroExtension`; backs the `Ext.*` macro functions. |
| `HyphalAnalyser.java` | Skeleton analysis: total length, tips, branchpoints, hyphal growth unit. |
| `SkeletonPruner.java` | Iterative pruning of short skeleton branches, optionally returning branch pixel traces. |
| `ResultSummariser.java` | Mean/std-dev summary rows over ImageJ's ResultsTable. |
| `params/DefaultParams.java` | `Properties` subclass. Every parameter label and default value. |

`src/main/resources/plugins.config` declares the two menu entries:

```
Plugins>AnaMorf, "AnaMorf Macro Extensions", net.calm.anamorf.AnaMorf_Macro_Extensions
Plugins>AnaMorf, "AnaMorf Batch Processor", net.calm.anamorf.Batch_Analyser
```

`src/main/java/Revision/` is an empty leftovers directory; the real `Revision` class
comes from the `IAClassLibrary` dependency.

## Architecture and data flow

`Batch_Analyser.run(String)` is the spine:

1. `showGUI()` unless in macro mode, then `setOutputs()` converts the ten boolean
   output options into a bitmask.
2. The input directory is picked (GUI) or already supplied (macro mode).
3. `analyseFiles()` lists files matching the configured extension, then loops
   `analyseFile()` per image.
4. `analyseFile()` allocates the per-image working buffers (`maskImage`, `curveMap`,
   `colorSkelImage`, `refProc`) and calls `preProcessImage()` unless pre-processing is
   disabled, then `searchImage()`.
5. `searchImage()` raster-scans for foreground pixels not yet in `refProc`, calls
   `Wand.autoOutline()`, and hands each boundary to `analyseObject()`.
6. `analyseObject()` -> `analyseImage()` applies the morphological filters (area,
   circularity), crops to the object box, then runs the measurement block.
7. `saveResults()` writes `results.csv`; `PropertyWriter` writes the settings
   (`AnaMorf v<version>.xml`); optional curvature values go to `CurvatureValues.csv`.

Results always land in a sibling directory named after the plug-in title, produced by
`GenUtils.openResultsDirectory(...)`.

## Conventions and patterns

- **Everything is driven by `Properties`.** Parameters are read with
  `props.getProperty(DefaultParams.SOME_LABEL)` and parsed on every use
  (`Boolean.parseBoolean`, `Double.parseDouble`). Add a new parameter by adding a label
  + default to `DefaultParams`, then wiring it into `UserInterface` and
  `Batch_Analyser`. There is no central validation.
- **`props` is static.** `Batch_Analyser.props` is a `static Properties` initialised in
  a static block, with a public `getProps()` setter used by the macro layer. Everything
  shares one mutable parameter set per JVM. Be careful with anything that assumes
  instance-scoped configuration.
- **Outputs are a bitmask.** `ASetOutputs()` builds `outputData` by treating the
  `boolean[]` from `getOptions()` positionally as powers of two. The ordering of that
  array must stay in sync with the `public static final int` flags declared in
  `Batch_Analyser` (`AREAS`, `CIRC`, `HYPHAL_GROWTH_UNIT`, ...). Reordering either side
  silently corrupts output selection.
- **Pixel polarity is inverted from intuition here.** `FOREGROUND = 0` and
  `BACKGROUND = 255`. Several places set `Prefs.blackBackground = false` and warn about
  inverted LUTs. Check which convention a method expects before comparing pixel values.
- **Macro mode vs GUI mode** is the `macroMode` constructor flag. In macro mode the
  directory and properties come from `AnaMorfMacroExecutor.initialise()`, no dialogs are
  shown, and errors are logged rather than surfaced.
- **NetBeans-generated code is off limits for hand-editing.** `initComponents()` in
  `UserInterface` and `ExitDialog` is fenced with
  `//GEN-BEGIN:initComponents` / `//GEN-END:initComponents` and the event handlers carry
  `//GEN-FIRST:` / `//GEN-LAST:` markers. Editing inside those fences gets overwritten
  the next time the form is regenerated in the designer. Put logic in the hand-written
  methods (`okButtonActionPerformed`, `getOptions`, the getters) instead.
- **`IAClassLibrary` provides a lot of the primitives** — `GenUtils`, `GenVariables`,
  `PropertyWriter`, `DataWriter`, `Utilities`, `FractalEstimator`, `CurveAnalyser`,
  `FuzzyThresholder`, `BinaryMaker`, `Dijkstra`/`Graph`/`Node`. Before writing your own
  helper, check whether the library already has one; the imports at the top of
  `Batch_Analyser` are the best index of what is available.
- **Licensing is mixed.** `Batch_Analyser`, `UserInterface`, `HyphalAnalyser`,
  `ResultSummariser` and others carry GPLv3 headers, while `pom.xml` declares BSD-2 for
  the project. Match the header style of the file you are editing.
- Version strings are resolved at runtime via
  `Revision.getVersionFromPom("./pom.xml")`, i.e. **relative to the process working
  directory**. Running the jar from somewhere without a `pom.xml` nearby will not give
  you the version you expect.

## Dependency posture and modernisation

`IAClassLibrary` has been modernised in place (v2.0.23: Java 21, parent
`pom-scijava:45.1.0`, GPL-3.0, JUnit 5), and **AnaMorf has not followed**. The
pinned coordinate `com.github.djpbarry:IAClassLibrary:8e10706f5e` resolves to
version **1.0.32**, so this project is a full major version behind and will not
build on the toolchain IACL now requires.

`DEVELOPMENT_PLAN.md` holds the compatibility assessment and the phased upgrade
plan; the maintainer decisions that gate it are in its section 5 (resolved
2026-10-11). `REVISION_LOG.md` is the dated engineering record,
`RELEASE_NOTES.md` is the user-facing changelog, and `CITATION.cff` is the
citation metadata. Keep all four in sync with any repo change. Two things to note
before touching the dependency:

- Package root (`net.calm.iaclasslibrary`) and every API AnaMorf calls survived the
  IACL 2.x break, **except** that `Revision.getVersionFromPom` is deprecated in
  favour of `Revision.getVersion()`, and `OnlyExt`, `SkeletonProcessor`,
  `FractalEstimator` and `DataStatistics` are now `@Deprecated` (slated for removal).
  `OnlyExt` and `DataStatistics` are on the critical path.
- IACL 2.x changed `FuzzyThresholder.threshold()` (it now scales the auto-threshold
  by `binSize`/`histMin`), so pre-processing output may drift. Characterise
  `results.csv` before and after any dependency change.

## Public API compatibility (downstream consumer: TWOMBLI)

AnaMorf is consumed as a **library** by
[TWOMBLI](https://github.com/FrancisCrickInstitute/TWOMBLI) (a Fiji plugin that
analyses the ECM network around blood vessels). TWOMBLI pins AnaMorf at
`com.github.djpbarry:Anamorf:d610176007` and calls it **directly from Java**, not
via the ImageJ macro layer. Any change below breaks TWOMBLI — do not make it
without coordinating with TWOMBLI first.

**Frozen Java API** (imported by `TWOMBLIRunner.java`):
- `net.calm.anamorf.Batch_Analyser` — constructor
  `Batch_Analyser(boolean macroMode, File currentDirectory, Properties props)`,
  `void run(String)`, and `static Properties getProps()`.
- `net.calm.anamorf.params.DefaultParams` — no-arg constructor (it is a
  `Properties` subclass, loaded by TWOMBLI via `loadFromXML`) and every
  `public static final String` label.

**Frozen property keys.** TWOMBLI ships a bundled `AnamorfProperties.xml` keyed by
the *exact string values* of the `DefaultParams` labels, and loads it with
`props.loadFromXML(...)`. Renaming a label constant (or its string) silently
un-wires TWOMBLI's parameters. The label set is effectively an external contract.

**Frozen output contract.** TWOMBLI drives AnaMorf by calling `run(null)` once per
curvature window and then globbing the results directory that
`GenUtils.openResultsDirectory` produces:
- exactly **one** directory whose name ends in `Output`, containing `results.csv`;
- the subsequent runs land in directories matching `.*Output_.*` (the
  `_Output_(n)` suffix), each with its own `results.csv`.

So the `_Output`/`_Output_(n)` naming, the `results.csv` filename, and the
one-directory-per-run semantics are load-bearing. An ADAPT-style output restructure
(e.g. `tables/`) would break TWOMBLI — any such change needs a compatibility shim.

**Frozen JitPack coordinate.** `com.github.djpbarry:Anamorf` is derived from the
**GitHub repo name** `djpbarry/AnaMorf` (mixed case), not from the `pom.xml`
`groupId`/`artifactId` (`net.calm:anamorf`). Renaming or moving the repo breaks the
coordinate, and TWOMBLI also pins a specific commit (`d610176007`).

**Consequences for the plan:**
- Phase E1 ("de-static `props`") is **not safe as written**: TWOMBLI mutates
  `Batch_Analyser.getProps().setProperty(...)` between runs. Keep `getProps()`
  static (even if it delegates to a static holder) or change it in lockstep with
  TWOMBLI.
- The G2 package-root rename (`net.calm` → `io.github.djpbarry`) **must include
  TWOMBLI** as a lockstep party, or it breaks TWOMBLI's imports.
- `Main` / `main-class` removal is safe (TWOMBLI does not use `Main`); the macro
  layer (`AnaMorfMacroExecutor`) is also not used by TWOMBLI, but keep it for
  other consumers.

## Gotchas

- **`build.xml` is malformed.** It contains an `<exec>` block followed by a stray
  `</target>` with no matching opening tag. Any Ant/NetBeans build fails on parse.
  Use Maven. The stray block is also machine-specific: it shells out to a hardcoded
  `C:\Users\barryd\...\perl.exe` script to stamp a git version number.
- **The `target/` JAR is the quickest way to read the real API.** Because the
  build is broken here, `target/IAClassLibrary-8e10706f5e.jar` is the ground truth
  for what is actually on the classpath; `jar xf <jar> META-INF/maven/.../pom.properties`
  reveals the pinned version. Do not trust the coordinate's name.
- **`Main.main` is a scratchpad, not a CLI.** It runs `Batch_Analyser.run(null)` and
  then `System.exit(0)`. The remaining lines are commented-out experiments referencing
  the original author's OneDrive paths. Do not assume `Main` accepts arguments.
- **`ResultSummariser` is partially commented out.** It still computes the mean and
  standard deviation rows but the per-column lookups and the confidence-interval row are
  disabled; it now iterates every column and only treats HGU as "clean data".
- **`target/` is checked into the working tree** in this environment and contains
  hundreds of dependency jars. `.gitignore` does not exclude it.
- The `.gitignore` entries `nbproject/private/`, `/build/`, `/dist/` correspond to the
  legacy NetBeans layout and are unrelated to Maven output.
- Untracked scratch directories (`.junie/plans/`) may exist locally; they are empty.
