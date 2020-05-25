package ch.bujaki.loadbalancer.integrationtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import ch.bujaki.loadbalancer.LoadBalancer;
import ch.bujaki.loadbalancer.LoadBalancerFactory;
import ch.bujaki.loadbalancer.exception.LoadBalancerException;
import ch.bujaki.loadbalancer.exception.MaxNumberOfAcceptedProvidersHasBeenReachedException;
import ch.bujaki.loadbalancer.exception.NoRegisteredProviderIsActiveException;
import ch.bujaki.loadbalancer.exception.TooManyConcurrentCallsException;
import ch.bujaki.loadbalancer.scheduler.RoundRobinScheduler;
import ch.bujaki.loadbalancer.util.SlowProvider;

/**
 * A simple console application for interactive testing of the {@link LoadBalancer}.
 */
public class LoadBalancerInteractiveTest {
	
	int addedProviderCount = 0;
	List<SlowProvider> providers = new ArrayList<>();
	private LoadBalancer<String> loadBalancer;

	public static void main(String[] args) {
		new LoadBalancerInteractiveTest().start();
	}
	
	private void start() {
		loadBalancer = LoadBalancerFactory.<String>builder()
				.healthCheckInterval(5)
				.healthCheckIntervalTimeUnit(TimeUnit.SECONDS)
				.maxAcceptedProviders(10)
				.maxNumberOfConcurrentCallsPerProvider(2)
				.scheduling(new RoundRobinScheduler<>())
				.build();

		loadBalancer.start();

		printHelp();
		
		startInputProcessingLoop();
		
		loadBalancer.stop();
		
		System.out.println(" ---< TERMINATED >--- ");
	}

	private void startInputProcessingLoop() {
		char c = ' ';
		try (Scanner in = new Scanner(System.in)) {
			while (c != 'q') {
				c = in.next().charAt(0);

				if (Character.isDigit(c)) {
					toggleHealth(c);
				}
				else {
					switch (c) {
					case 'g':
						getNextResult();
						break;
					case 'r':
						registerNewProvider();
						break;
					}
				}
			}	
		}
	}

	private void registerNewProvider() {
		String name = "Provider-" + (addedProviderCount);
		SlowProvider provider = new SlowProvider(name, 10000);
		System.out.println("Registering provider '" + name + "' to the load balancer...");
		try {
			loadBalancer.registerProvider(provider);
			providers.add(provider);
			addedProviderCount++;
			System.out.println("Succesfully registered " + name + ".");
		} catch (MaxNumberOfAcceptedProvidersHasBeenReachedException e) {
			System.out.println("Maximal number of accepted providers has been reached.");
		}
	}

	private void getNextResult() {
		System.out.println("Getting the next result from the load balancer... (runs in background)");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String result = loadBalancer.get();
					System.out.println("Result: " + result);
				} catch (NoRegisteredProviderIsActiveException e) {
					System.out.println("No active registered provider!");
				} catch (TooManyConcurrentCallsException e) {
					System.out.println("Too many concurrent calls!");
				} catch (LoadBalancerException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void toggleHealth(char c) {
		int index = (c - '0');
		
		if(providers.size()<index) {
			System.out.println("No provider with index " + index + ".");
			return;
		}
		
		boolean health = providers.get(index).check();
		providers.get(index).setHealth(!health);
		System.out.println("Set Provider-" + index + " to:" + (health ? "UNHEALTHY" : "HEALTHY"));
	}

	private void printHelp() {
		System.out.println(" ---< INTERACTIVE LOAD BALANCER TESTER >--- ");
		System.out.println("                                            ");
		System.out.println(" Controls:                                  ");
		System.out.println("  r:   Registers a new provider             ");
		System.out.println("  g:   Sends a request to the load balancer ");
		System.out.println("  0-9: Toggles the health of the provider   ");
		System.out.println("  q:   Terminates the execution             ");
		System.out.println("                                            ");
		System.out.println(" Enter only a single character per line!    ");
		System.out.println("                                            ");
		System.out.println(" ------------------------------------------ ");
	}
}
