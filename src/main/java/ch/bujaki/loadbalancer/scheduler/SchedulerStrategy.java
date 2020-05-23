package ch.bujaki.loadbalancer.scheduler;

import java.util.List;

import ch.bujaki.loadbalancer.provider.Provider;

public interface SchedulerStrategy<T> {

	Provider<T> getNextProvider(List<Provider<T>> providers);
}
