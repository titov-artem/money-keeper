package com.github.money.keeper.ui;

import org.junit.Test;

import javax.ws.rs.*;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EndpointTest {

    @Test
    public void testRegister() throws Exception {
        Endpoint endpoint = new Endpoint();
        StubController controller = new StubController();
        endpoint.register(controller);

//        assertThat(endpoint.get("/test", "1", "2", "3", "4"), is("1-2-3-4"));

//        endpoint.post("/test/path/", "1.0", "2.0", "true");
        assertThat(controller.postA, is(1.0F));
        assertThat(controller.postB, is(2.0));
        assertThat(controller.postC, is(true));

//        assertThat(endpoint.put("/test", "good string"), is("good string"));

//        assertThat(endpoint.delete("/test//path/good", "{\"a\": \"b\"}"), is("{\"a\":\"b\"}"));
    }

    @Path("/test")
    public static final class StubController {

        private float postA;
        private double postB;
        private boolean postC;

        @GET
        public String get(byte a, short b, int c, long e) {
            return String.format("%d-%s-%d-%d", a, b, c, e);
        }

        @POST
        @Path("/path")
        public void post(float a, double b, boolean c) {
            this.postA = a;
            this.postB = b;
            this.postC = c;
        }

        @PUT
        public String put(String s) {
            return s;
        }

        @DELETE
        @Path("/path/good")
        public Map<String, String> delete(Map<String, String> source) {
            return source;
        }
    }

}