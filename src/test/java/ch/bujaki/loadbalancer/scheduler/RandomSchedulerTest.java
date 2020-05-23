package ch.bujaki.loadbalancer.scheduler;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.bujaki.loadbalancer.provider.Provider;
import ch.bujaki.loadbalancer.scheduler.RandomScheduler;

public class RandomSchedulerTest {

	@Test
	public void test_getNextProvider(){
		RandomScheduler<Integer> scheduler = new RandomScheduler<Integer>();
		List<Provider<Integer>> providers = new ArrayList<>();
		
		providers.add(() -> 128);
		providers.add(() -> 256);
		providers.add(() -> 512);
		
		Provider<Integer> result;
		
		result = scheduler.getNextProvider(providers);
		assertTrue(providers.contains(result));

		result = scheduler.getNextProvider(providers);
		assertTrue(providers.contains(result));

		result = scheduler.getNextProvider(providers);
		assertTrue(providers.contains(result));
	}
}
