package jettyServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * This class uses Jetty & servlets to implement server serving hotel info
 */
public class JettyHotelServer {
    // FILL IN CODE

    public static final int PORT = 8084;

    public JettyHotelServer() throws Exception {
        Server server = new Server(PORT);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(AttractionsServlet.class, "/attractions");
        handler.addServletWithMapping(HotelServlet.class, "/hotelInfo");
        handler.addServletWithMapping(ReviewsServlet.class, "/reviews");

        server.setHandler(handler);
        server.start();
        server.join();
    }
}

