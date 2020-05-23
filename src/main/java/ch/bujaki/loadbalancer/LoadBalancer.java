package ch.bujaki.loadbalancer;

import java.util.List;

import ch.bujaki.loadbalancer.exception.LoadBalancerException;
import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.provider.Provider;

public interface LoadBalancer<T> {
	T get() throws LoadBalancerException;
	
	void start();
	
	void stop();

	void registerProvider(Provider<T> provider) throws MaxNumberOfAcceptedProvidersHasBeenReachedException;

	boolean excludeProvider(Provider<T> provider);

	boolean includeProvider(Provider<T> providerToBeIncludedAgain);

	List<Provider<T>> getActiveProviders();

	List<Provider<T>>  getRegisteredProviders();
}
