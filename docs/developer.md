# Developer guide

## Building

AnaMorf builds with Maven on Java 21:

    ./mvnw verify

GitHub Packages dependencies require a personal access token:

    ./mvnw -Dinternal.repo.password=<PAT> --settings mvn_settings.xml verify

## Testing

Unit tests run with:

    ./mvnw test

A live Fiji pipeline is provided under `bin/` (`install-to-fiji.cmd`,
`run-anamorf.cmd`, `verify-baseline.cmd`).

## Package map

| Class | Role |
| --- | --- |
| `Batch_Analyser` | The plug-in entry point; owns the whole pipeline. |
| `UserInterface` | Hand-managed Swing parameter dialog. |
| `ExitDialog` | Confirmation dialog. |
| `AnaMorf_Macro_Extensions` / `AnaMorfMacroExecutor` | ImageJ macro interface (`Ext.*`). |
| `HyphalAnalyser` | Skeleton analysis (length, tips, branch points, HGU). |
| `SkeletonPruner` | Pruning of short skeleton branches. |
| `ResultSummariser` | Summary rows over the results table. |
| `params.DefaultParams` | Every parameter label and default. |

## Macro interface

AnaMorf registers these macro functions:

- `Ext.initialiseAnaMorf`
- `Ext.setAnaMorfFileType`
- `Ext.setAnaMorfCurvatureWindow`
- `Ext.setAnaMorfMinBranchLength`
- `Ext.resetParameters`
- `Ext.runAnaMorf`

## Library use (TWOMBLI)

AnaMorf is also consumed as a JitPack dependency
(`com.github.djpbarry:Anamorf`). The public Java API is
`Batch_Analyser(boolean, File, Properties)`, `run(String)`, `static getProps()`,
and the `DefaultParams` label constants.
