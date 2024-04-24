package converter.artConvert

import converter.artConversion.ToAsciiArtConverter
import dataModel.asciiImage.{ASCIIImage, GrayScaleImage}
import dataModel.gridImage.{ASCIIGrid, GrayScaleGrid}
import dataModel.gridImageTest.GrayScaleGridTest
import dataModel.pixelImage.{ASCIIPixel, GrayScalePixel}
import dataModel.tableOptions.Table
import dataModel.tableOptions.possibleOptions.{BourkeSmallTable, NonLinearTable}
import org.scalatest.FunSuite

class ToASCIIArtConverterTest extends FunSuite{

  private val nonLinearTable: Table[String] = new NonLinearTable
  private val linearTable: Table[String] = new BourkeSmallTable


  test("converting GrayScaleArt successfully for linearTable") {
    val converter = ToAsciiArtConverter(linearTable)
    val refASCIIArt: ASCIIImage = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('='), ASCIIPixel('#')),
      Seq(ASCIIPixel('+'), ASCIIPixel(':')),
      Seq(ASCIIPixel('-'), ASCIIPixel('.')),
      Seq(ASCIIPixel('+'), ASCIIPixel('%')),
      Seq(ASCIIPixel('*'), ASCIIPixel(':')),
    )))
    val image: GrayScaleImage = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(0), GrayScalePixel(27)),
      Seq(GrayScalePixel(73), GrayScalePixel(89)),
      Seq(GrayScalePixel(116), GrayScalePixel(139)),
      Seq(GrayScalePixel(179), GrayScalePixel(193)),
      Seq(GrayScalePixel(219), GrayScalePixel(254)),
    )))
    // .:-=+*#%@


    val convertedImage = converter.convertArt(image)
    for (i <- 0 until convertedImage.getHeight) {
      for (j <- 0 until convertedImage.getWidth) {
        assert(convertedImage.getPixelValues(i, j).character.equals(refASCIIArt.getPixelValues(i, j).character))
      }

    }
  }

  test("converting GrayScaleArt successfully for nonLinearTable") {
    val converter = ToAsciiArtConverter(nonLinearTable)
    val refASCIIArt: ASCIIImage = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('@'),ASCIIPixel('@')),
      Seq(ASCIIPixel('#'),ASCIIPixel('*')),
      Seq(ASCIIPixel('+'),ASCIIPixel('+')),
      Seq(ASCIIPixel('='),ASCIIPixel('=')),
      Seq(ASCIIPixel('.'),ASCIIPixel('.')),
    )))
    val image: GrayScaleImage = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(0), GrayScalePixel(27)),
      Seq(GrayScalePixel(73), GrayScalePixel(119)),
      Seq(GrayScalePixel(166), GrayScalePixel(159)),
      Seq(GrayScalePixel(189), GrayScalePixel(183)),
      Seq(GrayScalePixel(233), GrayScalePixel(254)),
    )))
    //.=+*#@

    val convertedImage = converter.convertArt(image)
    for(i <- 0 until convertedImage.getHeight){
      for(j <- 0 until convertedImage.getWidth){
        assert(convertedImage.getPixelValues(i,j).character.equals(refASCIIArt.getPixelValues(i,j).character))
      }

    }
  }

  test("converting GrayScaleArt with invalid pixel throws an exception") {
    val converter = ToAsciiArtConverter(linearTable)

    val image: GrayScaleImage = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(0)),
      Seq(GrayScalePixel(70)),
      Seq(GrayScalePixel(200)),
      Seq(GrayScalePixel(300)), // This value is out of range
    )))

    assertThrows[IllegalArgumentException] {
      converter.convertArt(image)
    }
  }
}
