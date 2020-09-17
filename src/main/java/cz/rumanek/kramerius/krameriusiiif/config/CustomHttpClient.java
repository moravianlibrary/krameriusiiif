package cz.rumanek.kramerius.krameriusiiif.config;

import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AUTH;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * - Supports both HTTP and HTTPS
 * - Uses a connection pool to re-use connections and save overhead of creating connections.
 * - Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)
 * - Starts an idle connection monitor to continuously clean up stale connections.
 */
@Configuration
@EnableScheduling
public class CustomHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpClient.class);

    // Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT = 30000;

    // The timeout when requesting a connection from the connection manager.
    private static final int REQUEST_TIMEOUT = 30000;

    // The timeout for waiting for data
    private static final int SOCKET_TIMEOUT = 10000;

    private static final int MAX_TOTAL_CONNECTIONS = 500;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;
    private static final int IDLE_EVICTION_INTERVAL_MILLIS = 2500;


    SSLContext sslContext = SSLContexts
            .custom()
            .loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            })
            .build();
    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
            .<ConnectionSocketFactory> create().register("https", sslsf)
            .build();

    public CustomHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        poolingConnectionManager.setDefaultMaxPerRoute(MAX_TOTAL_CONNECTIONS);
        return poolingConnectionManager;
    }

    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long defaultKeepAlive = super.getKeepAliveDuration(response,context);
                return defaultKeepAlive <= 0 ? DEFAULT_KEEP_ALIVE_TIME_MILLIS : defaultKeepAlive;
            }
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();

        /**
         * Header to eliminate Auth checking
         */
        List<Header> headers = Lists.newArrayList();
        headers.add(new BasicHeader(AUTH.WWW_AUTH_RESP, "none"));
        /**
         * Header to eliminate Proxy checking
         */
        headers.add(new BasicHeader(AUTH.PROXY_AUTH_RESP, "none"));

        return HttpClients.custom()
                .disableCookieManagement()///*** performance
                .disableAuthCaching()//*** performance
                .disableDefaultUserAgent()
                .disableConnectionState()
                //.setProxyAuthenticationStrategy(null)
                .setDefaultHeaders(headers)
                .disableRedirectHandling()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)

                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                //.setRedirectStrategy(new LaxRedirectStrategy()) //REDIRECT ALL
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = IDLE_EVICTION_INTERVAL_MILLIS)
            public void run() {
                try {
                    if (connectionManager != null) {
                        LOGGER.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                        LOGGER.trace(connectionManager.getTotalStats().toString());
                        Set<HttpRoute> routes = connectionManager.getRoutes();
                        for (HttpRoute route : routes){
                            LOGGER.trace(route.toString() + " max:" + connectionManager.getMaxPerRoute(route));
                        }

                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
                    } else {
                        LOGGER.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                }
            }
        };
    }
}
