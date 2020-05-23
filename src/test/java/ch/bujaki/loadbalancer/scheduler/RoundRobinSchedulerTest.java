package ch.bujaki.loadbalancer.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.bujaki.loadbalancer.provider.Provider;
import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.SequenceAssertion;

public class RoundRobinSchedulerTest {
	
	private final Provider<Integer> first = () -> 128;
	private final Provider<Integer> second = () -> 256;
	private final Provider<Integer> third = () -> 512;
	private final Provider<Integer> fourth = () -> 1024;
	private final Provider<Integer> fifth = () -> 2048;
	
	@Test
	public void test_getNextProvider() throws Exception{
		RoundRobinScheduler<Integer> scheduler = new RoundRobinScheduler<Integer>();
		List<Provider<Integer>> providers = new ArrayList<>();
		
		providers.add(first);
		providers.add(second);
		providers.add(third);

		SequenceAssertion.assertSequence(
			() -> scheduler.getNextProvider(providers),
			first, second, third, first, second, third, first, second, third
		);
	}

	@Test
	public void test_getNextProvider_removedLast() throws Exception{
		RoundRobinScheduler<Integer> scheduler = new RoundRobinScheduler<Integer>();
		List<Provider<Integer>> providers = new ArrayList<>();
		
		providers.add(first);
		providers.add(second);
		providers.add(third);
		providers.add(fourth);
		providers.add(fifth);

		SequenceAssertion.assertSequence(
			() -> scheduler.getNextProvider(providers),
			first, second, third
		);
		
		// Remove the last provider:
		providers.remove(third);

		// Scheduling starts from the beginning:
		SequenceAssertion.assertSequence(
			() -> scheduler.getNextProvider(providers),
			first, second, fourth, fifth
		);
	}

	@Test
	public void test_getNextProvider_removedOneItemButNotTheLast() throws Exception{
		RoundRobinScheduler<Integer> scheduler = new RoundRobinScheduler<Integer>();
		List<Provider<Integer>> providers = new ArrayList<>();
		
		providers.add(first);
		providers.add(second);
		providers.add(third);
		providers.add(fourth);
		providers.add(fifth);

		SequenceAssertion.assertSequence(
			() -> scheduler.getNextProvider(providers),
			first, second, third
		);
		
		// Remove one provider, but not the last scheduled:
		providers.remove(second);

		// Scheduling continues from the last scheduled provider:
		SequenceAssertion.assertSequence(
			() -> scheduler.getNextProvider(providers),
			fourth, fifth, first, third, fourth, fifth
		);
	}
}
