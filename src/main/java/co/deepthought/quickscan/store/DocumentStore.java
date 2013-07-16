package co.deepthought.quickscan.store;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

// this import has to happen to use the sqlite jdbc
import org.sqlite.JDBC;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 *  DocumentStore persists denormalized documents to disk, using SQLITE for portable storage.
 */
public class DocumentStore {

    private final ConnectionSource connectionSource;
    private final Document.Dao documentDao;
    private final Field.Dao fieldDao;
    private final Score.Dao scoreDao;
    private final Tag.Dao tagDao;

    public DocumentStore(final String sqliteFilePath) throws SQLException {
        this.connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + sqliteFilePath);

        this.documentDao = new Document.Dao(this.connectionSource);
        this.fieldDao = new Field.Dao(this.connectionSource);
        this.scoreDao = new Score.Dao(this.connectionSource);
        this.tagDao = new Tag.Dao(this.connectionSource);

        TableUtils.createTableIfNotExists(this.connectionSource, Document.class);
        TableUtils.createTableIfNotExists(this.connectionSource, Field.class);
        TableUtils.createTableIfNotExists(this.connectionSource, Score.class);
        TableUtils.createTableIfNotExists(this.connectionSource, Tag.class);
    }

    public void clean() throws SQLException {
        TableUtils.clearTable(this.connectionSource, Document.class);
        TableUtils.clearTable(this.connectionSource, Field.class);
        TableUtils.clearTable(this.connectionSource, Score.class);
        TableUtils.clearTable(this.connectionSource, Tag.class);
    }

    public Document createDocument(final String documentId, final String resultId, final String shardId)
            throws SQLException {
        final Document document = new Document(documentId, resultId, shardId);
        this.persistDocument(document);
        this.documentDao.assignEmptyForeignCollection(document, "fields");
        this.documentDao.assignEmptyForeignCollection(document, "scores");
        this.documentDao.assignEmptyForeignCollection(document, "tags");
        return document;
    }

    public void deleteById(final String documentId) throws SQLException {
        final Document document = this.getDocumentById(documentId);
        if(document != null) {
            this.fieldDao.delete(document.getFields());
            this.scoreDao.delete(document.getScores());
            this.tagDao.delete(document.getTags());
            this.documentDao.delete(document);
        }
    }

    public Iterable<Document> getDocuments(final String shardId) throws SQLException {
        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);
        final Iterator<Document> iterator = this.documentDao.iterator(documentQuery.prepare());
        return new Iterable<Document>() {
            @Override
            public Iterator<Document> iterator() {
                return iterator;
            }
        };
    }

    public Set<String> getDistinctFields(final String shardId) throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);

        final QueryBuilder<Field, Integer> fieldQuery = this.fieldDao.queryBuilder();
        fieldQuery.join(documentQuery);
        fieldQuery.distinct().selectColumns("name");

        for(final Field field : this.fieldDao.query(fieldQuery.prepare())) {
            result.add(field.getName());
        }
        return result;
    }

    public Set<String> getDistinctScores(final String shardId) throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);

        final QueryBuilder<Score, Integer> scoreQuery = this.scoreDao.queryBuilder();
        scoreQuery.join(documentQuery);
        scoreQuery.distinct().selectColumns("name");

        for(final Score score : this.scoreDao.query(scoreQuery.prepare())) {
            result.add(score.getName());
        }
        return result;
    }

    public Set<String> getDistinctShards() throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.distinct().selectColumns("shardId");
        documentQuery.prepare();

        for(final Document document : this.documentDao.query(documentQuery.prepare())) {
            result.add(document.getShardId());
        }
        return result;
    }

    public Set<String> getDistinctTags(final String shardId) throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);

        final QueryBuilder<Tag, Integer> tagQuery = this.tagDao.queryBuilder();
        tagQuery.join(documentQuery);
        tagQuery.distinct().selectColumns("name");

        for(final Tag tag : this.tagDao.query(tagQuery.prepare())) {
            result.add(tag.getName());
        }
        return result;
    }

    public Document getDocumentById(final String documentId) throws SQLException {
        final QueryBuilder<Document, Integer> query = this.documentDao.queryBuilder();
        query.where().eq("documentId", documentId);
        return this.documentDao.queryForFirst(query.prepare());
    }

    public void persistDocument(final Document document) throws SQLException {
        if(document.isPersisted()) {
            this.documentDao.update(document);
        }
        else {
            this.documentDao.create(document);
        }
    }

    public void transaction(final Callable<Void> callable) throws SQLException {
        TransactionManager.callInTransaction(this.connectionSource, callable);
    }

}