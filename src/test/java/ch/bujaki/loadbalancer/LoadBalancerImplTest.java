package ch.bujaki.loadbalancer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.provider.Provider;
import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.SequenceAssertion;

/**
 * Tests the {@link LoadBalancerImpl} class.
 */
public class LoadBalancerImplTest {

	private static final int MAX_ACCEPTED_PROVIDER_COUNT = 10;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void test_basicUseCaseWithRoundRobin_sequenceOfProvidersIsAsExpected() throws Exception {
    	// Create a LoadBalancerImpl with round robin scheduling
    	LoadBalancer<Integer> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	
    	// Register three providers.
    	loadBalancer.registerProvider(() -> 1);
		loadBalancer.registerProvider(() -> 2);
    	loadBalancer.registerProvider(() -> 3);

    	// The providers are scheduled as expected:
    	SequenceAssertion.assertSequence(
    		loadBalancer::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    }

    @Test
    public void test_excludeProvider_sequenceOfProvidersIsAsExpected() throws Exception {
    	LoadBalancer<Integer> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	
    	loadBalancer.registerProvider(() -> 1);
		Provider<Integer> secondProvider = () -> 2;
		loadBalancer.registerProvider(secondProvider );
    	loadBalancer.registerProvider(() -> 3);

    	// The providers are scheduled as expected:
    	SequenceAssertion.assertSequence(
    		loadBalancer::get,
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    	
    	loadBalancer.excludeProvider(secondProvider);

    	// The second provider should be skipped, since it is excluded:
    	SequenceAssertion.assertSequence(
    		loadBalancer::get,
    		1, 3, 1, 3, 1, 3, 1, 3, 1, 3
    	);

    	loadBalancer.includeProvider(secondProvider);

    	// The second provider should not be skipped anymore, since it is not excluded anymore:
    	SequenceAssertion.assertSequence(
    		loadBalancer::get, 
    		1, 2, 3, 1, 2, 3, 1, 2, 3
    	);
    }
    
    @Test
    public void test_registerProvider_tooManyProvidersRegistered_leadsToException() throws Exception {

    	expectedException.expect(MaxNumberOfAcceptedProvidersHasBeenReachedException.class);
    	expectedException.expectMessage(
    			String.format(
    					"Could not add Provider#%d. There are already %d providers registered",
    					MAX_ACCEPTED_PROVIDER_COUNT + 1,
    					MAX_ACCEPTED_PROVIDER_COUNT
    					)
    			);
    	
    	
    	LoadBalancer<String> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	
    	for(int i = 0; i < MAX_ACCEPTED_PROVIDER_COUNT + 1; i++) {
    		final int id = i + 1; 
        	loadBalancer.registerProvider(new Provider<String>() {
				
				@Override
				public String get() {
					return "Provided Value";
				}
				
				@Override
				public String toString() {
					return "Provider#" + id;
				}
			});
    	}
    }

    @Test
    public void test_registerProvider_maxNumberOfAcceptedProvidersRegistered_leadsToNoException() throws Exception {
    	LoadBalancer<String> loadBalancer = new LoadBalancerImpl<>(new RoundRobinScheduler<>(), MAX_ACCEPTED_PROVIDER_COUNT);
    	
    	for(int i = 0; i < MAX_ACCEPTED_PROVIDER_COUNT; i++) {
        	loadBalancer.registerProvider(() -> "Some value");
    	}
    }
}
