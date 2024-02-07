package org.jfree.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.security.InvalidParameterException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import static org.jfree.data.Range.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This class tests the Range class in <code>JFree.data</code>.
 * 
 * @author Bryce Carson
 * @version 1.0.0
 */
class RangeTest {
	
	/** A parameter of shift and expand methods. */
	public static final double ONE_HUNDRED_PERCENT = 1.00, ZERO_PERCENT = 0.00;

	/** A parameter of the range object for the class under test. */
	public static double LOWER_BOUND, UPPER_BOUND, DIFFERENCE;

	/** The range object used to test the class under test. */
	public static Range range;

	/** This method initializes the static members of the test class. */
	@BeforeAll
	public static void setup() {
		LOWER_BOUND = 0.0;
		UPPER_BOUND = 100.0;
		DIFFERENCE = UPPER_BOUND - LOWER_BOUND;

		/** The range object used in all of the tests. */
		range = new Range(LOWER_BOUND, UPPER_BOUND);
	}

	/**
	 * The lower and upper bound arguments are reversed in this invocation
	 * intentionally.
	 */
	@Test
	public void illegalArgumentExceptionThrown() {
		assertThrows(java.lang.IllegalArgumentException.class, () -> new Range(UPPER_BOUND, LOWER_BOUND));
	}

	/** A simple arithmetical test. */
	@Test
	public void getLowerBoundReturnsLowerBound() {
		assertEquals(LOWER_BOUND, range.getLowerBound());
	}

	/** A simple arithmetical test. */
	@Test
	public void getUpperBoundReturnsUpperBound() {
		assertEquals(UPPER_BOUND, range.getUpperBound());
	}

	/** A simple arithmetical test. */
	@Test
	public void rangeObjectHasCorrectDifference() {
		assertEquals(DIFFERENCE, range.getLength());
	}

	/** A simple arithmetical test. */
	@Test
	public void rangeObjectHasCorrectAverage() {
		assertEquals((UPPER_BOUND + LOWER_BOUND) / 2.0, range.getCentralValue());
	}

	/**
	 * A simple arithmetical test of the constructor for
	 * {@link org.jfree.data.Range#Range(double, double)} objects.
	 * 
	 * @param testValue A value which should be contained by the static member
	 *                  <code>range</code> of the test class.
	 */
	@ParameterizedTest
	@ValueSource(doubles = { -10.0, 0.0, 10.0, 100.0, 110.0 })
	public void rangeObjectBoundsCorrect(double testValue) {
		// The range object should not contain the test value if the
		// value is less than or greater than its range.
		boolean EXPECTED = !(testValue < LOWER_BOUND || testValue > UPPER_BOUND);
		assertSame(EXPECTED, range.contains(testValue));
	}

	/**
	 * In order of their definition in the method below, the assertions are:
	 * <ol>
	 * <li>An exact overlap of the ranges,</li>
	 * <li>An overlap of the lower boundary,</li>
	 * <li>An overlap of the upper boundary,</li>
	 * <li>An overlap within boundaries of the dispatching instance,</li>
	 * <li>An overlap, beyond both boundaries,</li>
	 * <li>Non-overlap beyond the upper boundary,</li>
	 * <li>Non-overlap before the lower boundary.</li>
	 * <li>Non-overlap, with illogical parameters,</li>
	 * <li>Non-overlap, again with illogical parameters.</li>
	 * </ol>
	 */
	@Test
	public void intersectionBoundaryTruthTable() {
		assertAll(() -> {
			assertTrue(range.intersects(LOWER_BOUND, UPPER_BOUND));
		}, () -> {
			assertTrue(range.intersects(LOWER_BOUND - 10.0, LOWER_BOUND + 10.0));
		}, () -> {
			assertTrue(range.intersects(UPPER_BOUND - 10.0, UPPER_BOUND + 10.0));
		}, // Fails because first condition of second branch in implementation is "upper"
			// when it should be "lower".
				() -> {
					assertTrue(range.intersects(LOWER_BOUND + 10.0, UPPER_BOUND - 10.0));
				}, () -> {
					assertTrue(range.intersects(LOWER_BOUND - 10.0, UPPER_BOUND + 10.0));
				}, () -> {
					assertFalse(range.intersects(UPPER_BOUND + 10.0, UPPER_BOUND + 20.0));
				}, () -> {
					assertFalse(range.intersects(LOWER_BOUND - 20.0, LOWER_BOUND - 10.0));
				}, () -> {
					assertFalse(range.intersects(UPPER_BOUND + 20.0, UPPER_BOUND + 10.0));
				}, () -> {
					assertFalse(range.intersects(UPPER_BOUND, LOWER_BOUND));
				});
	}

