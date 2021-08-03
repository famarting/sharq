package org.acme;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sharq.platform.cloudevents.CloudEventsHttpClient;
import io.sharq.platform.cloudevents.Target;
import io.sharq.platform.kvs.client.quarkus.KeyValueStore;

@Path("/")
public class UsersResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String USERS_STORE = "users";

    @Inject
    @RestClient
    KeyValueStore store;

    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/users/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("username") String username) throws Exception {

        InputStream is = store.getValue(USERS_STORE, username);

        User user = mapper.readValue(is, User.class);

        return user;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users")
    public void createUser(User user) throws Exception {
        store.setValue(USERS_STORE, user.username, new ByteArrayInputStream(mapper.writeValueAsBytes(user)));
        fireEvent(user.username);
    }


    @DELETE
    @Path("/users/{username}")
    public void deleteUser(@PathParam("username") String username) throws Exception {
        store.delete(USERS_STORE, username);
        fireEvent(username);
    }

    @Inject
    @ConfigProperty(name = "events.sink")
    String eventsSinkUrl;

    public void fireEvent(String username) {

        URI uri = URI.create(eventsSinkUrl);

        CloudEventsHttpClient proxy = new CloudEventsHttpClient(new Target(uri.getHost(), uri.getPort(), uri.getPath()));

        proxy.send(username.getBytes(), Map.of("ce-type", "users.update", "ce-source", "users-service"))
            .subscribe()
                .with(res -> {

                });

        proxy.send(username.getBytes(), Map.of("ce-type", "another-random.event", "ce-source", "users-service"))
        .subscribe()
            .with(res -> {

            });

    }

    @POST
    @Path("/events")
    @Consumes("*/*")
    public void usersEvents(byte[] body, @Context HttpHeaders headers) {
        //TODO check the ce-type is users.update
        logger.info("new modification to user " + new String(body));
    }

}