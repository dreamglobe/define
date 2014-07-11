package com.kamomileware.akka.pattern.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamomileware.define.model.MessageTypes;
import com.kamomileware.define.model.MessageTypes.ClientMessage;
import com.kamomileware.define.model.MessageTypes.ClientReady;
import com.kamomileware.define.model.MessageTypes.ClientResponse;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by pepe on 29/06/14.
 */

public class TestJacksonParser {

    static ObjectMapper mapper;

    @BeforeClass
    public static void  setup(){
        mapper = new ObjectMapper();
    }

    @Test
    public void testClientMessage () throws IOException {
        final ClientResponse r1 = new ClientResponse("This is the response");
        String jsonClientResponse = mapper.writeValueAsString(r1);
        ClientResponse r2 = mapper.readValue(jsonClientResponse, ClientResponse.class);
        Assert.assertEquals(r1.getResponse(), r2.getResponse());
        ClientMessage r3 = mapper.readValue(jsonClientResponse, ClientMessage.class);
        Assert.assertTrue("message es de tipo ClientResponse", r3 instanceof ClientResponse);

        final ClientReady m1 = new ClientReady(true);
        String jsonClientReady = mapper.writeValueAsString(m1);
        ClientReady m2 = mapper.readValue(jsonClientReady, ClientReady.class);
        Assert.assertTrue(m1.isReady() && m2.isReady());
        ClientMessage m3 = mapper.readValue(jsonClientReady, ClientMessage.class);
        Assert.assertTrue("message es de tipo ClientReady", m3 instanceof ClientReady);
    }

    @Test
    public void testUsertMessage () throws IOException {
        System.out.println(mapper.writeValueAsString(new MessageTypes.UsersList(new HashMap<>())));
        System.out.println(mapper.writeValueAsString(new MessageTypes.RegisterUser("userId","userName")));
        System.out.println(mapper.writeValueAsString(new MessageTypes.Latch(2, 30)));
        final Term term = new Term("Term", "Definicion de term", TermCategory.AB);
        System.out.println(mapper.writeValueAsString(new MessageTypes.StartDefinition(60000, term)));
        System.out.println(mapper.writeValueAsString(MessageTypes.START));
        System.out.println(mapper.writeValueAsString(MessageTypes.STOP));
    }
}
