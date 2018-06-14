package com.jackbarrile.controller;

import com.jackbarrile.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class GreetingControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private GreetingController greetingController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(greetingController).build();
    }

    @Test
    public void testGreeting() throws Exception {
        String uriTemplate = "/greet/greeting";
        MvcResult result = mockMvc.perform(get(uriTemplate)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();

        /* Assert a 200 (status == ok) and the response body is as expected */
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("{\"id\":1,\"content\":\"Hello, World!\"}", result.getResponse().getContentAsString());
    }
}
