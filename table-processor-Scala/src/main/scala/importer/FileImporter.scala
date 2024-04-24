package importer

import model.dataModel.{EachCell, EachRow, EachTable}

import java.io.File
import scala.io.Source

class FileImporter(filePath: String) extends Importer[EachTable[Any]] {

  override def importF: EachTable[Any] = {
    val file = new File(filePath)
    if (!file.exists() || !file.isFile || !filePath.endsWith(".csv")) {
      throw new IllegalArgumentException(s"Invalid file: $filePath")
    }
    val source = Source.fromFile(file)
    try {
      var rows: List[EachRow[Any]] = List()

      for (line <- source.getLines()) {
        val values = line.split(",").map(_.trim)
        val row = EachRow(values.map(value => parseCellValue(value, "String")).toVector)
        rows = rows :+ row
      }
      EachTable(rows)
    } finally {
      source.close()
    }
  }

  private def parseCellValue(cellString: String, defaultType: String): EachCell[Any] = {
    try {
      EachCell(cellString.toInt, "Int")
    } catch {
      case _: NumberFormatException =>
        if (cellString.isEmpty){
          EachCell(cellString, "Empty")
        } else if (cellString.startsWith("=")) {
          EachCell(cellString, "Formula")
        } else {
          EachCell(cellString, defaultType)
        }
    }
  }

}
