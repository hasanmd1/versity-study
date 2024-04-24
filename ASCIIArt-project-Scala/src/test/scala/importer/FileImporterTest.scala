package importer

import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import importer.imageImporter.InputImageImporter
import org.scalatest.FunSuite

import java.io.File

class FileImporterTest extends FunSuite{

  test("Test importing valid image file - .jpg") {
    val imagePath = "src/test/assets/images/image02.jpg" // Path to a valid image file
    val importer = new InputImageImporter(imagePath)
    val importedImage: RGBImage = importer.importImage()

    assert(importedImage.isInstanceOf[RGBImage])
    assert(importedImage.grid.isInstanceOf[RGBGrid])
    assert(importedImage.getWidth > 0)
    assert(importedImage.getHeight > 0)
  }

  test("Test importing valid image file - .png") {
    val imagePath = "src/test/assets/images/image01.png" //.png
    val importer = new InputImageImporter(imagePath)
    val importedImage: RGBImage = importer.importImage()

    assert(importedImage.isInstanceOf[RGBImage])
    assert(importedImage.grid.isInstanceOf[RGBGrid])
    assert(importedImage.getWidth > 0)
    assert(importedImage.getHeight > 0)
  }

  test("Test importing invalid image file - non-existent file") {
    val imagePath = "src/test/assets/images/non_existent_image.jpg" //non-existent image file
    val importer = new InputImageImporter(imagePath)

    assertThrows[Exception] {
      importer.importImage()
    }
  }

  test("Test importing invalid image file - unsupported extension") {
    val imagePath = "src/test/assets/images/unsupported_image.bmp" //unsupported extension
    val importer = new InputImageImporter(imagePath)

    assertThrows[Exception] {
      importer.importImage()
    }
  }

  test("Test importing image with zero width") {
    val emptyImageFile = new File("src/test/assets/images/empty_image.jpg") // Zero width image file
    val importer = new InputImageImporter(emptyImageFile.getPath)

    assertThrows[IllegalArgumentException] {
      importer.importImage()
    }
  }

  test("Test importing image with zero height") {
    val emptyImageFile = new File("src/test/assets/images/empty_image.jpg") // Zero height image file
    val importer = new InputImageImporter(emptyImageFile.getPath)

    assertThrows[IllegalArgumentException] {
      importer.importImage()
    }
  }
}
