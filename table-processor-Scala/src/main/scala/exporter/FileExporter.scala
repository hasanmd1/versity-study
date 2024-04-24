package exporter

import model.dataModel.EachTable

import java.io.{File, PrintWriter}

class FileExporter(table: EachTable[Any], outputSeq: (String, Boolean), separator: String) extends Exporter[Unit] {

  def handleCsv(): Unit = {
    val file1 = new File(outputSeq._1)
    val writer = new PrintWriter(file1)

    try {
      if (!file1.exists()) {
        file1.createNewFile()
      }
      table.rows.foreach(row => {
        writer.println(row.cells.map(_.getValue).mkString(separator))
      })
    } finally {
      writer.close()
    }
  }

  def handleMd(): Unit = {
    val file = new File(outputSeq._1)
    val writer = new PrintWriter(file)

    try {
      if (!file.exists()) {
        file.createNewFile()
      }
      val headerRow = table.getRow(0)
      for (index <- 0 until headerRow.getLength) {
        writer.print("|" + headerRow.getCell(index).getValue)
      }
      writer.println("|")
      // Print the separator row
      for (_ <- 0 until headerRow.getLength) {
        writer.print("|---")
      }
      writer.println("|")

      // Print each row (starting from the second row)
      table.rows.drop(1).foreach(row => {
        writer.println("|" + row.cells.map(_.getValue).mkString("|") + "|")
      })
    } finally {
      writer.close()
    }
  }

  override def export(): Unit = {
    if(outputSeq._1.nonEmpty){
      try{
        if (outputSeq._1.endsWith(".md")){
          handleMd()
        }else if(outputSeq._1.endsWith(".csv")){
          handleCsv()
        }
        else{
          throw new Exception(s"Wrong output file format ${outputSeq._1}\n")
        }
      }
      catch{
        case e: Exception=>
          throw new Exception(e.getMessage)
      }
    }
    //can be extended for other formats by adding else if case
  }
}
