package info.itsthesky.websky.tools;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import info.itsthesky.websky.WebSky;
import info.itsthesky.websky.skript.WebsiteRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Class to handle custom web server and manage web server handle at the same time.
 * @author ItsTheSky
 */
public class WebServer {

    /**
     * This variable return the default content of a html web page.
     * Add a simple title, encode the page and use header & html tags.
     */
    @NotNull private final String DEFAULT_CONTENT = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "\t<meta charset=\"UTF-8\">\n" +
            "\t<title>Page Name</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\t<h1>\n" +
            "\t\tWelcome to WebSky!\n" +
            "</body>\n" +
            "</html>";

    private Integer port;
    private File file;
    @NotNull private Boolean started;
    @Nullable private HttpServer server;
    private final String name;

    /**
     * Create a new web server with a not null port.
     * @param port The wanted port
     * @param file The file to show when connection is made
     */
    public WebServer(@NotNull final Integer port, @NotNull final File file, @NotNull final String name) {
        this.port = port;
        this.file = file;
        this.started = false;
        this.name = name;
    }

    /**
     * Check if the current webserver is empty (can be built).
     * @return If the webserver is empty
     */
    public boolean isEmpty() {
        return port == null || file == null;
    }

    /**
     * Start the web server at the specified port.
     */
    public void start() {
        if (isEmpty()) {
            WebSky.error("WebSky cannot start an empty (without port or without file) web server!");
            return;
        }
        WebSky.log("Starting web server on port " + port + "...");
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (server == null) return;
        server.createContext("/", new WebHandler());
        server.setExecutor(null);
        server.start();
        WebSky.log("Web server started successfully on port " + port + "!");
        this.started = true;
    }

    /**
     * Stop the web server & remove it from the web server list
     */
    public void stop() {
        if (!isStarted()) {
            WebSky.error("Can't stop a web server which is not already started!");
            return;
        }
        WebSky.WEB_SERVERS.remove(this.name);
        assert server != null;
        server.stop(1);
    }

    public Integer getPort() {
        return port;
    }

    public File getFile() {
        return file;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public @Nullable HttpServer getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Handle connection to the website.
     */
    class WebHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestURI().toString().contains("favicon.ico")) return;
            if (!isStarted()) {
                OutputStream os = t.getResponseBody();
                os.write("This webserver is not started! Check documentation for more information.".getBytes());
                os.close();
                t.sendResponseHeaders(501, 0);
                return;
            }
            String response = "An internal error occurred while loading the HTML page!";
            try {
                response = Parsing.parse(new WebsiteRequest(t), file);
            } catch (Exception ex) {
                WebSky.exception(ex, "loading the HTML page");
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    @Override
    public String toString() {
        return "WebServer{" +
                "port=" + port +
                ", file=" + file +
                ", started=" + started +
                '}';
    }
}
