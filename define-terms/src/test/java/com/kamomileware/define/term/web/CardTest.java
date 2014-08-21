package com.kamomileware.define.term.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.term.fixture.RestDataFixture;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static junit.framework.TestCase.*;

/**
 * Functional test must have Server started
 * Created by pepe on 6/08/14.
 */
public class CardTest {

    public void thatOrdersCanBeAddedAndQueried() throws JsonProcessingException {

        HttpEntity<String> requestEntity = new HttpEntity<String>(
                RestDataFixture.createEventsAsJSON(),
                getHeaders("test" + ":" + "test123"));

        RestTemplate template = new RestTemplate();
        ResponseEntity<TermCard> entity = template.postForEntity(
                "http://localhost:8080/cards",
                requestEntity, TermCard.class);

        String path = entity.getHeaders().getLocation().getPath();

        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertTrue(path.startsWith("/cards"));
        TermCard card = entity.getBody();

        System.out.println ("The Location is " + entity.getHeaders().getLocation());

        assertEquals(4, card.getDefinitions().size());
    }

    public void thatOrdersCannotBeAddedAndQueriedWithBadUser() throws JsonProcessingException {

        HttpEntity<String> requestEntity = new HttpEntity<String>(
                RestDataFixture.createEventsAsJSON(),
                getHeaders("test" + ":" + "BADPASSWORD"));

        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<TermCard> entity = template.postForEntity(
                    "http://localhost:8080/cards",
                    requestEntity, TermCard.class);

            fail("Request Passed incorrectly with status " + entity.getStatusCode());
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }
    }

    static HttpHeaders getHeaders(String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));

        return headers;
    }
}
