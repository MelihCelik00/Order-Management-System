package com.oms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ResultActions performGet(String urlTemplate) throws Exception {
        return mockMvc.perform(get(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPost(String urlTemplate, Object request) throws Exception {
        return mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
    }

    protected ResultActions performPut(String urlTemplate, Object request) throws Exception {
        return mockMvc.perform(put(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
    }

    protected ResultActions performDelete(String urlTemplate) throws Exception {
        return mockMvc.perform(delete(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
} 