package co.deepthought.quickscan.service;

import co.deepthought.quickscan.server.DeprecateService;
import co.deepthought.quickscan.server.ServiceFailure;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DeleteServiceTest {

    @Test
    public void testDeleting() throws DatabaseException, ServiceFailure {
        final DocumentStore store = new DocumentStore(":tmp");
        for(final Document document : DocumentTest.mock()) {
            store.persist(document);
        }
        final DeprecateService service = new DeprecateService(store);
        final DeprecateService.Input input = new DeprecateService.Input();
        input.id = "c";
        service.handle(input);
        final Set<String> existing = new HashSet<>();
        for(final Document document : store.getByShardId("a")) {
            existing.add(document.getId());
        }
        assertTrue(existing.contains("a"));
        assertFalse(existing.contains("c"));
    }

}