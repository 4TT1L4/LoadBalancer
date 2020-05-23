package ch.bujaki.loadbalancer;

import java.util.List;

import ch.bujaki.loadbalancer.exception.LoadBalancerException;
import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.provider.Provider;

/**
 * {@link LoadBalancer} for distributing the load between different {@link Provider} implementations.
 * 
 * @param <T>
 */
public interface LoadBalancer<T> {
	
	/**
	 * @return the next result from the next {@link Provider}, as scheduled by the {@link LoadBalancer} instance.
	 * 
	 * Note: The instance must be started before getting the first result.
	 * 
	 * @throws LoadBalancerException
	 */
	T get() throws LoadBalancerException;
	
	/**
	 * Starts the instance.
	 */
	void start();
	
	/**
	 * Stops the instance (free the used resources).
	 */
	void stop();

	/**
	 * Adds a new {@link Provider} to the {@link LoadBalancer}.
	 * 
	 * @param provider
	 *        
	 *        The provider to be added to the {@link LoadBalancer}.
	 * 
	 * @throws MaxNumberOfAcceptedProvidersHasBeenReachedException
	 *         
	 *         If the maximal number of registered providers are already registered,
	 *         then this method throws a {@link MaxNumberOfAcceptedProvidersHasBeenReachedException}.
	 */
	void registerProvider(Provider<T> provider) throws MaxNumberOfAcceptedProvidersHasBeenReachedException;

	/**
	 * Temporarily disables the {@link Provider} instance passed as parameter.
	 * 
	 * @param provider
	 * 
	 *        The {@link Provider} instance to be disabled.
	 * 
	 * @return true, if a previously active, registered {@link Provider} instance could have been disabled. 
	 *         Otherwise false.
	 */
	boolean excludeProvider(Provider<T> providerToBeExcluded);

	/**
	 * Re-activate a previously disabled {@link Provider}.
	 * 
	 * @param providerToBeIncludedAgain
	 * 
	 *         The {@link Provider} instance to be activated afain.
	 *         
	 * @return true, if a previously deactivated {@link Provider} instance could have been re-activated.
	 *         Otherwise false.
	 */
	boolean includeProvider(Provider<T> providerToBeIncludedAgain);

	/**
	 * @return the {@link List} of the currently active, previously registered {@link Provider} instances.
	 * 
	 * See also: {@link LoadBalancer#includeProvider(Provider)} and {@link LoadBalancer#includeProvider(Provider)}
	 *           for activating and deactivating the registered {@link Providers}.  
	 */
	List<Provider<T>> getActiveProviders();

	/**
	 * @return the {@link List} of all the previously registered {@link Provider} instances.
	 * 
	 * Note: The returned {@link List} includes both activated and deactivated {@link Providers}.
	 * 
	 * See also: {@link LoadBalancer#getActiveProviders(Provider)}
	 */
	List<Provider<T>>  getRegisteredProviders();
}
