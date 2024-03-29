package org.jfree.data;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.opencsv.CSVReader;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

class CorrectRowSumsArguments implements ArgumentsProvider {
	/**
	 * Copied from https://www.baeldung.com/opencsv. Used for educational purposes
	 * under Canadian copyright law.
	 *
	 * @throws {@link Exception}
	 */
	public static List<String[]> readAllLines(Path filePath) throws Exception {
		try (Reader reader = Files.newBufferedReader(filePath)) {
			try (CSVReader csvReader = new CSVReader(reader)) {
				return csvReader.readAll();
			}
		}
	}

	/**
	 * Create a stream of object arrays (each of length two) to provide the
	 * parameterized test <code>correctRowSums</code> with the arguments it requires
	 * during each iteration of the test.
	 * <br/>
	 * <em>NOTE:</em> The ordering of the value and row-column is opposite that from the
	 * <code>HousePricesColumnSums.csv</code>, wherein the other file has the sum of
	 * the column in the first field, and the column index in the second field. This
	 * CSV has the row index in the first field, and the sum of the row in the
	 * second field.
	 *
	 * @throws {@link Exception} if there is an issue reading the CSV file, then an
	 *                exception is thrown.
	 */
	public static Stream<Object[]> generateRecordsFromCSVForCorrectRowSums() throws Exception {
		Path resourcesPath = Paths.get("src", "test", "resources");
		String resourcesDirectory = resourcesPath.toFile().getAbsolutePath();
		Path housePricesRowSumsCSV = Paths.get(resourcesDirectory, "/HousePricesRowSums.csv").toAbsolutePath();

		List<String[]> records = readAllLines(housePricesRowSumsCSV);
		Object[][] arguments = new Object[records.size()][2];

		int i = 0;
		/** Index into <code>arguments</code>. */
		while (!records.isEmpty()) {
			String[] record = records.remove(0);
			arguments[i][0] = record[0]; // Row sum
			arguments[i++][1] = record[1]; // Row number
		}

		return Stream.of(arguments);
	}

	/**
	 * I'm unsure if <code>org.junit.jupiter.params.provider.Arguments</code> is the
	 * correct class. The JUnit documentation doesn't annotate the example code to
	 * my satisfaction.
	 */
	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return generateRecordsFromCSVForCorrectRowSums().map(Arguments::of);
	}
}
