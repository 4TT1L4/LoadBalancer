package ch.bujaki.loadbalancer.exception;

public class TooManyConcurrentCallsException extends LoadBalancerException {

	private static final long serialVersionUID = 1L;

	public TooManyConcurrentCallsException(String message) {
		super(message);
	}
}
