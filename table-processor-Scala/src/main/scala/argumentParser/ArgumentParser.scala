package argumentParser

import model.dataModel.EachTable

import scala.collection.View.Empty
import scala.io.BufferedSource

class ArgumentParser(arguments : List[String]) extends Parser [String, List[String], (String, Boolean)] {
  if(arguments.contains("--help")){
    println(s"--input-file [FILE] -> file should be only .csv format and only 1 workbook.")

  }

  private val length = arguments.length
  private val setValues = Seq("--input-file", "--header", "--output-file", "--filter", "--stdout", "--separator", "--range")
  override def collectInput: String = {
    val index = arguments.indexOf("--input-file")
    if (index != -1 && (index >= length - 1 || arguments(index+1).contains(setValues) || arguments(index+1).startsWith("--"))){
      throw new IllegalArgumentException("Input file is not passed.\n")
    }
    arguments(index + 1)
  }

  override def collectOutput: (String, Boolean) = {
    if(arguments.contains("--help")){
      println(s"--output-file [FILE] -> file should be only .csv or .md format and only 1 workbook.")
      println(s"--stdout -> will print output to console")
    }
    val indexFile = arguments.indexOf("--output-file")
    val stdOut = arguments.indexOf("--stdout")
    val inputFile = ""

    if (indexFile != -1 && arguments(indexFile + 1).startsWith("--")){
      throw new IllegalArgumentException("Output filename not specified.\n")
    }


    val outputFile = if (indexFile != -1 && indexFile < arguments.length - 1) {
      arguments(indexFile + 1)
    } else {
      ""
    }
    val stdOUT = if (stdOut != -1 && indexFile <= arguments.length - 1){
      true
    }else{
      false
    }

    (outputFile, stdOUT)
  }

  override def collectFilter: List[String] = {
    var filters: List[String] = List()
    var i = 0
    while(i < length){
      arguments(i) match {
        case "--headers" =>
          if(i != length - 1 && !arguments(i+1).startsWith("--")){
            throw new IllegalArgumentException("Unknown value for --headers.")
          }
          filters :+= "--headers"
          i += 1
        case "--filter" =>
          if (i + 1 < arguments.length && !arguments(i+1).startsWith("--")) {
            filters :+= "--filter"
            filters :+= arguments(i+1)
            i += 2
          } else {
            throw new IllegalArgumentException("Missing value for --filter.")
          }
        case "--filter-is-not-empty" =>
          if(i + 1 < arguments.length && !arguments(i+1).startsWith("--")){
            filters :+= "--filter-is-not-empty"
            filters :+= arguments(i+1)
            i += 2
          }
          else{
            throw new IllegalArgumentException("Missing value for --filter-is-not-empty.")
          }

        case "--filter-is-empty" =>
          if(i + 1 < arguments.length && !arguments(i+1).startsWith("--")){
            filters :+= "--filter-is-empty"
            filters :+= arguments(i+1)
            i += 2
          }
          else{
            throw new IllegalArgumentException("Missing value for --filter-is-empty.")
          }

        case "--range" =>
          if (i + 1 < arguments.length && !arguments(i+1).startsWith("--")) {
            filters :+= "--range"
            filters :+= arguments(i+1)
            i += 2
          } else {
            throw new IllegalArgumentException("Missing value for --range.")
          }

        case "--help" =>
          println(s"--range [FROM] [TO] -> collecting from certain range. FROM and TO will be [column+row] value")
          println(s"--filter [COLUMN] [OPERATOR] [VALUE] -> for filtering based on condition(< , >, >=, <=, ==, !=)")
          println(s"--headers -> for showing headers for each column")
          println(s"--filter-is-empty [COLUMN] -> filtering rows for empty COLUMN")
          println(s"--filter-is-not-empty [COLUMN] -> filtering rows for non-empty COLUMN")
          i += 1
        case "--input-file" =>
          i += 2
        case "--output-separator" =>
          i += 2
        case "--output-file" =>
          i += 2
        case "--stdout" =>
          i += 1
        case _ =>
          throw new IllegalArgumentException("Wrong Argument.\n")
      }
    }
    filters
  }

  override def getSeparator: String = {
    if(arguments.contains("--help")){
      println(s"--output-separator [SEPARATOR] -> which will be used as separator for printing data from input csv file")
      println(s"--help -> show help for individual allowed argument")
    }
    var separator = ","
    val index = arguments.indexOf("--output-separator")
    if(index != -1){
      separator = arguments(index + 1)
    }else if(index + 1 < length - 1 && arguments(index+1).startsWith("--")){
      throw new Exception("Invalid --output Separator.\n")
    }
    separator
  }
}
