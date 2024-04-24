package exporter.art

import dataModel.asciiImage.ASCIIImage
import exporter.Exporter
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

import java.io.{File, FileWriter}

class ExportToFile(outputPath: String) extends Exporter[ASCIIImage] {

  private val consoleArgumentPresenter = new ConsoleArgumentPresenter
  private var closed = false
  //additional method for closing OutputStream manually
  def closeOutputFile(): Unit = {
    closed = true
  }
  /**
   * export image Art
   *
   * @param image which image will be exported
   */
  override def exportArt(image: ASCIIImage): Unit = {
    if(!outputPath.endsWith(".txt")){
      consoleArgumentPresenter.throwingError("Only output file of type .txt allowed")
    }
    //this part we wrote for testing(for more see test of export to file)
    val outputFile = new File(outputPath)
    if(!outputFile.exists()){
      closeOutputFile()
    }
    if(closed){
      consoleArgumentPresenter.throwingError("The file is closed already.\n")
    }
    val writerFile = new FileWriter(new File(outputPath))
    try {
      for (i <- 0 until image.getHeight) {
        val line = (0 until image.getWidth).map(j => image.getPixelValues(i, j).character).mkString
        writerFile.write(line + "\n")
      }
    } finally {
      writerFile.close()
    }
  }
}
