package co.deepthought.quickscan.server;

import co.deepthought.quickscan.index.SearcherManager;
import co.deepthought.quickscan.service.*;
import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SearchServer extends AbstractHandler {

    final static Logger LOGGER = Logger.getLogger(SearchServer.class.getCanonicalName());

    public static void main(final String[] args) throws Exception {
        PropertyConfigurator.configure("resources/log4j.properties");
        final Properties prop = new Properties();
        prop.load(new FileInputStream("resources/config.properties"));
        final SearchServer searchServer = new SearchServer(
            prop.getProperty("dbfile"),
            Integer.parseInt(prop.getProperty("port")));
        searchServer.startServer();
    }

    private final int port;
    private final Map<String, BaseService> services;

    public SearchServer(final String dbFile, final int port) throws DatabaseException {
        this.port = port;

        LOGGER.info("Preparing services...");
        final long start = System.currentTimeMillis();

        // TODO: make me configurable
        final ResultStore docStore = new ResultStore(dbFile);
        final SearcherManager manager = new SearcherManager(docStore);
        manager.index();

        this.services = new HashMap<String, BaseService>();
        this.services.put("/clean/", new CleanService(docStore));
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

        response.setContentType("text/html;charset=utf-8");
        try {
            final String responeString = this.handleService(path, payload);
            if(responeString != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(responeString);
                final long end = System.currentTimeMillis();
                LOGGER.info("200 " + path + ":" + (end-start));
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                LOGGER.error("404 " + path);
            }
        } catch(final Exception e) {
            LOGGER.error("500 " + path, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        request.setHandled(true);
    }

    public String handleService(final String path, final String payload) {
        final BaseService service = this.services.get(path);
        if(service != null) {
            return service.handleJson(payload);
        }
        else {
            return null;
        }
    }

    public void startServer() throws Exception {
        Server server = new Server(this.port);
        server.setHandler(this);
        server.start();
        server.join();
    }

}
