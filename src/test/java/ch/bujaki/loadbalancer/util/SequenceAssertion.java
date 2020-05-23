package ch.bujaki.loadbalancer.util;

import static org.junit.Assert.assertEquals;

/**
 * Helper class for assertions about response sequences.
 */
public class SequenceAssertion {
	
	@FunctionalInterface
	public interface SupplierWithException<T> {
	    T get() throws Exception;
	}
	
	@SafeVarargs
	public static <T> void assertSequence(SupplierWithException<T> supplier, T... expectedItems) throws Exception {
		for(T expectedItem : expectedItems) {
			T actualItem = supplier.get();
			assertEquals(expectedItem, actualItem);
		}
	}
}
