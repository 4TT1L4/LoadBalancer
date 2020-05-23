package ch.bujaki.loadbalancer.scheduler;

import java.util.List;

import ch.bujaki.loadbalancer.LoadBalancer;
import ch.bujaki.loadbalancer.provider.Provider;

/**
 * {@link SchedulerStrategy} to be used by the {@link LoadBalancer}.
 * 
 * Implements a scheduling algorithm, that can be plugged in to the {@link LoadBalancer}. 
 */
public interface SchedulerStrategy<T> {

	/**
	 * @param providers
	 *         
	 *         The {@link List} of available {@link Provider} instances.
	 *         
	 * @return the next {@link Provider} instance according to the scheduling algorithm represented by the instance.
	 */
	Provider<T> getNextProvider(List<Provider<T>> providers);
}
