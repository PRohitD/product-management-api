package com.zestindia.productapi.service;

import com.zestindia.productapi.dto.request.ProductRequest;
import com.zestindia.productapi.dto.response.ProductResponse;
import com.zestindia.productapi.entity.Product;
import com.zestindia.productapi.exception.ResourceNotFoundException;
import com.zestindia.productapi.repository.ItemRepository;
import com.zestindia.productapi.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository itemRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {

        productService = new ProductService(
                productRepository,
                itemRepository
        );
    }

    @Test
    void shouldReturnProductWhenProductExists() {

        Product product = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .createdBy("rohit")
                .createdOn(LocalDateTime.now())
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "Dell Laptop",
                response.getProductName()
        );

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(999L)
        );

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldCreateProduct() {

        ProductRequest request = new ProductRequest();
        request.setProductName("HP Laptop");

        Product savedProduct = Product.builder()
                .id(1L)
                .productName("HP Laptop")
                .createdBy("rohit")
                .createdOn(LocalDateTime.now())
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response =
                productService.createProduct(
                        request,
                        "rohit"
                );

        assertNotNull(response);
        assertEquals(
                "HP Laptop",
                response.getProductName()
        );

        assertEquals(
                "rohit",
                response.getCreatedBy()
        );

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {

        Product product = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .createdBy("rohit")
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }
}