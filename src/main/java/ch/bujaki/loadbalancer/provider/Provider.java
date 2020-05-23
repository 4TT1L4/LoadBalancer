package ch.bujaki.loadbalancer.provider;

/**
 *
 */
@FunctionalInterface
public interface Provider<T> {
	T get();
	
	default boolean check() {
		return true;
	};
}
