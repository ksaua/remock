package no.saua.remock.webapptest;


import no.saua.remock.CommonTest;
import no.saua.remock.RemockWebAppTest;
import no.saua.remock.ReplaceWithMock;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
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
@ContextConfiguration
public class WebAppTest extends CommonTest {

    @Inject
    private WebApplicationContext context;

    @ReplaceWithMock
    private SomeRepository someRepository;

    @Test
    public void webAppTest() throws Exception {
        // Setup
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        Mockito.when(someRepository.callMethod()).thenReturn("abcxyz");

        // Act
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/abc")).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        // Assert
        assertEquals("abcxyz", contentAsString);
    }

    @Configuration
    @Import({MyController.class, SomeRepository.class})
    public static class TestConfig {
    }

    @Controller
    public static class MyController {

        @Inject
        private SomeRepository someRepository;

        @RequestMapping("/abc")
        @ResponseBody
        public String abc() {
            return someRepository.callMethod();
        }
    }

    @Service
    private static class SomeRepository {
        public String callMethod() {
            throw new AssertionError("This should have been replaced.");
        }
    }
}
