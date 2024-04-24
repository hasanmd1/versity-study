package importer

import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import importer.imageImporter.RandomImageImporter
import org.scalatest.FunSuite


/**
 * We have hardly any case of failure if successfully called the method
 *
 */
class RandomImporterTest extends FunSuite{

  test("Test importing random image") {
    val importer = new RandomImageImporter()
    val importedImage: RGBImage = importer.importImage()

    assert(importedImage.isInstanceOf[RGBImage])
    assert(importedImage.grid.isInstanceOf[RGBGrid])
    assert(importedImage.getWidth > 0)
    assert(importedImage.getHeight > 0)

    for (row <- 0 until importedImage.getHeight) {
      for(column <- 0 until importedImage.getWidth){
        assert(importedImage.getPixelValues(row,column).blue >= 0 && importedImage.getPixelValues(row,column).blue <= 255)
        assert(importedImage.getPixelValues(row,column).green >= 0 && importedImage.getPixelValues(row,column).green <= 255)
        assert(importedImage.getPixelValues(row,column).red >= 0 && importedImage.getPixelValues(row,column).red <= 255)
      }
    }
  }

}
