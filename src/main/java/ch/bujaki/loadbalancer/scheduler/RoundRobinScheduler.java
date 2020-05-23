package ch.bujaki.loadbalancer.scheduler;

import java.util.List;

import ch.bujaki.loadbalancer.provider.Provider;

/**
 * Round-robin {@link SchedulerStrategy}, implementing sequential scheduling.
 */
public class RoundRobinScheduler<T> implements SchedulerStrategy<T> {

	private volatile Provider<T> last;
	
	public RoundRobinScheduler() {
		last = null;
	}
	
	@Override
	public Provider<T> getNextProvider(List<Provider<T>> providers) {
		Provider<T> next = getNext(providers);
		last = next;
		return next;
	}
 
	private Provider<T> getNext(List<Provider<T>> elements) {
		int indexOfLastElement = elements.indexOf(last);
		int indexOfNext = (indexOfLastElement + 1) % elements.size();
		
		return elements.get(indexOfNext);
	}

}