	/**
	 * The missed branch is unreachable because the inner condition is unnecessary,
	 * and the code is unreachable.
	 */
	@Test
	public void rangeConstraintTruthTable() {
		assertAll(() -> {
			assertEquals(UPPER_BOUND, range.constrain(UPPER_BOUND + 1.0));
		}, () -> {
			assertEquals(LOWER_BOUND, range.constrain(LOWER_BOUND - 1.0));
		}, () -> {
			assertEquals(UPPER_BOUND - 1.0, range.constrain(UPPER_BOUND - 1.0));
		}, () -> {
			assertEquals(LOWER_BOUND + 1.0, range.constrain(LOWER_BOUND + 1.0));
		});
	}

	/**
	 * Tests that {@link org.jfree.data.Range#combine(Range, Range)} returns
	 * reasonable objects and values.
	 */
	@Test
	public void saneReturnValuesFromCombine() {
		assertAll(() -> {
			assertNull(Range.combine(null, null));
		},

				() -> {
					assertNotNull(Range.combine(range, range));
				}, () -> {
					assertNotNull(Range.combine(range, null));
				}, () -> {
					assertNotNull(Range.combine(null, range));
				},

				() -> {
					assertEquals(range, Range.combine(range, range));
				}, () -> {
					assertEquals(range, Range.combine(null, range));
				}, () -> {
					assertEquals(range, Range.combine(range, null));
				},

				() -> {
					assertEquals(new Range(-UPPER_BOUND, UPPER_BOUND),
							Range.combine(range, new Range(-UPPER_BOUND, LOWER_BOUND)));
				});
	}

	/**
	 * Technically, the specification does not imply that the same range object will
	 * be returned if the potential value to be included in the range is already
	 * contained by the range object. Therefore, <code>assertEquals</code> is used
	 * and not <code>assertSame</code>.
	 */
	@Test
	public void saneRangeObjectsReturnedFromExpandToInclude() {
		assertAll(() -> {
			assertEquals(range, expandToInclude(range, 25.0));
		}, () -> {
			assertEquals(new Range(25.0, 25.0), expandToInclude(null, 25.0));
		}, () -> {
			assertTrue(expandToInclude(range, 125.0).contains(125.0));
		}, () -> {
			assertTrue(expandToInclude(range, -25.0).contains(-25.0));
		});
	}

	/**
	 * This method is tested very simply because it is mostly arithmetic that
	 * doesn't need checking.
	 * 
	 * @see #expandThrowsIAEForNullRange()
	 * @see #expandThrowsIAEWhenLowerGreaterThanUpper()
	 * 
	 * @param percentage The percentage of the <code>length</code> of the Range
	 *                   object under test.
	 */
	@ParameterizedTest
	@ValueSource(doubles = { 0.00, 0.10, 0.25, 0.50, 0.75, 1.00, 1.25 })
	public void expandReturnsRangeObjectsForReasonableParameters(double percentage) {
		assertSame(Range.class, expand(range, percentage, percentage).getClass());
	}

	/** A simple test for the correct return value. */
	@Test
	public void expandThrowsIAEForNullRange() {
		final double PERCENTAGE = 0.00;
		assertThrows(IllegalArgumentException.class, () -> {
			expand(null, PERCENTAGE, PERCENTAGE);
		});
	}

