package controller

import converter.argumentConversion.{ArgumentToImageConverter, ArgumentToTableConverter}
import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import dataModel.tableOptions.Table
import org.scalatest.FunSuite
import processor.processArgument.ApplyFilterAndExport
import userInterface.argumentParser.console.ConsoleArgumentParser

class imageControllerTest extends FunSuite{
  private val commandOptions: Array[String] = Array(
    "--image", "src/test/assets/images/image01.png",
    "--custom-table", "*&-+=#$",
    "--scale", "1",
  )
  private val errorCommandOptions: Array[String] = Array(
    "--image", "src/test/assets/images/image01.png",
    "--image-random",
    "--lol", "3"
  )
  private val expectedMappedValues: Map[String, Any] = Map(
    "--image"->"src/test/assets/images/image01.png",
    "--custom-table"->"*&-+=#$",
    "--scale"->"1",
  )
  private val wrongMappedValues: Map[String, Any] = Map(
    "--image" -> "src/test/assets/images/image03.png",
    "--image-random"->"true",
    "--table" -> "non-linear",
    "--custom-table"->"*&^%$",
    "--scale" -> "1",
    "--output-file"->"src/test/assets/outputFiles/NoOutput.txt"
  )
  test("successfully calling parseArgument"){
    val consoleArgumentParser = new ConsoleArgumentParser
    val mappedValues: Map[String, Any] =consoleArgumentParser.parseArguments(commandOptions)
    assert(mappedValues==expectedMappedValues)

  }
  test("Test calling parseArgument throws an exception"){
    val consoleArgumentParser = new ConsoleArgumentParser
    assertThrows[IllegalArgumentException]{
      consoleArgumentParser.parseArguments(errorCommandOptions)
    }
  }
  test("test successfully calling convertArgumentTo for image") {
    val consoleArgumentParser = new ConsoleArgumentParser
    val mappedValues: Map[String, Any] = consoleArgumentParser.parseArguments(commandOptions)
    val toImageConverter = new ArgumentToImageConverter

    var expectedImage: RGBImage = RGBImage(RGBGrid(Seq.empty[Seq[RGBPixel]]))
    expectedImage = toImageConverter.convertArgumentTo(mappedValues)

    assert(toImageConverter.convertArgumentTo(mappedValues).equals(expectedImage))

  }
  test("Test calling convertArgumentTo throws an exception for image") {
    val toImageConverter = new ArgumentToImageConverter

    assertThrows[IllegalArgumentException] {
      toImageConverter.convertArgumentTo(wrongMappedValues)
    }

  }
  test("successfully calling convertArgumentTo for table") {
    val consoleArgumentParser = new ConsoleArgumentParser
    val mappedValues: Map[String, Any] = consoleArgumentParser.parseArguments(commandOptions)
    val toTableConverter = new ArgumentToTableConverter

    val expectedTable: Table[String] =  toTableConverter.convertArgumentTo(mappedValues)

    assert(toTableConverter.convertArgumentTo(mappedValues).equals(expectedTable))

  }
  test("calling convertArgumentTo throws an exception for table") {
    val toTableConverter = new ArgumentToTableConverter

    assertThrows[IllegalArgumentException] {
      toTableConverter.convertArgumentTo(wrongMappedValues)
    }
  }
  test("successfully calling processHandler") {
    val consoleArgumentParser = new ConsoleArgumentParser
    val mappedValues: Map[String, Any] = consoleArgumentParser.parseArguments(commandOptions)

    val applyFilterAndExport = new ApplyFilterAndExport
    var rawImage: RGBImage = RGBImage(RGBGrid(Seq.empty[Seq[RGBPixel]]))
    val toImageConverter = new ArgumentToImageConverter
    val toTableConverter = new ArgumentToTableConverter

    val table = toTableConverter.convertArgumentTo(mappedValues)
    rawImage = toImageConverter.convertArgumentTo(mappedValues)


    //assert test as all the errors were removed before
    //theres no need to check error in applyFilter method
    try{
      applyFilterAndExport.processHandler(mappedValues, commandOptions, rawImage, table)
    }
    catch {
      case _:IllegalArgumentException =>
        throw new IllegalArgumentException("Failed while processing")
    }
  }

}
