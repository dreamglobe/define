package com.kamomileware.akka.pattern.dispatcher;

import com.kamomileware.define.actor.PlayerActor.ClientHandler;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by pepe on 29/06/14.
 */
public class ClientWSControllerTest {

    @InjectMocks
    private ClientHandler controller;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

//    @Test
//    public void test() throws Exception {
//        mockMvc.perform(get("/login")).andDo(print());
//    }
}