	/**
	 * This test is disabled because any combination of percentages which result in
	 * the lower bound of the new range being greater than the upper bound of the
	 * range would result in an <code>IllegalArgumentException.class</code> being
	 * thrown by {@link org.jfree.data.Range#Range(double, double)} and hence the
	 * test would be redundant when {@link #illegalArgumentExceptionThrown()}
	 * already tests this behavior.
	 */
	@Disabled
	@Test
	public void expandThrowsIAEWhenLowerGreaterThanUpper() {
		final double PERCENTAGE = -1.50;
		assertThrows(IllegalArgumentException.class, () -> {
			expand(range, PERCENTAGE, PERCENTAGE);
		});
	}

	/**
	 * A simple, silly test for the {@link org.jfree.data.Range#hashCode()} method.
	 */
	@Test
	public void hashCodeIsNotNull() {
		assertNotNull(range.hashCode());
	}

	/**
	 * A simple, silly test for the {@link org.jfree.data.Range#toString()} method.
	 */
	@Test
	public void toStringReturnsAString() {
		assertSame(String.class, range.toString().getClass());
	}

	/** A test of the {@link org.jfree.data.Range#equals(Object)} method. */
	@Test
	public void equalsReturnsCorrectValues() {
		assertAll(() -> {
			assertEquals(range, expand(range, ZERO_PERCENT, ZERO_PERCENT));
		}, () -> {
			assertNotEquals(range, expand(range, ONE_HUNDRED_PERCENT, ONE_HUNDRED_PERCENT));
		}, () -> {
			assertNotEquals(range, expand(range, ONE_HUNDRED_PERCENT, ZERO_PERCENT));
		}, () -> {
			/**
			 * FIXME: the equals method should return false when the lower is the same but
			 * the upper is different.
			 */
			assertNotEquals(range, expand(range, ZERO_PERCENT, ONE_HUNDRED_PERCENT));
		});
	}

	/** Tests of the {@link org.jfree.data.Range#shift(Range, double)} method. */
	@Test
	public void shiftThrowsInvalidParameterExceptionCorrectly() {
		assertThrows(InvalidParameterException.class, () -> shift(null, ZERO_PERCENT));
	}
	
	/** Tests that the boolean parameter is respected. */
	@Test
	public void shiftWorksAsSpecified() {
		final double negativeTen = -10.0, negativeFive = -5.0, zero = 0.0, five = 5.0, ten = 10.0, twenty = 20.0;
		Range lessThanZero, containsZero, greaterThanZero;
		
		// Each of these ranges has a length of five.
		lessThanZero = new Range(negativeTen, negativeFive);
		containsZero = new Range(negativeFive, five);
		greaterThanZero = new Range(five, ten);
		
		assertAll(() -> { assertEquals(zero, shift(range, five).getUpperBound()); },
				() -> { assertEquals(zero, shift(range, ten).getLowerBound()); },
				
				// These reveal that both bounds are zero when allowZeroCrossing is false.
				() -> { assertEquals(zero, shift(lessThanZero, twenty, false).getLowerBound()); },
				() -> { assertEquals(zero, shift(lessThanZero, twenty, false).getUpperBound()); },
				
				() -> { assertEquals(zero, shift(lessThanZero, ten, true).getLowerBound()); },
				() -> { assertEquals(five, shift(lessThanZero, ten, true).getUpperBound()); },
				() -> { assertEquals(ten, shift(lessThanZero, twenty, true).getLowerBound()); },
				
				() -> { assertEquals(zero, shift(containsZero, five, false).getLowerBound()); },
				() -> { assertEquals(zero, shift(containsZero, five, true).getLowerBound()); },
				
				() -> { assertEquals(zero, shift(containsZero, ten, false).getLowerBound()); },
				() -> { assertEquals(five, shift(containsZero, ten, true).getLowerBound()); },
				
				// The boolean parameter does not affect ranges that do not cross zero.
				() -> { assertEquals(ten, shift(greaterThanZero, five, false).getLowerBound()); },
				() -> { assertEquals(ten, shift(greaterThanZero, five, true).getLowerBound()); });
	}
}
