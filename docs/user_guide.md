# User guide

## Interface

**Plugins > AnaMorf > AnaMorf Batch Processor** opens the parameter dialog, which
has two tabs: **Basic** and **Advanced**. After clicking **OK**, choose the
directory containing the images to analyse. Each image is analysed in turn and the
results are tabulated.

## Basic settings

### Analysis parameters

- **Image Format** — file extension of the images to analyse (BMP, GIF, JPG, PNG,
  TIF).
- **Image Resolution** — side length of one pixel, in microns/pixel.
- **Minimum Branch Length** — minimum line length (microns) for a branch to be
  treated as genuine rather than a skeletonisation artefact.
- **Maximum Circularity** — objects with a circularity above this threshold are
  excluded.
- **Minimum Area** — minimum object area (microns²) required to be analysed.
- **Curvature Window** — window size (pixels) used for curvature estimation.

### Output data

Tick the measurements to include in the results:

- Projected Area
- Circularity
- Total Length
- Mean Branch Length
- Number of Branchpoints
- Number of Endpoints
- Fourier Fractal Dimension
- Box-Counting Fractal Dimension
- Lacunarity
- Curvature

Definitions and formulas are given in the *Concepts* page.

## Advanced settings

- **Pre-Process Images** — enable or disable pre-processing (background
  correction, filtering, thresholding). When off, input images must already be
  8-bit binary.
- **Light / Dark Background** — whether objects are dark on a light background or
  light on a dark background.
- **Noise Reduction Filter Radius** — radius (microns) of the noise-reduction
  filter.
- **Remove Background** — apply ImageJ's "Rolling Ball" background subtraction.
- **Background Filter Radius** — estimated size (microns²) of the largest object;
  used to set the rolling-ball radius.
- **Auto / Manual Threshold** — segment automatically (with the chosen
  **Threshold Method**) or with a manual **Threshold Level**.
- **Create Mask Images** — save a binary mask and a skeletonised image (with
  branch points and end points highlighted) for each object.
- **Exclude Edge Objects** — ignore objects touching the image edge.
- **Separate Touching Objects** — apply watershed segmentation to split touching
  objects.
- **Treat Whole Image as One Object** — analyse the whole field of view as a
  single object.
- **Output Curvature Values** — write per-pixel curvature values to a separate
  `CurvatureValues.csv`.

## Results

Results are written to a directory next to the input images named
`AnaMorf v<version>_Output` (subsequent runs append `_(n)`). It contains:

- `results.csv` — one row per analysed object.
- `properties.xml` — the settings used for the run.
- mask and skeleton images (if **Create Mask Images** is selected).
- `CurvatureValues.csv` (if **Output Curvature Values** is selected).

Colour (RGB) images are not supported; convert them to 8-bit greyscale first
(`Image > Type > 8-bit`).
