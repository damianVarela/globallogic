package com.evaluation.globallogic;

import com.evaluation.globallogic.dto.AccountDTO;
import com.evaluation.globallogic.dto.AccountWithTokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GlobalLogicApplication.class)
class GlobalLogicApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void signUpTest() throws Exception {
        val mapper = new ObjectMapper();
        val accountDTO = new AccountDTO();
        accountDTO.setEmail("user@company.com");
        accountDTO.setPassword("aV4lid0ne");
        val signUpResult = mvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsBytes(accountDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        val resultWithTokenDTO = mapper.readValue(signUpResult, AccountWithTokenDTO.class);
        val token = resultWithTokenDTO.getToken();
        Assertions.assertThat(token)
                .isNotNull()
                .isNotEmpty();

        val loginResult = mvc.perform(MockMvcRequestBuilders.get("/login")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        val resultWithNewToken = mapper.readValue(loginResult, AccountWithTokenDTO.class);
        val newToken = resultWithNewToken.getToken();

        Assertions.assertThat(newToken)
                .isNotNull()
                .isNotEmpty()
                .isNotEqualTo(token);
    }

}
