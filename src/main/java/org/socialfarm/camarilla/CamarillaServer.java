package org.socialfarm.camarilla;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Hello world!
 *
 */
public class CamarillaServer
{
    public static void main( String[] args )
    {
        System.out.println( "Starting " + args );

        // TODO : parse args or config to get port etc
        final URI restEndPoint = URI.create( String.format("http://0.0.0.0:%d/", 3242) )  ;

        final ResourceConfig resourceConfig = new ResourceConfig( CamarillaEndPoints.class ) ;

        final HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(restEndPoint, resourceConfig);

        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig()
                .setPoolName( "camarilla-service-threads")
                .setCorePoolSize(256)
                .setMaxPoolSize(65535) ;

        NetworkListener listener = httpServer.getListeners().iterator().next();
        listener.getTransport().setWorkerThreadPoolConfig(threadPoolConfig);
        try {
            System.out.println( "Serving requests, press any key to exit" );
            System.in.read() ;

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            httpServer.shutdownNow();
        }

        System.out.println( "Done " + args );
    }
}
