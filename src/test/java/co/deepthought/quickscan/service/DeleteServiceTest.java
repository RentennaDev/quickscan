package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import co.deepthought.quickscan.store.ResultTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DeleteServiceTest {

    @Test
    public void testDeleting() throws DatabaseException, ServiceFailure {
        final ResultStore store = new ResultStore(":tmp");
        for(final Result result : ResultTest.mock()) {
            store.persist(result);
        }
        final DeleteService service = new DeleteService(store);
        final DeleteService.Input input = new DeleteService.Input();
        input.id = "c";
        service.handle(input);
        final Set<String> existing = new HashSet<>();
        for(final Result result : store.getByShardId("a")) {
            existing.add(result.getId());
        }
        assertTrue(existing.contains("a"));
        assertFalse(existing.contains("c"));
    }

}