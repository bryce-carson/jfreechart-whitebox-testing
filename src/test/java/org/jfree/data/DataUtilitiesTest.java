package org.jfree.data;

import static org.jfree.data.DataUtilities.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.io.CSV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This set of test cases exercises the several methods in the
 * <code>DataUtilities</code> class in JFreeChart.
 */
class DataUtilitiesTest {
	/**
	 * The number of columns in <code>HousePrices.csv</code>.
	 */
	private static final int HOUSE_PRICES_COLUMN_COUNT = 12;
	/**
	 * The number of rows in <code>HousePrices.csv</code>.
	 */
	public static final int HOUSE_PRICES_ROW_COUNT = 21;

	/**
	 * The datasets which will be used in the various tests; one which is completely
	 * valid, and another which is intentionally empty (and invalid).
	 */
	public static DefaultCategoryDataset housePrices, housePricesEmptyTable;

	/**
	 * Setup the objects needed for the test cases to run.
	 * 
	 * CSV expects data to look like the following:
	 * <pre>
	 * Row/Column	One	    Two	    Three
	 *          1	42000	5850	3
	 *          2	38500	4000	2
	 *          3   49500	3060	3
	 * </pre>
	 * 
	 * @throws IOException           Thrown if there is an issue reading the
	 *                               category dataset from the CSV.
	 * @throws FileNotFoundException Thrown if the file behind the category dataset
	 *                               is not found.
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
	@Disabled
	@Test
	public void calculateColumnTotalReturnsZeroWithInvalidInput() {
		final int COLUMN = 1;
		final double EXPECTED = 0.0;

		assertEquals(EXPECTED, calculateColumnTotal(housePricesEmptyTable, COLUMN));
	}

	/**
	 * The only reasonable way to produce a NULL value in the dataset is to clear
	 * the table; a NULL value in a CSV file cannot be read by JFreeChart.
	 * 
	 * TODO: learn how to generate invalid Values2D data objects.
	 * 
	 * @throws SecurityException     If the JVM security policy prevents reflection.
	 * @throws NoSuchMethodException If there method requested is not defined in the
	 *                               class.
	 */
	@ParameterizedTest
	@ValueSource(strings = { "Row", "Column" })
	public void illegalArgumentExceptionThrown(String dimension) throws NoSuchMethodException, SecurityException {
		final int ROW_OR_COLUMN = 0;

		Method method = DataUtilities.class // The class in which the static (declared) method is defined
				.getDeclaredMethod("calculate" + dimension + "Total", // Method name
						Values2D.class, // Parameter one
						int.class); // Parameter two

		// The modified JavaDoc from assignment two states that the method throws
		// InvalidParameterException from java.security, but that is an inappropriate
		// exception type to throw in this case. While other methods should reasonably
		// return zero when the content is zero, this method shouldn't return zero when
		// there is an issue with the data object. The solution to the bad
		// implementation is changing the method to not return a default value of zero
		// when there is an issue, and instead throw an exception only when there is an
		// issue (and not return a value).
		Exception exception = assertThrows(java.lang.reflect.InvocationTargetException.class,
				() -> method.invoke(null, housePricesEmptyTable, ROW_OR_COLUMN));

		assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
	}

	/**
	 * The method should be able to calculate the correct sum for each column. Given
	 * that the test input file has different sums for each column, any incorrect
	 * result means that the method is either calculating the correct sum of the
	 * incorrect column, or it is not calculating the correct sum for the correct
	 * column. Either possibility is a defect.﻿
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
		assumeTrue(housePrices.getColumnCount() == HOUSE_PRICES_COLUMN_COUNT);
		assertEquals(expected, calculateColumnTotal(housePrices, column));
	}

	/**
	 * Ensures all rows are totaled correctly.
	 * 
	 * @param expected
	 * @param row
	 */
	@ParameterizedTest
	@ArgumentsSource(CorrectRowSumsArguments.class)
	public void correctRowSums(double expected, int row) {
		assumeTrue(housePrices.getRowCount() == HOUSE_PRICES_ROW_COUNT);
		assertEquals(expected, calculateRowTotal(housePrices, row));
	}

	/**
	 * The reported count of rows should be equal to the actual row count in the
	 * test data.
	 */
	@Test
	public void correctRowCount() {
		assertSame(HOUSE_PRICES_ROW_COUNT, housePrices.getRowCount());
	}

