package ch.bujaki.loadbalancer.util;

import ch.bujaki.loadbalancer.provider.Provider;

public class FailingProvider implements Provider<Integer> {
	
	private volatile boolean healthy = false;
	
	private final int value;
	
	public FailingProvider(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "FailingProvider [healthy=" + healthy + ", value=" + value +"]";
	}

	@Override
	public Integer get() {
		return value;
	}

	@Override
	public boolean check() {
		return healthy;
	}
	
	public void setHealthy(boolean healthy) {
		this.healthy = healthy;
	}
}