package com.github.grantwest.sparkj;

        import static org.hamcrest.CoreMatchers.is;
        import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.core.IsNull.nullValue;

        import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
        import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
        import org.glassfish.jersey.media.sse.SseFeature;
        import org.junit.Test;

        import javax.ws.rs.client.Client;
        import javax.ws.rs.client.ClientBuilder;
        import javax.ws.rs.client.WebTarget;
        import java.util.Arrays;
        import java.util.List;
        import java.util.function.Consumer;

/**
 * Created by Mariana on 02/02/2017.
 */
public class EventsRead{
        SparkSession session;
        public static void main(String[] args) {
            EventsRead events = new EventsRead();
            // Change username and password for your username and password
            String username = "";
            String password = "";
            events.session = new SparkSession(username, password);
            events.session.connectIfNotConnected();
            System.out.print(events.session.getTokenKey());
            SparkEventStream stream = events.eventStream((event) -> System.out.println(event.toString()));
        }

        public SparkEventStream eventStream(Consumer<SparkEvent> eventHandler) {
            Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
            WebTarget target = client.target(session.baseUrl).path("/v1/events/gps_ami").queryParam("access_token", session.getTokenKey());
            return new SparkEventStream(target, eventHandler);
        }
}
