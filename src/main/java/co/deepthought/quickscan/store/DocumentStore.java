package co.deepthought.quickscan.store;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

// this import has to happen to use the sqlite jdbc
import org.sqlite.JDBC;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  DocumentStore persists denormalized documents to disk, using SQLITE for portable storage.
 */
public class DocumentStore {

    private final Dao<Document, Integer> documentDao;
    private final Dao<Field, Integer> fieldDao;
    private final Dao<Score, Integer> scoreDao;
    private final Dao<Tag, Integer> tagDao;

    public DocumentStore(final String sqliteFilePath) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + sqliteFilePath);

        this.documentDao = DaoManager.createDao(connectionSource, Document.class);
        this.fieldDao = DaoManager.createDao(connectionSource, Field.class);
        this.scoreDao = DaoManager.createDao(connectionSource, Score.class);
        this.tagDao = DaoManager.createDao(connectionSource, Tag.class);

        TableUtils.createTableIfNotExists(connectionSource, Document.class);
        TableUtils.createTableIfNotExists(connectionSource, Field.class);
        TableUtils.createTableIfNotExists(connectionSource, Score.class);
        TableUtils.createTableIfNotExists(connectionSource, Tag.class);
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

    public Set<String> getDistinctFields(final String shardId) throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);

        final QueryBuilder<Field, Integer> fieldQuery = this.fieldDao.queryBuilder();
        fieldQuery.join(documentQuery);
        fieldQuery.distinct().selectColumns("name");
        fieldQuery.prepare();

        for(final Field field : this.fieldDao.query(fieldQuery.prepare())) {
            result.add(field.getName());
        }
        return result;
    }

    public Set<String> getDistinctScores(final String shardId, final Score.Valence valence) throws SQLException {
        final Set<String> result = new HashSet<String>();

        final QueryBuilder<Document, Integer> documentQuery = this.documentDao.queryBuilder();
        documentQuery.where().eq("shardId", shardId);

        final QueryBuilder<Score, Integer> scoreQuery = this.scoreDao.queryBuilder();
        scoreQuery.join(documentQuery);
        scoreQuery.where().eq("valence", valence);
        scoreQuery.distinct().selectColumns("name");
        scoreQuery.prepare();

        for(final Score score : this.scoreDao.query(scoreQuery.prepare())) {
            result.add(score.getName());
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
        tagQuery.prepare();

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

}