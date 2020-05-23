package ch.bujaki.loadbalancer.provider;

import java.util.function.Supplier;

/**
 * A simple interface providing access to 
 * 
 * Similar to {@link Supplier} interface, but also defines the a method for health checks.
 */
@FunctionalInterface
public interface Provider<T> {
	
	/*
	 * Get the next result from the Provider.
	 */
	T get();
	
	/**
	 * @return true, if the {@link Provider} is healthy, otherwise false.
	 */
	default boolean check() {
		return true;
	}
}
