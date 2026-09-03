package com.zestindia.productapi.controller;

import com.zestindia.productapi.dto.response.ItemResponse;

import java.util.List;
import com.zestindia.productapi.dto.request.ProductRequest;
import com.zestindia.productapi.dto.response.ProductResponse;
import com.zestindia.productapi.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                productService.getAllProducts(page, size)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    //
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemResponse>> getItems(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductItems(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(

            @Valid
            @RequestBody ProductRequest request,

            Authentication authentication
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productService.createProduct(
                                request,
                                authentication.getName()
                        )
                );
    }



    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(

            @PathVariable Long id,

            @Valid
            @RequestBody ProductRequest request,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}