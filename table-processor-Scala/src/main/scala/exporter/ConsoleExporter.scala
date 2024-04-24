package exporter

import model.dataModel.EachTable

class ConsoleExporter(table: EachTable[Any], outputSeq: (String, Boolean), separator: String) extends Exporter[Unit] {

  override def `export`(): Unit = {
    if(outputSeq._2){
      table.rows.foreach(row => {
        println(row.cells.map(_.getValue).mkString(separator))
      })
    }
  }
}
