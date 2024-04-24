package processor

import importer.FileImporter
import org.scalatest.FunSuite

class ProcessArgumentTest extends FunSuite{
  test("Applying filters should return a processed table") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter", "B > 25")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 2)
  }
  test("Applying filters should return a processed table with headers") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter", "B > 25", "--headers")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 3)
  }
  test("Applying filters and filter-non-empty should return a processed table with headers") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter-is-not-empty", "B","--filter", "B < 25", "--headers")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 4)
  }

  test("Applying filters and filter-empty should return a processed table with headers") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter-is-empty", "B","--filter", "B < 25", "--headers")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 2)
  }
  test("Applying filters and filter-empty should return a processed table") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter-is-empty", "B","--filter", "B < 25")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 1)
  }
  test("Applying filters and filter-empty should return a processed table with range") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter-is-empty", "B","--filter", "B < 25", "--range", "A0 B6")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 1)
  }
  test("Applying filters and filter-empty should return a processed table with range, headers") {
    val sampleTable = new FileImporter("./src/assets/input.csv").importF
    val filters = List("--filter-is-empty", "B", "--headers","--filter","B < 25", "--range", "A0 B6")
    val processor = new ProcessArguments(sampleTable, filters)
    val result = processor.applyFilters()

    assert(result.getRowCount == 2)
  }

}
