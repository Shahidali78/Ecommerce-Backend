package com.shahid.ecommerce;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EcommerceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void registrationAndLoginReturnJwtTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Test Customer",
                                  "email": "customer@example.com",
                                  "password": "Secure123!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("customer@example.com"))
                .andExpect(jsonPath("$.user.role").value("USER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "customer@example.com",
                                  "password": "Secure123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void catalogIsPublicButCartRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerCanCompleteCheckoutAndAdminCanManageOrder() throws Exception {
        String adminToken = tokenFrom(postJson("/api/auth/login", """
                {
                  "email": "admin@example.com",
                  "password": "AdminTest123!"
                }
                """, null));

        String categoryResponse = postJson("/api/admin/categories", """
                {
                  "name": "Integration Test Category",
                  "description": "Created by the checkout integration test"
                }
                """, adminToken);
        Integer categoryId = JsonPath.read(categoryResponse, "$.id");

        String productResponse = postJson("/api/admin/products", """
                {
                  "name": "Test Product",
                  "description": "Product used to verify checkout",
                  "price": 25.50,
                  "stock": 5,
                  "categoryId": %d
                }
                """.formatted(categoryId), adminToken);
        Integer productId = JsonPath.read(productResponse, "$.id");

        String customerToken = tokenFrom(postJson("/api/auth/register", """
                {
                  "fullName": "Checkout Customer",
                  "email": "checkout@example.com",
                  "password": "Secure123!"
                }
                """, null));

        postJson("/api/cart/add", """
                {
                  "productId": %d,
                  "quantity": 2
                }
                """.formatted(productId), customerToken);

        String orderResponse = postJson("/api/orders", """
                {
                  "shippingAddress": "123 Integration Test Street"
                }
                """, customerToken);
        Integer orderId = JsonPath.read(orderResponse, "$.id");

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(3));

        mockMvc.perform(get("/api/cart").header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0));

        mockMvc.perform(put("/api/admin/orders/{id}/status", orderId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    private String postJson(String path, String body, String token) throws Exception {
        var request = post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        if (token != null) {
            request.header("Authorization", bearer(token));
        }
        return mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private String tokenFrom(String response) {
        return JsonPath.read(response, "$.accessToken");
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