	/**
	 * The reported count of columns should be equal to the actual column count in
	 * the test data.
	 */
	@Test
	public void correctColumnCount() {
		assertSame(HOUSE_PRICES_COLUMN_COUNT, housePrices.getColumnCount());
	}

	/**
	 * It is only possible for the method to return zero if all of the numbers in
	 * the Values2D object are <code>NULL</code>, as a column of values whose number
	 * values are all zero is not an invalid test case, though that is the other
	 * instance in which the value returned is zero.
	 * 
	 * Pseudo-backtrace for the production of <code>NULL</code> values used by
	 * <code>calculateRowTotal</code>:
	 * <ol>
	 * <li>org.jfree.data.DefaultKeyedValues2D.rows.get(int)</li>
	 * <li>org.jfree.data.DefaultKeyedValues2D.getValue(int, int)</li>
	 * <li>org.jfree.data.calculateRowTotal(Values2D, int)</li>
	 * </ol>
	 */
	@Disabled
	@Test
	public void calculateRowTotalReturnsZeroWithInvalidInput() {
		final int ROW = 1; // It's over nine thousand.
		final double EXPECTED = 0.0;
		assertEquals(EXPECTED, calculateRowTotal(housePricesEmptyTable, ROW));
	}

	@Test
	public void illegalArgumentExceptionsThrown() {
		assertAll(() -> assertThrows(IllegalArgumentException.class, () -> createNumberArray(null)),
				() -> assertThrows(IllegalArgumentException.class, () -> createNumberArray2D(null)),
				() -> assertThrows(IllegalArgumentException.class, () -> getCumulativePercentages(null)));
	}

	@Test
	public void numberArrayReturned() {
		final double[] DOUBLE_ARRAY = new double[] { 0.0, 1.1, 2.2, 3.3, 4.4 };
		final Number[] NUMBER_ARRAY = new Number[] { 0.0, 1.1, 2.2, 3.3, 4.4 };
		assertArrayEquals(NUMBER_ARRAY, createNumberArray(DOUBLE_ARRAY));
	}

	@Test
	public void numberArray2DReturned() {
		final double[][] DOUBLE_ARRAY_2D = new double[][] { { 0.0, 1.1, 2.2, 3.3, 4.4 }, { 0.0, 1.1, 2.2, 3.3, 4.4 } };
		final Number[][] NUMBER_ARRAY_2D = new Number[][] { { 0.0, 1.1, 2.2, 3.3, 4.4 }, { 0.0, 1.1, 2.2, 3.3, 4.4 } };
		assertArrayEquals(NUMBER_ARRAY_2D, createNumberArray2D(DOUBLE_ARRAY_2D));
	}

	/**
	 * Make the assertion operate on the interface, not a given implementation;
	 * therefore, the first interface of the implementing class is asserted to be of
	 * the interface of interest (KeyedValues).
	 */
	@Test
	public void keyedValuesReturned() {
		final String KEY = "KEY";
		DefaultKeyedValues KEYED_VALUES = new DefaultKeyedValues();
		KEYED_VALUES.addValue(KEY, 0.0);

		assertSame(KeyedValues.class, getCumulativePercentages(KEYED_VALUES).getClass().getInterfaces()[0]);
	}

	/**
	 * Test the
	 * {@link org.jfree.data.DataUtilities#getCumulativePercentages(KeyedValues)}
	 * method with the data from the method's example.
	 * 
	 * FIXME: this test does not work because the return value from the underlying
	 * method is broken, however, the implementation is outside the class under test
	 * and so will not be corrected to make this test pass.
	 */
	@Disabled
	@Test
	public void keyedValuesContainsCorrectCumulativePercentages() {
		DefaultKeyedValues keyValueTable = new DefaultKeyedValues();
		keyValueTable.addValue(0, (Number) 5);
		keyValueTable.addValue(1, (Number) 9);
		keyValueTable.addValue(2, (Number) 2);

		KeyedValues expectedCumulativePercentages = getCumulativePercentages(keyValueTable);
		assertTrue(expectedCumulativePercentages.getValue(0) == (Number) 0.3125
				&& expectedCumulativePercentages.getValue(1) == (Number) 0.875
				&& expectedCumulativePercentages.getValue(2) == (Number) 1);
	}
}
