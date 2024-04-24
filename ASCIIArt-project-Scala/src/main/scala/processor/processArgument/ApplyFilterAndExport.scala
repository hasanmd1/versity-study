package processor.processArgument

import converter.artConversion.{ToAsciiArtConverter, ToGrayScaleConverter}
import dataModel.asciiImage.{GrayScaleImage, RGBImage}
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import dataModel.tableOptions.Table
import exporter.art.{ExportToConsole, ExportToFile}
import filter.artFilter._
import processor.ArgumentProcessor
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class ApplyFilterAndExport extends ArgumentProcessor[Map[String, Any], Array[String], RGBImage, Table[String]] {


  private var processedImage: GrayScaleImage = GrayScaleImage(GrayScaleGrid(Seq.empty[Seq[GrayScalePixel]]))
  /**
   * abstract defined method to handle rest of arguments
   * we will only process the args mentioned in the cases in method
   *
   * @param mappedValues   name of input
   * @param commandOptions name of input
   * @param rawImage       name of input
   */
  override def processHandler(mappedValues: Map[String, Any], commandOptions: Array[String], rawImage: RGBImage, table: Table[String]): Unit = {
    val toGrayScale = new ToGrayScaleConverter
    val toAsciiArtConverter = ToAsciiArtConverter(table)

    processedImage = toGrayScale.convertArt(rawImage)

    for((option, index) <- commandOptions.zipWithIndex){
      option match {
        case "--output-console" =>
          handleOutputConsole(mappedValues, toAsciiArtConverter)
        case "--output-file" =>
          new ExportToFile(mappedValues("--output-file").toString).exportArt(toAsciiArtConverter.convertArt(processedImage))
        case "--rotate" =>
          processedImage =  new RotateFilter(mappedValues.get("--rotate").map(_.toString.toDouble).getOrElse(0.0)).filters(processedImage)
        case "--scale" =>
          processedImage = new ScaleFilter(mappedValues.get("--scale").map(_.toString.toDouble).getOrElse(1.0)).filters(processedImage)
        case "--invert" =>
          handleInvert(mappedValues)
        case "--brightness" =>
          processedImage = new BrightnessFilter(mappedValues.get("--brightness").map(_.toString.toInt).getOrElse(0)).filters(processedImage)
        case "--flip" =>
          handleFlip(commandOptions, index)
        case _ =>
      }
    }
  }
  private def handleOutputConsole(mappedValues: Map[String, Any], toAsciiArtConverter: ToAsciiArtConverter): Unit = {
    if (mappedValues("--output-console").equals(true)) {
      new ExportToConsole().exportArt(toAsciiArtConverter.convertArt(processedImage))
    }
    else {
      new ConsoleArgumentPresenter().throwingError("Error in --output-console")
    }
  }
  private def handleInvert(mappedValues: Map[String, Any]): Unit = {
    if (mappedValues.getOrElse("--invert", false).asInstanceOf[Boolean]) {
      processedImage = new InvertFilter().filters(processedImage)
    } else {
      new ConsoleArgumentPresenter().throwingError("Error in --invert")
    }
  }
  private def handleFlip(commandOptions: Array[String], index: Int): Unit = {
    if(commandOptions(index + 1).equals("x")){
      //flip x
      processedImage = new FlipXFilter().filters(processedImage)
    }
    else if(commandOptions(index + 1).equals("y")){
      //flip y
      processedImage = new FlipYFilter().filters(processedImage)
    }
    else {
      new ConsoleArgumentPresenter().throwingError("Error in --flip")
    }
  }
}
