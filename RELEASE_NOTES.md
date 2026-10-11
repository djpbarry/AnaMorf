# AnaMorf Release Notes

User-facing changelog: what changed and which changes affect behaviour. For the
internal, dated engineering record (decisions, mistakes, lessons) see
[`REVISION_LOG.md`](REVISION_LOG.md).

---

## Unreleased (next public release)

### Overview

AnaMorf is being modernised: the codebase will target **Java 21**, the
`IAClassLibrary` dependency is being updated to its current `2.x` line, and the
project is being brought onto a maintainable, testable footing. The plugin's
analysis pipeline and ImageJ macro interface are unchanged in behaviour.

### Requirements / upgrade notes

- **Java 21** — Fiji must run on a Java 21 runtime.
- **TrackMate 8** is resolved transitively via the updated `IAClassLibrary`.
- **`IAClassLibrary` is upgraded** from the legacy `1.0.32` to the modernised
  `2.x` line. This is the substance of the compatibility work; the package name
  (`net.calm.iaclasslibrary`) is unchanged, but the classes `OnlyExt`,
  `SkeletonProcessor`, `FractalEstimator` and `DataStatistics` are now
  deprecated upstream and are being migrated off the critical path over time.

### Behaviour-changing / breaking changes

- **Output directory name changes.** The results folder was previously named
  `AnaMorf v<version>` where the version came from `Revision.getVersionFromPom`
  (which read a nearby `pom.xml`). It now uses the version embedded in the JAR
  manifest (`Revision.getVersion()`), so the folder name may differ slightly and
  is no longer dependent on the process working directory. Scripts keyed to the
  exact folder name should be updated.

### Improvements

- **Licence clarified to GPL-3.0-or-later**, matching the source headers and the
  rest of the tool suite.
- **ImageJ macro scripting is retained and remains the scriptable interface**
  (`Ext.initialiseAnaMorf`, `Ext.setAnaMorfFileType`,
  `Ext.setAnaMorfCurvatureWindow`, `Ext.setAnaMorfMinBranchLength`,
  `Ext.resetParameters`, `Ext.runAnaMorf`).
- **Downstream compatibility preserved.** The Java API used by TWOMBLI
  (`Batch_Analyser` and `DefaultParams`) and the `results.csv` / `*_Output`
  directory layout are kept unchanged, so TWOMBLI continues to work without
  modification.
- **Build is Maven-only** (the legacy, broken NetBeans/Ant build is removed) and
  will use a pinned Maven wrapper.

### Notes for upgraders

- The results `*.csv` / `properties.xml` / `CurvatureValues.csv` file contents
  are not intentionally changed. However, the `IAClassLibrary` upgrade alters
  the underlying thresholding implementation, so **review your results after the
  upgrade**; small numerical differences in derived metrics are possible and are
  expected to be within the normal run-to-run range.
