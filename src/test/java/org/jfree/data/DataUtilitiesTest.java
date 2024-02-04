package org.jfree.data;

import static org.jfree.data.DataUtilities.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.io.CSV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * This set of test cases exercises the several methods in the <code>DataUtilities</code> class in JFreeChart.
 */
class DataUtilitiesTest {
	/**
	 * The number of columns in <code>HousePrices.csv</code>.
	 */
	private static final int HOUSE_PRICES_COLUMN_COUNT = 13;

	/**
	 * The number of rows in <code>HousePrices.csv</code>.
	 */
	public static final int HOUSE_PRICES_ROW_COUNT = 546;
	
	/**
	 * The datasets which will be used in the various tests; one which is completely valid, and another which is intentionally empty (and invalid).
	 */
	public static DefaultCategoryDataset housePrices, housePricesEmptyTable;

	/**
	 * Setup the objects needed for the test cases to run.
	 * 
	 * @throws IOException Thrown if there is an issue reading the category dataset from the CSV.
	 * @throws FileNotFoundException Thrown if the file behind the category dataset is not found.
	 */
	@BeforeAll
	public static void setup() throws IOException, FileNotFoundException {
		Path resourcesPath = Paths.get("src", "test", "resources");
		String resourcesDirectory = resourcesPath.toFile().getAbsolutePath();

		String housePricesCSV = Paths.get(resourcesDirectory, "/HousePrices.csv").toAbsolutePath().toFile()
				.getAbsolutePath();
		housePrices = (DefaultCategoryDataset) new CSV().readCategoryDataset(new FileReader(housePricesCSV));

		// Create another table like housePrices, but clear the table.
		housePricesEmptyTable = (DefaultCategoryDataset) new CSV().readCategoryDataset(new FileReader(housePricesCSV));
		housePricesEmptyTable.clear();
	}

	/**
	 * It is only possible for the method to return zero if all of the numbers in
	 * the Values2D object are <code>NULL</code>, as a column of values whose number
	 * values are all zero is not an invalid test case, though that is the other
	 * instance in which the value returned is zero.
	 */
	@Test
	public void calculateColumnTotalReturnsZeroWithInvalidInput() {
		final int COLUMN = 0;
		final double COLUMN_SUM = 149331.0;

		assertSame(COLUMN_SUM, calculateColumnTotal(housePrices, COLUMN));
	}

	/**
	 * On the assumption that a NULL value in a CSV file is invalid, because the
	 * specification is ambiguous, the method should throw an
	 * {@link java.security.InvalidParameterException}. The only reasonable way to
	 * produce a NULL value in the dataset is to clear the table; a NULL value in a
	 * CSV file cannot be read by JFreeChart.
	 * 
	 * TODO: learn how to generate invalid Values2D data objects.
	 */
	@Test
	public void invalidParameterExceptionThrown() {
		final int COLUMN = 0;
		assertThrows(java.security.InvalidParameterException.class,
				() -> calculateColumnTotal(housePricesEmptyTable, COLUMN));
	}

	/**
	 * The method should be able to calculate the correct sum for each column. Given
	 * that the test input file has different sums for each column, any incorrect
	 * result means that the method is either calculating the correct sum of the
	 * incorrect column, or it is not calculating the correct sum for the correct
	 * column. Either possibility is a defect.
	 * ﻿
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Expected</th>
	 * <th>Column index</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>149331</td>
	 * <td>0</td>
	 * </tr>
	 * <tr>
	 * <td>37194392</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>2812045</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td>1619</td>
	 * <td>3</td>
	 * </tr>
	 * <tr>
	 * <td>702</td>
	 * <td>4</td>
	 * </tr>
	 * <tr>
	 * <td>987</td>
	 * <td>5</td>
	 * </tr>
	 * <tr>
	 * <td>469</td>
	 * <td>6</td>
	 * </tr>
	 * <tr>
	 * <td>97</td>
	 * <td>7</td>
	 * </tr>
	 * <tr>
	 * <td>191</td>
	 * <td>8</td>
	 * </tr>
	 * <tr>
	 * <td>25</td>
	 * <td>9</td>
	 * </tr>
	 * <tr>
	 * <td>173</td>
	 * <td>10</td>
	 * </tr>
	 * <tr>
	 * <td>378</td>
	 * <td>11</td>
	 * </tr>
	 * <tr>
	 * <td>128</td>
	 * <td>12</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * FIXME: there are thirteen columns in the CSV <code>HousePrices.csv</code>;
	 * why is this occurring?
	 * 
	 * <pre>
	 * <code>
	 * java.lang.IndexOutOfBoundsException: Index 12 out of bounds for length 12
	 *         at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
	 *         at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
	 *         at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
	 *         at java.base/java.util.Objects.checkIndex(Objects.java:385)
	 *         at java.base/java.util.ArrayList.get(ArrayList.java:427)
	 *         at org.jfree.data.DefaultKeyedValues2D.getValue(DefaultKeyedValues2D.java:136)
	 *         at org.jfree.data.category.DefaultCategoryDataset.getValue(DefaultCategoryDataset.java:102)
	 *         at org.jfree.data.DataUtilities.calculateColumnTotal(DataUtilities.java:69)
	 * </code>
	 * </pre>
	 *
	 * @param expected The test oracle
	 * @param column   The column for which the oracle value applies.
	 */
	@ParameterizedTest
	@ArgumentsSource(CorrectColumnSumsArguments.class)
	public void correctColumnSums(double expected, int column) {
		assumeTrue(housePrices.getColumnCount() == HOUSE_PRICES_COLUMN_COUNT
				&& housePrices.getRowCount() == HOUSE_PRICES_ROW_COUNT);
		
		assertSame(expected, calculateColumnTotal(housePrices, column));
	}
	
	/**
	 * Ensures all rows are totaled correctly.
	 * @param expected
	 * @param row
	 */
	@ParameterizedTest
	@ArgumentsSource(CorrectRowSumsArguments.class)
	public void correctRowSums(double expected, int row) {
		assumeTrue(housePrices.getColumnCount() == HOUSE_PRICES_COLUMN_COUNT
				&& housePrices.getRowCount() == HOUSE_PRICES_ROW_COUNT);
		
		assertSame(expected, calculateRowTotal(housePrices, row));
	}
	
	/**
	 * It is only possible for the method to return zero if all of the numbers in
	 * the Values2D object are <code>NULL</code>, as a column of values whose number
	 * values are all zero is not an invalid test case, though that is the other
	 * instance in which the value returned is zero.
	 * 
	 * Pseudo-backtrace for the production of <code>NULL</code> values used by <code>calculateRowTotal</code>:
	 * <ol>
	 * <li>org.jfree.data.DefaultKeyedValues2D.rows.get(int)</li>
	 * <li>org.jfree.data.DefaultKeyedValues2D.getValue(int, int)</li>
	 * <li>org.jfree.data.calculateRowTotal(Values2D, int)</li>
	 * </ol>
	 */
	@Test
	public void calculateRowTotalReturnsZeroWithInvalidInput() {
		final int ROW = 1; // It's over nine thousand.
		final double ROW_SUM = 149331.0;
		assertSame(ROW_SUM, calculateRowTotal(housePrices, ROW));
	}
}
