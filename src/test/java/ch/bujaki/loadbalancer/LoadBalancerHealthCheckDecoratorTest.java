package ch.bujaki.loadbalancer;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.FailingProvider;
import ch.bujaki.loadbalancer.util.SequenceAssertion;

/**
 * Tests the {@link LoadBalancerHealthCheckDecorator} class.
 */
public class LoadBalancerHealthCheckDecoratorTest {

	private static final int MAX_ACCEPTED_PROVIDER_COUNT = 10;
    
    @Test
    public void test_checkProviders() throws Exception {
    	// Create a LoadBalancerImpl with round robin scheduling
    	LoadBalancer<Integer> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	LoadBalancerHealthCheckDecorator<Integer> decorated = new LoadBalancerHealthCheckDecorator<>(loadBalancer, 2, TimeUnit.SECONDS);
    	
    	// Register three providers.
    	decorated.registerProvider(() -> 1);
    	FailingProvider failingTwoProvider = new FailingProvider(2);
    	decorated.registerProvider(failingTwoProvider);
    	decorated.registerProvider(() -> 3);
    	
    	assertEquals(3, decorated.getActiveProviders().size());
    	
    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    	
    	decorated.checkProviders();
    	// -> failingTwoProvider should be excluded.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);

    	decorated.checkProviders();
    	// -> failingTwoProvider should be still excluded.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);

    	failingTwoProvider.setHealthy(true);
    	decorated.checkProviders();
    	// -> failingTwoProvider was once healthy.
    	
    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);

    	decorated.checkProviders();
    	// -> failingTwoProvider was healthy twice -> should be included again.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    }

    // FIXME: Find a solution for "slow" unit tests.
    @Test
    public void test_checkProviders_usingTimer() throws Exception {
    	// Create a LoadBalancerImpl with round robin scheduling
    	LoadBalancer<Integer> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	LoadBalancerHealthCheckDecorator<Integer> decorated = new LoadBalancerHealthCheckDecorator<>(loadBalancer, 100, TimeUnit.MILLISECONDS);
    	
    	// Register three providers.
    	decorated.registerProvider(() -> 1);
    	FailingProvider failingTwoProvider = new FailingProvider(2);
    	decorated.registerProvider(failingTwoProvider);
    	decorated.registerProvider(() -> 3);
    	
    	assertEquals(3, decorated.getActiveProviders().size());
    	
    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);

    	decorated.start();
    	Thread.sleep(125);
    	// -> failingTwoProvider should be excluded.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);
    	
    	Thread.sleep(110);
    	// -> failingTwoProvider should be still excluded.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);

    	failingTwoProvider.setHealthy(true);
    	Thread.sleep(110);
    	// -> failingTwoProvider was once healthy.
    	
    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 3, 1, 3, 1, 3
    	);

    	Thread.sleep(110);
    	// -> failingTwoProvider was healthy twice -> should be included again.

    	SequenceAssertion.assertSequence(
    		decorated::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    	
    	decorated.stop();
    }
}
