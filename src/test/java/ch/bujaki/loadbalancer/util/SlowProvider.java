package ch.bujaki.loadbalancer.util;

import ch.bujaki.loadbalancer.provider.Provider;

public class SlowProvider implements Provider<Integer> {
	
	private String name;

	public SlowProvider(String name) {
		this.name = name;
	}
	
	@Override
	public Integer get() {
		return fakeExpensiveCalculation();
	}

	private int fakeExpensiveCalculation() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return 42;
	}

	@Override
	public String toString() {
		return "SlowProvider [name=" + name + "]";
	}
}