package ch.bujaki.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.exception.NoRegisteredProviderIsActiveException;
import ch.bujaki.loadbalancer.provider.Provider;
import ch.bujaki.loadbalancer.scheduler.SchedulerStrategy;

/**
 * The core implementation of the {@link LoadBalancer}.
 * 
 * Implements the basic scheduling logic and the management of the registered {@link Provider} instances.
 */
public class LoadBalancerImpl <T> implements LoadBalancer<T> {
	
	private final List<Provider<T>> registeredProviders;
	private final Set<Provider<T>> excludedProviders;
	private final int maxNumberOfAcceptedProviders;
	
	private final SchedulerStrategy<T> strategy;
	
	/**
	 * Constructor.
	 */
	LoadBalancerImpl(SchedulerStrategy<T> strategy, int maxNumberOfAcceptedProviders) {
		this.registeredProviders = new CopyOnWriteArrayList<>();
		this.excludedProviders = new CopyOnWriteArraySet<>();
		this.strategy = strategy;
		this.maxNumberOfAcceptedProviders = maxNumberOfAcceptedProviders;
	}
	
	@Override
	public T get() throws NoRegisteredProviderIsActiveException {		
		Provider<T> next = getNext();
		return next.get();
	}

	private synchronized Provider<T> getNext() throws NoRegisteredProviderIsActiveException {
		List<Provider<T>> activeProviders = getActiveProviders();
		
		if(activeProviders.isEmpty()) {
			throw new NoRegisteredProviderIsActiveException();
		}
		
		return this.strategy.getNextProvider(activeProviders);
	}
	 
	public synchronized void registerProvider(Provider<T> provider) throws MaxNumberOfAcceptedProvidersHasBeenReachedException {
		checkNumberOfRegisteredProviders(provider);
		
		registeredProviders.add(provider);
	}

	private void checkNumberOfRegisteredProviders(Provider<T> provider) throws MaxNumberOfAcceptedProvidersHasBeenReachedException {
		if (registeredProviders.size() >= maxNumberOfAcceptedProviders) {
			throw new MaxNumberOfAcceptedProvidersHasBeenReachedException(
					String.format(
							"Could not add %s. There are already %d providers registered",
							provider, registeredProviders.size())
					);
		}
	}
	
	public synchronized boolean excludeProvider(Provider<T> provider) {
		return this.excludedProviders.add(provider);
	}
	
	public synchronized boolean includeProvider(Provider<T> providerToBeIncludedAgain){
		return this.excludedProviders.remove(providerToBeIncludedAgain);
	}

	public synchronized List<Provider<T>> getActiveProviders() {
		List<Provider<T>> activeProviders = new ArrayList<>(registeredProviders);
		activeProviders.removeAll(excludedProviders);
		
		return activeProviders;
	}

	public synchronized List<Provider<T>> getRegisteredProviders() {
		return new ArrayList<>(registeredProviders);
	}

	@Override
	public void start() {
		// Nothing to do.
	}

	@Override
	public void stop() {
		// Nothing to do.
	}
}
