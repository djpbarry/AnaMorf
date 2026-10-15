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

The next release is a **major version bump to 4.0.0** (from 3.1.0), reflecting
the dependency and toolchain break.

### Requirements / upgrade notes

- **Java 21** — Fiji must run on a Java 21 runtime.
- **TrackMate 8** is resolved transitively via the updated parent (`pom-scijava:45.1.0`).
- **`IAClassLibrary` is upgraded** from the legacy `1.0.32` to the modernised
  `2.x` line (pinned to `v2.0.23`). This is the substance of the compatibility work; the package name
  (`net.calm.iaclasslibrary`) is unchanged, but the classes `OnlyExt`,
  `SkeletonProcessor`, `FractalEstimator` and `DataStatistics` are now
  deprecated upstream and are being migrated off the critical path over time.

### Behaviour-changing / breaking changes

- **Output directory name changes.** The results folder was previously named
  `AnaMorf v<version>` where the version came from a hardcoded local `Revision`
  class (stale "v2.027"). It now uses the project version injected via Maven
  resource filtering, so the folder is `AnaMorf v4.0.0-SNAPSHOT_Output` during
  development (`AnaMorf v4.0.0_Output` on release) and no longer depends on a
  hardcoded value. Scripts keyed to the exact folder name should be updated.

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
- **Build is Maven-only.** The legacy, broken NetBeans/Ant build (`build.xml` and
  `nbproject/`) has been removed from the tree, and a Maven wrapper (pinned to
  Maven 3.9.16) is now included for reproducible builds.
- **CI now builds on JDK 21** using the Maven wrapper, with dependency caching.
- **Version lookup is collision-proof.** The version resource is now stored under
  the plugin's own package path (`net/calm/anamorf/version.properties`) instead of
  a root-level `project.properties`, so a co-installed ADAPT plugin can no longer
  shadow AnaMorf's version and mislabel the output folder.
- **The parameter dialog has been rewritten** as a hand-managed Swing layout (the
  NetBeans `.form` files are gone), with the same two tabs and parameter surface.
  This also fixes three long-standing dialog bugs: the "Mean Branch Length"
  checkbox now loads its saved state correctly; the "Threshold Method" selector
  now actually applies the chosen method instead of always using `Default`; and a
  non-numeric manual threshold value is now caught at the dialog instead of
  failing later during analysis.

### Notes for upgraders

- The results `*.csv` / `properties.xml` / `CurvatureValues.csv` file contents
  are not intentionally changed. Characterisation on a regression image set
  found **byte-identical results** after the `IAClassLibrary` upgrade, so no
  numerical drift is expected for typical 8-bit inputs. Review your results after
  the upgrade regardless; edge cases (e.g. 16-bit or unusual histograms) were
  not exercised.
