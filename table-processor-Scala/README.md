# Table Processor

The main goal of the project is to create an application to process tables with formulas. We should be able to load the table, evaluate all formulas found in the table, filter the rows by their values in specified columns, and finally print the result.

[Course Page with assignement](https://courses.fit.cvut.cz/BI-OOP/projects/table-processor.html)

# Possible args
 --input-file "FileName.csv"
 --separator-cell ",/;/TAB"
 --stdout / --output-file "FileName" / --output-format [Type(.md / .csv)] generate a file of type and output
 --header (for including headerNames)
 --filter column (operator value) / --filter-is-not-empty column
 --range B2 D4 like this

# evaluating formula
 our formula is evaluated from right to left

### We have used input.csv file for some test cases to randomize tests please do not remove or change that file
 