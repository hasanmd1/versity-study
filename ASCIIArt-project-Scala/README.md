# ASCII Art

[![pipeline status](https://gitlab.fit.cvut.cz/BI-OOP/B201/asciiart/badges/master/pipeline.svg)](https://gitlab.fit.cvut.cz/BI-OOP/B201/asciiart)

The idea of this project is to load images, translate them into ASCII ART images, optionally apply filters, and save them. (https://courses.fit.cvut.cz/BI-OOP/projects/ASCII-art.html)

## How to do it

1. **Make your repository private**
2. **Read [the instructions](https://courses.fit.cvut.cz/BI-OOP/projects/ASCII-art.html)**

## Developer's Guide

1. used IDE - Intellij 2023
2. Scala 2.13
3. Without using any other library other than image.io
4. Test cases are written only using FunSuite(This one was available in mainTest.scala)
5. Speed can be optimized using vector/removing multiple layer of error checking

## The sources used for motivation
1. Motivation for dataModel comes from https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/image/BufferedImage.html. We also consider our image as bufferedImage(colorModel is not necessary for our design because we don't want to make another image). In raster image data, the data is stored as a image which can be converted to grid. Also the grid consists of some number of pixels.
2. Motivation of controller comes from MVC design pattern https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller
3. Motivation for importer and exporter comes from lectures and tutorials
4. Motivation for ui design comes from MVC controller, we dont need a view, so ui is used to interact(printing) with shell
5. Motivation for processor & filter comes from bufferedImage documentation https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/image/BufferedImageFilter.html, where its discussed how filtering works with buffered Image. We just modified those according to our need


## How its developed

1. Use of trait, Inheritance, abstraction methods for classes - can be easily extended
2. usage of polymorphism
3. OOP approach and division between module - for easier understanding
4. encapsulation for methods
5. Error checking and showing error messages

## Using sbt-shell guide

We have several options(be mindful our calculation does not fix gap between pixels/character so, to view the image correctly in console apply 0.08 or less scale value)

1. --image [imagePath]      -       used to import a manual image from  relative/ actual image path (only .jpg, .png, .gif are allowed..can be extended for other formats)
2. --image-random    -              used to generate some random image with random pixels
3. --output-file [outputPath]    -  used to write to output File(only .txt is allowed..can be extended for other formats)
4. --output-console    -            used to write to console
5. --brightness [Int]   -           used to apply filter of brightness value
6. --scale [Double]   -             used to apply filter of scale value
7. --flip [X or Y]   -              used to apply filter of flip value
8. --invert      -                  used to apply filter of invert
9. --rotate [Double]   -            used to apply filter of rotate value only values(both + & -) divisible by 90 is allowed currently(can be extended by adding else if case and related method)
