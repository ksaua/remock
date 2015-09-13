package no.saua.remock.webapptest;


import no.saua.remock.CommonTest;
import no.saua.remock.RemockWebAppTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RemockWebAppTest
@ContextConfiguration(classes = WebAppTest.MyController.class)
public class WebAppTest extends CommonTest {

    @Inject
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void meh() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/abc")).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("abcxyz", contentAsString);
    }

    @Controller
    public static class MyController {
        @RequestMapping("/abc")
        @ResponseBody
        public String abc() {
            return "abcxyz";
        }
    }
}
