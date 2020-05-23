package ch.bujaki.loadbalancer.scheduler;

import java.util.List;
import java.util.Random;

import ch.bujaki.loadbalancer.provider.Provider;

public class RandomScheduler<T> implements SchedulerStrategy<T> {

	private final Random random;
	
	public RandomScheduler() {
		random = new Random();
	}
	
	@Override
	public Provider<T> getNextProvider(List<Provider<T>> providers) {
		return getRandomElement(providers);
	}

	private Provider<T> getRandomElement(List<Provider<T>> elements) {
		int indexOfRandomElement = random.nextInt(elements.size());
		return elements.get(indexOfRandomElement);
	}

}
