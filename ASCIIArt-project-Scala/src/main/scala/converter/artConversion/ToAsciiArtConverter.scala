package converter.artConversion

import converter.ArtConverter
import dataModel.asciiImage.{ASCIIImage, GrayScaleImage}
import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel
import dataModel.tableOptions.Table
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

case class ToAsciiArtConverter(table: Table[String]) extends ArtConverter[GrayScaleImage, ASCIIImage] {
  private val consoleArgumentPresenter = new ConsoleArgumentPresenter

  /**
   * convertsImage A to B
   *
   * @param image input
   * @return converted image
   */
//  override def convertArt(image: GrayScaleArt): ASCIIArt = {
//    var newGrid = Seq[Seq[ASCIIPixel]]()
//    for(i <- 0 until image.getHeight){
//      var row = Seq[ASCIIPixel]()
//      for(j <- 0 until image.getWidth){
//        row = row.appended(ASCIIPixel(convert(image.getPixelValues(i, j).grayScaleValue)))
//      }
//      newGrid = newGrid.appended(row)
//    }
//    ASCIIArt(ASCIIGrid(newGrid))
//
//  }

  override def convertArt(image: GrayScaleImage): ASCIIImage = {
    val newGrid = (0 until image.getHeight).map { i =>
      (0 until image.getWidth).map { j =>
        ASCIIPixel(convert(image.getPixelValues(i, j).grayScaleValue))
      }
    }

    ASCIIImage(ASCIIGrid(newGrid))
  }
  private def convert(value: Int): Char = {
    var symbol: Char = 'o'
    if(value < 0 || value > 255){
      consoleArgumentPresenter.throwingError("GrayScale value must be between 0 <= value <= 255")
    }
    else if(table.tableName == "non-linear"){
      //For non linear table
      value match {
        //non linear can be changed by changing values
        case x if x > 220 => symbol = table.tableContainingString(0)
        case x if x > 180 => symbol = table.tableContainingString(1)
        case x if x > 140 => symbol = table.tableContainingString(2)
        case x if x > 100 => symbol = table.tableContainingString(3)
        case x if x > 60 => symbol = table.tableContainingString(4)
        case _ => symbol = table.tableContainingString(5)
      }
    }
    else {
      //For linear table
      val intervalSize: Double = 256.toDouble / table.tableContainingString.length
      val index: Int = Math.floorMod(table.tableContainingString(table.tableContainingString.length - (value / intervalSize).toInt - 1) , table.tableContainingString.length)

      if (index >= 0 && index < table.tableContainingString.length) {
        symbol = table.tableContainingString(index)
      } else {
        //print(value+"-"+intervalSize+"-"+index+"-")
        consoleArgumentPresenter.throwingError("Invalid table index\n")
      }
    }
    symbol
  }
}
