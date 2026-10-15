[![Build](https://github.com/djpbarry/AnaMorf/actions/workflows/maven.yml/badge.svg)](https://github.com/djpbarry/AnaMorf/actions/workflows/maven.yml)
[![Documentation Status](https://readthedocs.org/projects/anamorf/badge/?version=development)](https://anamorf.readthedocs.io/en/development/)
![Commit activity](https://img.shields.io/github/commit-activity/y/djpbarry/AnaMorf?style=plastic)
![License](https://img.shields.io/github/license/djpbarry/AnaMorf?color=green&style=plastic)

# AnaMorf

AnaMorf is a [FIJI/ImageJ](https://fiji.sc) plug-in for analysing the morphology of
filamentous structures (e.g. fungal hyphae). Given a directory of images, it
segments each image, traces each object, and writes tabulated morphology data
(projected area, circularity, total length, endpoints, branchpoints, hyphal growth
unit, lacunarity, box-counting and Fourier fractal dimensions, curvature).

For installation and usage, see the
[documentation](https://anamorf.readthedocs.io/en/development/).

## Building

AnaMorf builds with Maven on Java 21:

    ./mvnw verify

GitHub Packages dependencies require a personal access token:

    ./mvnw -Dinternal.repo.password=<PAT> --settings mvn_settings.xml verify

## Consuming

- **As a Fiji plugin** — install via the CALM update site, or copy `anamorf-*.jar`
  into Fiji's `plugins/` and `IAClassLibrary-*.jar` into `jars/`.
- **As a JitPack dependency** — `com.github.djpbarry:Anamorf:<tag-or-commit>`.

## Package map

| Class | Role |
| --- | --- |
| `net.calm.anamorf.Batch_Analyser` | The plug-in entry point; owns the whole pipeline. |
| `net.calm.anamorf.UserInterface` | Hand-managed Swing parameter dialog. |
| `net.calm.anamorf.ExitDialog` | Confirmation dialog. |
| `net.calm.anamorf.AnaMorf_Macro_Extensions` / `AnaMorfMacroExecutor` | ImageJ macro interface (`Ext.*`). |
| `net.calm.anamorf.HyphalAnalyser` | Skeleton analysis (length, tips, branch points, HGU). |
| `net.calm.anamorf.SkeletonPruner` | Pruning of short skeleton branches. |
| `net.calm.anamorf.ResultSummariser` | Summary rows over the results table. |
| `net.calm.anamorf.params.DefaultParams` | Every parameter label and default. |
