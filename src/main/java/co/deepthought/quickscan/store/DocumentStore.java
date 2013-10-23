package co.deepthought.quickscan.store;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

/**
 *  DocumentStore persists denormalized documents to disk, using BerkeleyDB for portable storage.
 */
public class DocumentStore {

    final static Logger LOGGER = Logger.getLogger(DocumentStore.class.getCanonicalName());

    final private Environment environment;
    final private EntityStore store;
    final private PrimaryIndex<String, Document> resultIndex;
    final private SecondaryIndex<String, String, Document> shardIndex;

    public DocumentStore(final String filePath) throws DatabaseException {
        final EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);

        final File file;
        if(filePath.equals(":tmp")) {
            final Random random = new Random();
            final String dbName = "/tmp/db-" + random.nextInt();
            file = new File(dbName);
        }
        else {
            file = new File(filePath);
        }
        file.mkdir();
        LOGGER.info("Using " + file + " for data persistence.");

        this.environment = new Environment(file, envConfig);

        final StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);
        this.store = new EntityStore(this.environment, "store", storeConfig);

        this.resultIndex = this.store.getPrimaryIndex(String.class, Document.class);
        this.shardIndex = this.store.getSecondaryIndex(this.resultIndex, String.class, "shardId");
    }

    public void clean() throws DatabaseException {
        // probably a very slow way to do this...
        final Transaction txn = this.environment.beginTransaction(null, null);
        final EntityCursor<Document> cursor = this.resultIndex.entities(txn, null);
        try {
            for(final Document document : cursor) {
                cursor.delete();
            }
            cursor.close();
            txn.commit();
        }
        catch (Exception e) {
            cursor.close();
            txn.abort();
        }
    }

    public EntityCursor<Document> getByShardId(final String shardId) throws DatabaseException {
        return this.shardIndex.subIndex(shardId).entities();
    }

    public Set<String> getDistinctShardIds() throws DatabaseException {
        final Set<String> result = new HashSet<>();
        final EntityCursor<String> keyCursor = this.shardIndex.keys();
        try {
            for(final String key : keyCursor) {
                result.add(key);
            }
            return result;
        }
        finally {
            keyCursor.close();
        }
    }

    public Document getById(final String id) throws DatabaseException {
        return this.resultIndex.get(id);
    }

    public void persist(final Document document) throws DatabaseException {
        this.resultIndex.put(document);
    }

}
