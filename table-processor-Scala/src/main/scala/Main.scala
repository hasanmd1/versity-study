package Main

import argumentParser.ArgumentParser
import exporter.{ConsoleExporter, FileExporter}
import importer.FileImporter
import processor.ProcessArguments

object Main extends App {
  private def run(inputPath: String, outputSeq: (String, Boolean), filters: List[String], separator: String): Unit = {
    // Handling import
    val importer = new FileImporter(inputPath)
    var table = importer.importF

    // Handling processing of table
    val processor = new ProcessArguments(table, filters)
    table = processor.applyFilters()

    // Handling output
    val outputHandle = new FileExporter(table, outputSeq, separator)
    val outputHandle2 = new ConsoleExporter(table, outputSeq, separator)
    outputHandle.`export`()
    outputHandle2.`export`()
  }

  // Application entry point
  runFromArgs(args)

  // Helper method to parse command-line arguments and run the application
  def runFromArgs(args: Array[String]): Unit = {
    val output = new ArgumentParser(args.toList)
    val inputPath = output.collectInput
    val outputSeq = output.collectOutput
    val filters = output.collectFilter
    val separator = output.getSeparator

    run(inputPath, outputSeq, filters, separator)
  }
}
