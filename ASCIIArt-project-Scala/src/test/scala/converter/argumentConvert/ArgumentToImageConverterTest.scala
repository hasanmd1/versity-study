package converter.argumentConvert

import converter.argumentConversion.ArgumentToImageConverter
import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import org.scalatest.FunSuite

class ArgumentToImageConverterTest extends FunSuite{

  test("successfully converting --image argument to RGBArt") {
    val converter = new ArgumentToImageConverter
    val imagePath = "src/test/assets/images/image01.png"
    val mappedValues: Map[String, Any] = Map("--image" -> imagePath)

    val rawImage: RGBImage = converter.convertArgumentTo(mappedValues)

    assert(rawImage.isInstanceOf[RGBImage])
    assert(rawImage.grid.isInstanceOf[RGBGrid])
    assert(rawImage == converter.convertArgumentTo(mappedValues))
  }

  test("throwing an exception when --image file does not exist") {
    val converter = new ArgumentToImageConverter
    val imagePath = "src/test/assets/images/nonexistent.png"
    val mappedValues: Map[String, Any] = Map("--image" -> imagePath)

    assertThrows[IllegalArgumentException] {
      converter.convertArgumentTo(mappedValues)
    }
  }

  test("successfully converting --image-random argument to RGBArt") {
    val converter = new ArgumentToImageConverter
    val mappedValues: Map[String, Any] = Map("--image-random" -> true)

    val rawImage: RGBImage = converter.convertArgumentTo(mappedValues)

    assert(rawImage.isInstanceOf[RGBImage])
    assert(rawImage.grid.isInstanceOf[RGBGrid])
  }

  test("converting neither --image nor --image-random throws an exception") {
    val converter = new ArgumentToImageConverter
    val mappedValues: Map[String, Any] = Map.empty[String, Any]

    assertThrows[IllegalArgumentException] {
      converter.convertArgumentTo(mappedValues)
    }
  }

  test("throwing an exception when both --image and --image-random arguments are provided") {
    val converter = new ArgumentToImageConverter
    val imagePath = "src/test/assets/images/image01.png"
    val mappedValues: Map[String, Any] = Map("--image" -> imagePath, "--image-random" -> true)

    assertThrows[IllegalArgumentException] {
      converter.convertArgumentTo(mappedValues)
    }
  }
}
