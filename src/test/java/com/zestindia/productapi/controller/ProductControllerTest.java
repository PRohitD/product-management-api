package com.zestindia.productapi.controller;

import com.zestindia.productapi.dto.response.ProductResponse;
import com.zestindia.productapi.security.JwtService;
import com.zestindia.productapi.service.ProductService;
import com.zestindia.productapi.security.CustomUserDetailsService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldGetProductById() throws Exception {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .productName("Laptop")
                .createdBy("admin")
                .createdOn(LocalDateTime.now())
                .build();


        given(productService.getProductById(1L))
                .willReturn(response);

        mockMvc.perform(
                        get("/api/v1/products/1")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.createdBy").value("admin"));
    }
}