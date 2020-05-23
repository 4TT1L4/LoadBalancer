package ch.bujaki.loadbalancer;

import java.util.concurrent.TimeUnit;

import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.scheduler.SchedulerStrategy;

public class LoadBalancerFactory {
	
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
			LoadBalancer<T> decoratedWithCapacityLimit = new LoadBalancerClusterCapacityLimitDecorator<T>(balancer, maxNumberOfConcurrentCallsPerActiveProvider);
			return new LoadBalancerHealthCheckDecorator<T>(decoratedWithCapacityLimit, healthCheckInterval, healthCheckIntervalTimeUnit);
		}
	}

	public static <T> LoadBalancerBuilder<T> builder() {
		return new LoadBalancerBuilder<T>();
	}
}
