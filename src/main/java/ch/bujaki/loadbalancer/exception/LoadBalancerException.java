package ch.bujaki.loadbalancer.exception;

public class LoadBalancerException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoadBalancerException() {
		super();
	}
	
	public LoadBalancerException(String message) {
		super(message);
	}
}
