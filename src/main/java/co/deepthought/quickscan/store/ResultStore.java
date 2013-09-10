package co.deepthought.quickscan.store;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

/**
 *  ResultStore persists denormalized documents to disk, using BerkeleyDB for portable storage.
 */
public class ResultStore {

    final static Logger LOGGER = Logger.getLogger(ResultStore.class.getCanonicalName());

    final private Environment environment;
    final private EntityStore store;
    final private PrimaryIndex<String, Result> resultIndex;
    final private SecondaryIndex<String, String, Result> shardIndex;

    public ResultStore(final String filePath) throws DatabaseException {
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

        this.resultIndex = this.store.getPrimaryIndex(String.class, Result.class);
        this.shardIndex = this.store.getSecondaryIndex(this.resultIndex, String.class, "shardId");
    }

    public void clean() throws DatabaseException {
        // probably a very slow way to do this...
        final EntityCursor<Result> cursor = this.resultIndex.entities();
        try {
            for(final Result result : cursor) {
                cursor.delete();
            }
        }
        finally {
            cursor.close();
        }
    }

    public void deleteById(final String id) throws DatabaseException {
        this.resultIndex.delete(id);
    }

    public EntityCursor<Result> getByShardId(final String shardId) throws DatabaseException {
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

    public Result getById(final String id) throws DatabaseException {
        return this.resultIndex.get(id);
    }

    public void persist(final Result result) throws DatabaseException {
        this.resultIndex.put(result);
    }

}
