package ch.bujaki.loadbalancer.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;

import org.junit.Test;

import ch.bujaki.loadbalancer.provider.IdentifierProvider;

/**
 * Tests the {@link IdentifierProvider} class.
 */
public class IdentifierProviderTest {
	
    @Test
    public void test_get_twoDifferentInstances() {
    	IdentifierProvider identifierProvider1 = new IdentifierProvider();
    	IdentifierProvider identifierProvider2 = new IdentifierProvider();
    	
    	assertNotEquals(identifierProvider1.get(), identifierProvider2.get());
    }

    @Test
    public void test_get_hundredDifferentInstances() {
    	HashSet<String> ids = new HashSet<>();
    	
    	for(int i = 0; i < 100; i++) {
    		ids.add(new IdentifierProvider().get());
    	}
    	
    	// All the identifiers should be unique:
    	assertEquals(100, ids.size());
    }
}
