package ch.bujaki.loadbalancer.provider;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link Provider} implementation supplying random {@link UUID}s.
 */
public class IdentifierProvider implements Provider<String> {
	
    private static final Logger logger = LogManager.getLogger(IdentifierProvider.class);

	private final String id;
	
	public IdentifierProvider() {
		this.id = UUID.randomUUID().toString();
	}
	
	@Override
	public String get() {
		logger.debug("get - id: {}", id);
		return id;
	}

	@Override
	public String toString() {
		return "IdentifierProvider [id=" + id + "]";
	}
}
