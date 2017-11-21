package pl.mkarebski.longanddifficulturlhelper;

import android.util.Log;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;
import static java.lang.String.format;

public class AndroidHttpServer extends NanoHTTPD {

    private String urlToRedirect;

    public AndroidHttpServer(int port) {
        super(port);
    }

    public AndroidHttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d("LongURLServer", "Incoming Request");
        Log.d("LongURLServer", session.toString());
        Log.d("LongURLServer", session.getHeaders().toString());
        Log.d("LongURLServer", session.getMethod().toString());


        Log.d("LongURLServer", "URL: " + urlToRedirect);
        Response response = newFixedLengthResponse(format("<html><head></head><body>" +
                "<h1><a href=\"%s\">Redirect</a></h1>" +
                "</body>" +
                "<script type=\"text/javascript\">" +
                "document.getElementsByTagName(\"a\")[0].click();" +
                "</script></html>\n", urlToRedirect));

        Log.d("LongURLServer", "Response prepared");
        Log.d("LongURLServer", "Response status: " + String.valueOf(response.getStatus()));
        return response;
    }

    public void start(String urlToRedirect) throws IOException {
        this.urlToRedirect = urlToRedirect;
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d("LongURLServer", "Server is up");
    }

    @Override
    public void stop() {
        Log.d("LongURLServer", "Server is down");
        super.stop();
    }
}
