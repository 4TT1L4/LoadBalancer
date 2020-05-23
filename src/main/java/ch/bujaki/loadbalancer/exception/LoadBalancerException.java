package ch.bujaki.loadbalancer.exception;

public abstract class LoadBalancerException extends Exception {

	private static final long serialVersionUID = 1L;

	LoadBalancerException() {
		super();
	}
	
	LoadBalancerException(String message) {
		super(message);
	}
}
