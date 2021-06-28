package info.itsthesky.websky.tools;

import info.itsthesky.websky.WebSky;

public class WebSkyErrorHandler {

    public static void logException(Exception exception) {
        WebSky.error("WebSky got an internal error: " + exception.getMessage());
    }

}
