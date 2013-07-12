package co.deepthought.quickscan.server;

import co.deepthought.quickscan.index.SearcherManager;
import co.deepthought.quickscan.service.*;
import co.deepthought.quickscan.store.DocumentStore;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SearchServer extends AbstractHandler {

    final static Logger LOGGER = Logger.getLogger(SearchServer.class.getCanonicalName());

    public static void main(final String[] args) throws Exception {
        PropertyConfigurator.configure("resources/log4j.properties");
        final SearchServer searchServer = new SearchServer();
        searchServer.startServer();
    }

    private final Map<String, BaseService> services;

    public SearchServer() throws SQLException {
        LOGGER.info("Preparing services...");
        final long start = System.currentTimeMillis();

        // TODO: make me configurable
        final DocumentStore docStore = new DocumentStore(":memory:");
        final SearcherManager manager = new SearcherManager(docStore);
        manager.index();

        this.services = new HashMap<String, BaseService>();
        this.services.put("/delete/", new DeleteService(docStore));
        this.services.put("/index/", new IndexService(manager));
        this.services.put("/search/", new SearchService(manager));
        this.services.put("/upsert/", new UpsertService(docStore));

        final long end = System.currentTimeMillis();
        LOGGER.info("Services started in " + (end-start) + "ms");
    }

    @Override
    public void handle(
            final String s,
            final Request request,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse response) throws IOException, ServletException
        {
        final long start = System.currentTimeMillis();
        final String path = request.getPathInfo();
        final String payload = request.getParameter("payload");

        try {
            final String responeString = this.handleService(path, payload);
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            request.setHandled(true);
            response.getWriter().print(responeString);
            final long end = System.currentTimeMillis();
            LOGGER.info("Service: " + path + " OK " + (end-start));
        } catch(final Exception e) {
            // TODO: log critical, create a safe 500 response
        }

    }

    public String handleService(final String path, final String payload) {
        final BaseService service = this.services.get(path);
        if(service != null) {
            return service.handleJson(payload);
        }
        else {
            // 404?
            return null;
        }
    }

    public void startServer() throws Exception {
        Server server = new Server(5400);
        server.setHandler(this);
        server.start();
        server.join();
    }

}
