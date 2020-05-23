package ch.bujaki.loadbalancer;

import java.util.concurrent.TimeUnit;

import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.scheduler.SchedulerStrategy;

/**
 * Factory class for configuring and creating {@link LoadBalancer} instances.
 */
public class LoadBalancerFactory {
	
	/**
	 * Private constructor.
	 */
	private LoadBalancerFactory() {
		// Hides the implicit public constructor.
		// -> Nothing to do.
	}
	
	/**
	 * {@link LoadBalancer} builder.
	 */
	public static class LoadBalancerBuilder<T> {
		
		private int healthCheckInterval = 2;
		private TimeUnit healthCheckIntervalTimeUnit = TimeUnit.SECONDS;	
		private int maxAcceptedProviders = 10;
		private SchedulerStrategy<T> strategy = new RoundRobinScheduler<>();

		private int maxNumberOfConcurrentCallsPerActiveProvider = 20;				

		public LoadBalancerBuilder<T> healthCheckInterval(int healthCheckInterval) {
			this.healthCheckInterval = healthCheckInterval;
			return this;
		}

		public LoadBalancerBuilder<T> healthCheckIntervalTimeUnit(TimeUnit healthCheckIntervalTimeUnit) {
			this.healthCheckIntervalTimeUnit = healthCheckIntervalTimeUnit;
			return this;
		}
		
		public LoadBalancerBuilder<T> scheduling(SchedulerStrategy<T> strategy) {
			this.strategy = strategy;
			return this;
		}

		public LoadBalancerBuilder<T> maxAcceptedProviders(int maxAcceptedProviders) {
			this.maxAcceptedProviders = maxAcceptedProviders;
			return this;
		}
		
		public LoadBalancerBuilder<T> maxNumberOfConcurrentCallsPerProvider(int maxNumberOfConcurrentCallsPerActiveProvider) {
			this.maxNumberOfConcurrentCallsPerActiveProvider = maxNumberOfConcurrentCallsPerActiveProvider;
			return this;
		}
		
		public LoadBalancer<T> build() {
			LoadBalancer<T> balancer = new LoadBalancerImpl<>(this.strategy, maxAcceptedProviders);
			LoadBalancer<T> decoratedWithCapacityLimit = new LoadBalancerClusterCapacityLimitDecorator<>(balancer, maxNumberOfConcurrentCallsPerActiveProvider);
			return new LoadBalancerHealthCheckDecorator<>(decoratedWithCapacityLimit, healthCheckInterval, healthCheckIntervalTimeUnit);
		}
	}

	public static <T> LoadBalancerBuilder<T> builder() {
		return new LoadBalancerBuilder<>();
	}
}
