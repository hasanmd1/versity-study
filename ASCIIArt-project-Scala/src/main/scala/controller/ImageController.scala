package controller

import converter.argumentConversion.{ArgumentToImageConverter, ArgumentToTableConverter}
import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import dataModel.tableOptions.Table
import processor.processArgument.ApplyFilterAndExport
import userInterface.argumentParser.console.ConsoleArgumentParser
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter


/**
 * handles the controlling
 */
class ImageController extends Controller[Array[String]] {

  private val parsedArguments = new ConsoleArgumentParser
  private val toImageConverter = new ArgumentToImageConverter
  private val toTableConverter = new ArgumentToTableConverter
  private val applyFilterAndExport = new ApplyFilterAndExport
  /**
   * two arguments -> table, image will be read
   * the rest of the args will be processed in
   * inputted order(most recommended from course page)
   *
   * @param commandOptions contains all args
   */
  def processArguments(commandOptions: Array[String]): Unit = {

    try {
      val mappedValues: Map[String, Any] = parsedArguments.parseArguments(commandOptions)
      var rawImage: RGBImage = RGBImage(RGBGrid(Seq.empty[Seq[RGBPixel]]))
      rawImage = toImageConverter.convertArgumentTo(mappedValues)
      val table: Table[String] = toTableConverter.convertArgumentTo(mappedValues)
      //print(mappedValues)
      applyFilterAndExport.processHandler(mappedValues, commandOptions, rawImage, table)
    }
    catch {
      case e: Exception =>
        val consoleArgumentPresenter = new ConsoleArgumentPresenter
        consoleArgumentPresenter.throwingError(s"Error ${e.getMessage}")
    }
  }
}
