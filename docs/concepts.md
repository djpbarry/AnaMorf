# Concepts

Definitions of the output descriptors. Where the results column name differs from
the checkbox label, the column name is shown in brackets.

## Projected Area (`Area (microns^2)`)

The projected area of an object, in square microns.

## Circularity

`C = 4 * pi * Ap / P^2`, where `Ap` is the projected area and `P` the perimeter
length. A circle has circularity 1.

## Total Length (`Total Length (microns)`)

Total length of a structure, estimated from its binary skeleton, in microns.

## Endpoints

Number of tips on a structure, estimated as the number of end points of the
skeleton.

## Branchpoints

Number of branch points, estimated as the number of crossing points of the
skeleton.

## Hyphal Growth Unit (`HGU (microns)`, checkbox "Mean Branch Length")

`HGU = L_h / N`, where `L_h` is the total length and `N` the number of tips.

## Box-Counting Fractal Dimension

A measure of the self-similarity of an object, or the degree to which it fills
space.

## Fourier Fractal Dimension

A fractal dimension estimated from the spectral periodicity of the object
boundary; it correlates with branching frequency.

## Lacunarity

`L = |sigma^2 / mean^2 - 1|`, where `mean` is the average pixel luminance within
an object boundary (internal holes included) and `sigma^2` the variance of those
luminances. It estimates how a structure fills space.

## Curvature

The local curvature of the longest path through each structure, evaluated over the
**Curvature Window**.
