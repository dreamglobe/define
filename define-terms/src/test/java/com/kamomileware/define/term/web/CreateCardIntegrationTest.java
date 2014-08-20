package com.kamomileware.define.term.web;

import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.term.RestDataFixture;
import com.kamomileware.define.term.repository.TermCardRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static com.kamomileware.define.term.RestDataFixture.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by pepe on 5/08/14.
 */
public class CreateCardIntegrationTest {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    MockMvc mockMvc;

    @InjectMocks
    CardCommandController controller;

    @Mock
    TermCardRepository cardRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(controller).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void thatCreateCardWorksCorrectly() throws Exception {
        when(cardRepository.addNewCard(any(TermCard.class))).thenReturn(RestDataFixture.newlyCreatedCard());

        final String eventsAsJSON = createEventsAsJSON();
        logger.info(eventsAsJSON);
        this.mockMvc.perform(
                post("/cards")
                        .content(eventsAsJSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.definitions['" + CAT1_NAME + "']['name']").value(term1().getName()));
    }

}
