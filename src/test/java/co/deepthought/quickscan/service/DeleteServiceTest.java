package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DeleteServiceTest {

    @Test
    public void testDeleting() throws SQLException, ServiceFailure {
        final DocumentStore store = new DocumentStore(":memory:");
        DocumentTest.mockDocuments(store);
        final DeleteService service = new DeleteService(store);
        final DeleteService.Input input = new DeleteService.Input();
        input.documentId = "c";
        service.handle(input);
        final Set<String> existing = new HashSet<String>();
        for(final Document document : store.getDocuments("a")) {
            existing.add(document.getDocumentId());
        }
        assertTrue(existing.contains("a"));
        assertFalse(existing.contains("c"));
    }

}