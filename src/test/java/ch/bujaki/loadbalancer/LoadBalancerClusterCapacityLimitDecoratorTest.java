package ch.bujaki.loadbalancer;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.bujaki.loadbalancer.exception.TooManyConcurrentCallsException;
import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.SlowProvider;
import io.reactivex.rxjava3.core.Observable;

/**
 * Tests the {@link LoadBalancerClusterCapacityLimitDecoratorTest} class.
 */
public class LoadBalancerClusterCapacityLimitDecoratorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void test_maximumNumberOfConcurrentCallsIsStillAllowed() throws Exception {
    	// Create a LoadBalancerImpl with round robin scheduling
    	LoadBalancer<String> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), 3);
    	LoadBalancerClusterCapacityLimitDecorator<String> decorated = new LoadBalancerClusterCapacityLimitDecorator<>(loadBalancer, 1);

    	decorated.registerProvider(new SlowProvider("first"));
    	decorated.registerProvider(new SlowProvider("second"));
    	decorated.registerProvider(new SlowProvider("third"));

    	Observable.timer(5, TimeUnit.MILLISECONDS).subscribe(x -> decorated.get());
    	Observable.timer(5, TimeUnit.MILLISECONDS).subscribe(x -> decorated.get());
    	
    	// Third concurrent call should be still allowed:
    	decorated.get();
    }

    // FIXME: Find a solution for "slow" unit tests.
	@Test
    public void test_moreCallsThanThemaximumNumberOfConcurrentCallsIsNotAllowed() throws Exception {
    	expectedException.expect(TooManyConcurrentCallsException.class);
    	expectedException.expectMessage(
    			String.format("There are already 1 concurrent calls and only 1 are allowed.")
    			);
    	
    	// Create a LoadBalancerImpl with round robin scheduling
    	LoadBalancer<String> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), 3);
    	LoadBalancerClusterCapacityLimitDecorator<String> decorated = new LoadBalancerClusterCapacityLimitDecorator<>(loadBalancer, 1);

    	decorated.registerProvider(new SlowProvider("first"));
    	
    	Observable.timer(5, TimeUnit.MILLISECONDS).subscribe(x -> decorated.get());	
    	
    	Thread.sleep(50);
    	
    	// Exception:
    	decorated.get();
    }
}
