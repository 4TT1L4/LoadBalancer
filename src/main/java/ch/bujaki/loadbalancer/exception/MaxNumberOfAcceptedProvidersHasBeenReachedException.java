package ch.bujaki.loadbalancer.exception;

public class MaxNumberOfAcceptedProvidersHasBeenReachedException extends Exception {

	private static final long serialVersionUID = 1L;

	public MaxNumberOfAcceptedProvidersHasBeenReachedException(String message) {
		super(message);
	}
}
