package ch.bujaki.loadbalancer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bujaki.loadbalancer.exception.LoadBalancerException;
import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.exception.NoRegisteredProviderIsActiveException;
import ch.bujaki.loadbalancer.exception.TooManyConcurrentCallsException;
import ch.bujaki.loadbalancer.provider.Provider;

public class LoadBalancerClusterCapacityLimitDecorator <T> implements LoadBalancer<T> {
	
    private static final Logger logger = LogManager.getLogger(LoadBalancerClusterCapacityLimitDecorator.class);
    
	private final LoadBalancer<T> loadBalancer;
	private final int maxNumberOfConcurrentCallsPerActiveProvider;
	
	private volatile int concurrentCalls;

	
	LoadBalancerClusterCapacityLimitDecorator(LoadBalancer<T> loadBalancer, int maxNumberOfConcurrentCallsPerActiveProvider) {
		this.loadBalancer = loadBalancer;
		this.maxNumberOfConcurrentCallsPerActiveProvider = maxNumberOfConcurrentCallsPerActiveProvider;
		this.concurrentCalls = 0;
	}
	
	@Override
	public T get() throws LoadBalancerException {
		if(loadBalancer.getActiveProviders().isEmpty()) {
			throw new NoRegisteredProviderIsActiveException();
		}
		
		incrementConcurrentCallCount();
		
		try {
			logger.debug("get - call delegate - concurrentCalls: " + concurrentCalls);
			return loadBalancer.get();	
		}
		finally {
			decrementConcurrentCallCount();
		}
	}

	@Override
	public void registerProvider(Provider<T> provider) throws MaxNumberOfAcceptedProvidersHasBeenReachedException {
		loadBalancer.registerProvider(provider);
	}

	@Override
	public boolean  excludeProvider(Provider<T> provider) {
		return loadBalancer.excludeProvider(provider);
	}

	@Override
	public boolean includeProvider(Provider<T> providerToBeIncludedAgain){
		return loadBalancer.includeProvider(providerToBeIncludedAgain);
	}

	@Override
	public List<Provider<T>> getActiveProviders() {
		return this.loadBalancer.getActiveProviders();
	}

	private synchronized void decrementConcurrentCallCount() {
		logger.debug("decrementConcurrentCallCount - concurrentCalls was {0}", concurrentCalls);
		concurrentCalls--;
	}

	private synchronized void incrementConcurrentCallCount() throws TooManyConcurrentCallsException {
		int maxConcurrentCallCount = loadBalancer.getActiveProviders().size() * maxNumberOfConcurrentCallsPerActiveProvider;
		logger.debug("incrementConcurrentCallCount - concurrentCalls was: {0} maxConcurrentCallCount: {1}" +concurrentCalls,  maxConcurrentCallCount );
		if (concurrentCalls + 1 > maxConcurrentCallCount) {
			throw new TooManyConcurrentCallsException("There are already " + concurrentCalls + " concurrent calls and only " + maxConcurrentCallCount + " are allowed.");
		}
		
		concurrentCalls++;
	}

	@Override
	public List<Provider<T>> getRegisteredProviders() {
		return loadBalancer.getRegisteredProviders();
	}

	@Override
	public void start() {
		loadBalancer.start();
	}

	@Override
	public void stop() {
		loadBalancer.stop();
	}
}
