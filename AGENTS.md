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

## Gotchas

- **`build.xml` is malformed.** It contains an `<exec>` block followed by a stray
  `</target>` with no matching opening tag. Any Ant/NetBeans build fails on parse.
  Use Maven. The stray block is also machine-specific: it shells out to a hardcoded
  `C:\Users\barryd\...\perl.exe` script to stamp a git version number.
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
