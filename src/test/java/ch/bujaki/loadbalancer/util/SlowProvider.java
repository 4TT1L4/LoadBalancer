package ch.bujaki.loadbalancer.util;

import ch.bujaki.loadbalancer.provider.Provider;

public class SlowProvider implements Provider<String> {
	
	private final String name;
	private final int calculationLength;
	private volatile boolean health = true;

	public SlowProvider(String name) {
		this.name = name;
		this.calculationLength = 50;
	}
	
	public SlowProvider(String name, int calculationLength) {
		this.name = name;
		this.calculationLength = calculationLength;
	}
	
	@Override
	public String get() {
		return fakeExpensiveCalculation();
	}
	
	@Override
	public boolean check() {
		return health;
	}

	private String fakeExpensiveCalculation() {
		try {
			Thread.sleep(calculationLength);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return name;
	}

	@Override
	public String toString() {
		return "SlowProvider [name=" + name + "]";
	}

	public void setHealth(boolean b) {
		health = b;
	}
}