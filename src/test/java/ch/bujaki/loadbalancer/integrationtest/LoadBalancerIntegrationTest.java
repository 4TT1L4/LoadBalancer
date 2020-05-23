package ch.bujaki.loadbalancer.integrationtest;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.bujaki.loadbalancer.LoadBalancer;
import ch.bujaki.loadbalancer.LoadBalancerFactory;
import ch.bujaki.loadbalancer.exception.NoRegisteredProviderIsActiveException;
import ch.bujaki.loadbalancer.exception.TooManyConcurrentCallsException;
import ch.bujaki.loadbalancer.provider.IdentifierProvider;
import ch.bujaki.loadbalancer.provider.Provider;
import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.FailingProvider;
import ch.bujaki.loadbalancer.util.SequenceAssertion;
import ch.bujaki.loadbalancer.util.SlowProvider;
import io.reactivex.rxjava3.core.Observable;

/**
 * Integration tests for the {@link LoadBalancer}.
 */
public class LoadBalancerIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Provider<String> first = new IdentifierProvider();
    Provider<String> second = new IdentifierProvider();
    Provider<String> third = new IdentifierProvider();
    
    @Test
    public void test_simpleCase_threeProviders() throws Exception {
    	LoadBalancer<String> loadBalancer = LoadBalancerFactory.<String>builder()
    		.healthCheckInterval(50)
    		.healthCheckIntervalTimeUnit(TimeUnit.MICROSECONDS)
    		.maxAcceptedProviders(3)
    		.maxNumberOfConcurrentCallsPerProvider(2)
    		.scheduling(new RoundRobinScheduler<>())
    		.build();

    	loadBalancer.registerProvider(first);
    	loadBalancer.registerProvider(second);
    	loadBalancer.registerProvider(third);
    	
    	SequenceAssertion.assertSequence(
    		loadBalancer::get, 
    		first.get(), second.get(), third.get(),
    		first.get(), second.get(), third.get(),
    		first.get(), second.get(), third.get()
    	);
    }

    @Test(expected = NoRegisteredProviderIsActiveException.class)
    public void test_noProvidersRegistered_exceptionShouldBeThrown() throws Exception {
    	// Given:
    	LoadBalancer<String> loadBalancer = LoadBalancerFactory.<String>builder()
    		.healthCheckInterval(50)
    		.healthCheckIntervalTimeUnit(TimeUnit.MICROSECONDS)
    		.maxAcceptedProviders(3)
    		.maxNumberOfConcurrentCallsPerProvider(2)
    		.scheduling(new RoundRobinScheduler<>())
    		.build();

    	// When: No providers are registered.
    	
    	// Then: An exception is thrown:
    	loadBalancer.get();
    }

    @Test(expected = TooManyConcurrentCallsException.class)
    public void test_tooManyConcurrentCalls_exceptionIsBeingThrown() throws Exception {
    	LoadBalancer<Integer> loadBalancer = LoadBalancerFactory.<Integer>builder()
    		.healthCheckInterval(1000)
    		.healthCheckIntervalTimeUnit(TimeUnit.MICROSECONDS)
    		.maxAcceptedProviders(3)
    		.maxNumberOfConcurrentCallsPerProvider(1)
    		.scheduling(new RoundRobinScheduler<>())
    		.build();

    	loadBalancer.registerProvider(new SlowProvider("first"));

    	Observable.timer(5, TimeUnit.MILLISECONDS).subscribe(x -> loadBalancer.get());
    	
    	Thread.sleep(50);
    	
    	loadBalancer.get();
    }

    @Test
    public void test_twoConcurrentCalls_noExceptionIsThrownIfThisIsAllowed() throws Exception {
    	LoadBalancer<Integer> loadBalancer = LoadBalancerFactory.<Integer>builder()
    		.healthCheckInterval(1000)
    		.healthCheckIntervalTimeUnit(TimeUnit.MICROSECONDS)
    		.maxAcceptedProviders(3)
    		.maxNumberOfConcurrentCallsPerProvider(2)
    		.scheduling(new RoundRobinScheduler<>())
    		.build();

    	loadBalancer.registerProvider(new SlowProvider("first"));

    	Observable.timer(5, TimeUnit.MILLISECONDS).subscribe(x -> loadBalancer.get());
    	
    	Thread.sleep(50);
    	
    	loadBalancer.get();
    }

    @Test
    public void test_healthCheck_failingProviderWillBeExcluded() throws Exception {
    	LoadBalancer<Integer> loadBalancer = LoadBalancerFactory.<Integer>builder()
    		.healthCheckInterval(5)
    		.healthCheckIntervalTimeUnit(TimeUnit.MILLISECONDS)
    		.maxAcceptedProviders(3)
    		.maxNumberOfConcurrentCallsPerProvider(2)
    		.scheduling(new RoundRobinScheduler<>())
    		.build();
    	FailingProvider failingTwoProvider = new FailingProvider(2);

    	loadBalancer.registerProvider(() -> 1);
    	loadBalancer.registerProvider(failingTwoProvider);
    	
    	loadBalancer.start();
    	Thread.sleep(50);
    	
    	SequenceAssertion.assertSequence(loadBalancer::get,
    			1, 1, 1 ,1);
    	
    	failingTwoProvider.setHealthy(true);

    	Thread.sleep(50);
    	
    	SequenceAssertion.assertSequence(loadBalancer::get,
    			2, 1, 2 ,1, 2 ,1);
    	
    	loadBalancer.stop();
    }
}
