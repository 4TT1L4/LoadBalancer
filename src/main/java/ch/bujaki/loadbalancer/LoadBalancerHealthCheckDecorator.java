package ch.bujaki.loadbalancer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bujaki.loadbalancer.exception.LoadBalancerException;
import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.provider.Provider;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoadBalancerHealthCheckDecorator <T> implements LoadBalancer<T> {

    private static final Logger logger = LogManager.getLogger(LoadBalancerHealthCheckDecorator.class);
    
	private final LoadBalancer<T> loadBalancer;
	private final int interval;
	private final TimeUnit intervalUnit;
	private final Set<Provider<T>> onceHealthy;
	
	private @NonNull Disposable healthCheck;
	
	LoadBalancerHealthCheckDecorator(LoadBalancer<T> loadBalancer, int interval, TimeUnit unit) {
		this.loadBalancer = loadBalancer;
		this.interval = interval;
		this.intervalUnit = unit;
		this.onceHealthy = new CopyOnWriteArraySet<>();
	}

	@Override
	public void start() {
		logger.debug("start");
		healthCheck = Observable.interval(interval, intervalUnit)
			.subscribe( __ -> checkProviders() );
		
		loadBalancer.start();
	}

	@Override
	public void stop() {
		logger.debug("stop");
		if (healthCheck != null) {
			healthCheck.dispose();
		}

		loadBalancer.stop();
	}
	
	@Override
	public T get() throws LoadBalancerException {
		return loadBalancer.get();
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

	synchronized void checkProviders() {
		logger.debug("checkProviders");
		
		List<Provider<T>> activeProviders = loadBalancer.getActiveProviders();
		
		for (Provider<T> provider : loadBalancer.getRegisteredProviders()) {
			if (provider.check()) {
				if (!activeProviders.contains(provider)) {
					if (onceHealthy.contains(provider)) {
						logger.info("checkProviders - {} has recovered.", provider);
						loadBalancer.includeProvider(provider);
						onceHealthy.remove(provider);
					} 
					else {
						logger.info("checkProviders - {} was once healthy.", provider);
						onceHealthy.add(provider);
					}
				}
			}
			else {
				logger.info("checkProviders - {} is failing.", provider);
				onceHealthy.remove(provider);
				loadBalancer.excludeProvider(provider);
			}
		}
	}

	@Override
	public List<Provider<T>> getActiveProviders() {
		return loadBalancer.getActiveProviders();
	}

	@Override
	public List<Provider<T>> getRegisteredProviders() {
		return loadBalancer.getRegisteredProviders();
	}
}
