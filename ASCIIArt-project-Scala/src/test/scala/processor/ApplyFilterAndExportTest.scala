package processor

import dataModel.asciiImage.RGBImage
import dataModel.tableOptions.Table
import dataModel.tableOptions.possibleOptions.BourkeSmallTable
import importer.imageImporter.RandomImageImporter
import org.scalatest.FunSuite
import processor.processArgument.ApplyFilterAndExport

import java.nio.file.{Files, Paths}

/**
 * to be noted we have hardly any case of error for arguments
 * so we will only test that passed values/paths are correct
 *
 */
class ApplyFilterAndExportTest extends FunSuite{

  private val table: Table[String] = new BourkeSmallTable

  private val successMap: Map[String, Any] = Map(
    "--output-console" -> true,
    "--output-file" -> "src/test/assets/outputFiles/output.txt",
    "--rotate" -> 90.0,
    "--scale" -> 0.1,
    "--invert" -> true,
    "--brightness" -> 50,
    "--flip" -> "x"
  )
  private val successArguments: Array[String] = Array(
    "--rotate" , "90.0",
    "--scale" , "0.1",
    "--invert" ,
    "--brightness" , "50",
    "--flip" , "x",
    "--output-console",
    "--output-file", "src/test/assets/outputFiles/output.txt",
  )
  private val rawImage: RGBImage = new RandomImageImporter().importImage()

  test("Test successful ApplyFilterAndExport processHandler calls") {

    val processor = new ApplyFilterAndExport
    processor.processHandler(successMap, successArguments, rawImage, table)

    val filePath = "src/test/assets/outputFiles/output.txt"
    val fileContent = new String(Files.readAllBytes(Paths.get(filePath)))
    val consoleOutput: String = Console.out.toString

    assert(fileContent.nonEmpty)
    assert(consoleOutput.nonEmpty)

  }
  test("test fails when output-file throws error"){
    val errorMap: Map[String, Any] = Map(
      "--output-file" -> "src/test/outputFiles/ErrorOutput.txt", //wrong output
    )
    val errorArguments: Array[String] = Array(
      "--output-file", "src/test/outputFiles/ErrorOutput.txt", //wrong output
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when output-console throws error") {
    val errorMap: Map[String, Any] = Map(
      "--output-console" -> "invalid", //wrong output
    )
    val errorArguments: Array[String] = Array(
      "--output-console", "fds"//wrong output
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when rotate throws error") {
    val errorMap: Map[String, Any] = Map(
      "--rotate" -> "invalid", //wrong rotate arg
    )
    val errorArguments: Array[String] = Array(
      "--rotate", "invalid" //wrong arg
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when brightness throws error") {
    val errorMap: Map[String, Any] = Map(
      "--brightness" -> "invalid", //wrong brightness arg
    )
    val errorArguments: Array[String] = Array(
      "--brightness", "invalid" //wrong arg
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when flip throws error") {
    val errorMap: Map[String, Any] = Map(
      "--flip" -> "z", //wrong brightness arg
    )
    val errorArguments: Array[String] = Array(
      "--flip", "z" //wrong arg
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when invert throws error") {
    val errorMap: Map[String, Any] = Map(
      "--invert" -> "invert", //wrong brightness arg
    )
    val errorArguments: Array[String] = Array(
      "--invert", "invalid" //wrong arg
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
  test("test fails when scale throws error") {
    val errorMap: Map[String, Any] = Map(
      "--scale" -> "invalid", //wrong arg
    )
    val errorArguments: Array[String] = Array(
      "--scale", "invalid" //wrong arg
    )
    val processor = new ApplyFilterAndExport
    intercept[Exception] {
      processor.processHandler(errorMap, errorArguments, rawImage, table)
    }
  }
}
