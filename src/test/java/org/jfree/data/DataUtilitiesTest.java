package org.jfree.data;

import static org.jfree.data.DataUtilities.calculateColumnTotal;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

class DataUtilitiesTest {
    final public int HOUSE_PRICES_ROW_COUNT = 546;
    public static DefaultCategoryDataset housePrices, housePricesEmptyTable;

    /**
     * @throws IOException
     * @throws FileNotFoundException
     */
    @BeforeAll
    public static void setup() throws IOException, FileNotFoundException {
        Path resourcesPath = Paths.get("src", "test", "resources");
        String resourcesDirectory = resourcesPath.toFile().getAbsolutePath();

        String housePricesCSV = Paths.get(resourcesDirectory, "/HousePrices.csv")
                .toAbsolutePath()
                .toFile()
                .getAbsolutePath();
        housePrices = (DefaultCategoryDataset) new CSV()
                .readCategoryDataset(new FileReader(housePricesCSV));

        // Create another table like housePrices, but clear the table.
        housePricesEmptyTable = (DefaultCategoryDataset) new CSV()
                .readCategoryDataset(new FileReader(housePricesCSV));
        housePricesEmptyTable.clear();
    }

    /**
     * On the assumption that a NULL value in a CSV file is invalid, because the
     * specification is ambiguous, the method should return zero.
     * 
     * It is only possible for the method to return zero if all of the numbers in the Values2D object are <code>NULL</code>, as a column of values whose number values are all zero is not an invalid test case, though that is the other instance in which the value returned is zero.
     */
    @Test
    public void zeroReturned() {
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
	 * 
	 * <pre>
	 * 149331,0
	 * 37194392,1
	 * 2812045,2
	 * 1619,3
	 * 702,4
	 * 987,5
	 * 469,6
	 * 97,7
	 * 191,8
	 * 25,9
	 * 173,10
	 * 378,11
	 * 128,12
	 * </pre>
	 * 
	 * FIXME: there are thirteen columns in the CSV <code>HousePrices.csv</code>; why is this occurring?
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
	 * <code>
	 * </pre>
	 *
	 * @param expected The test oracle
	 * @param column   The column for which the oracle value applies.
	 */
    @ParameterizedTest
    @ArgumentsSource(CorrectColumnSumsArguments.class)
    public void correctColumnSums(double expected, int column) {
        assertSame(expected, calculateColumnTotal(housePrices, column));
    }
}
