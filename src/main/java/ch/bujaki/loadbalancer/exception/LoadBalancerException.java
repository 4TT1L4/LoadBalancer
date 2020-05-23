package ch.bujaki.loadbalancer.exception;

abstract public class LoadBalancerException extends Exception {

	private static final long serialVersionUID = 1L;

	LoadBalancerException() {
		super();
	}
	
	LoadBalancerException(String message) {
		super(message);
	}
}
