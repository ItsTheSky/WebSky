package info.itsthesky.websky.tools;

import ch.njol.skript.lang.SkriptEvent;
import info.itsthesky.websky.WebSky;

import java.io.File;

/**
 * Class which handle custom web server builder.
 */
public class WebServerBuilder {

    private final String name;
    private Integer port;
    private File index;

    public WebServerBuilder(final String name) {
        this.index = null;
        this.port = null;
        this.name = name;
    }

    public WebServer build() {
        if (index == null || port == null) {
            WebSky.error("Can't build an empty web server builder! Must have at least a port and an index file.");
            return null;
        }
        WebServer server = new WebServer(port, index, name);
        WebSky.WEB_SERVERS.put(name, server);
        return server;
    }

    public String getName() {
        return name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }
}
