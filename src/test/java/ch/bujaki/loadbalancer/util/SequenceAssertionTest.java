package ch.bujaki.loadbalancer.util;

import org.junit.Test;

import ch.bujaki.loadbalancer.util.SequenceAssertion.SupplierWithException;

public class SequenceAssertionTest {

	private final class SequenceSupplier implements SupplierWithException<Integer> {
		private int callNumber = 0;

		@Override
		public Integer get() throws Exception { 
			return ++callNumber;
		}
	}

	@Test
	public void test_assertSequence_valid() throws Exception {
		SequenceAssertion.assertSequence(() -> 1, 1);
	}

	@Test(expected = AssertionError.class)
	public void test_assertSequence_notValid() throws Exception {
		SequenceAssertion.assertSequence(() -> 1, 2);
	}

	@Test
	public void test_assertSequence_longSequenceValid() throws Exception {
		SupplierWithException<Integer> supplier = new SequenceSupplier();
		SequenceAssertion.assertSequence(
			supplier, 
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10
		);
	}

	@Test(expected = AssertionError.class)
	public void test_assertSequence_longSequenceNotValid() throws Exception {
		SupplierWithException<Integer> supplier = new SequenceSupplier();
		SequenceAssertion.assertSequence(
			supplier, 
			1, 2, 3, 4, 99999999, 6, 7, 8, 9, 10
		);
	}
}
