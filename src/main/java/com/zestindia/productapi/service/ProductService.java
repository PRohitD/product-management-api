package com.zestindia.productapi.service;

import com.zestindia.productapi.dto.request.ProductRequest;
import com.zestindia.productapi.dto.response.ItemResponse;
import com.zestindia.productapi.dto.response.ProductResponse;
import com.zestindia.productapi.entity.Product;
import com.zestindia.productapi.exception.ResourceNotFoundException;
import com.zestindia.productapi.repository.ItemRepository;
import com.zestindia.productapi.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;

    // Constructor injection
    public ProductService(
            ProductRepository productRepository,
            ItemRepository itemRepository
    ) {
        this.productRepository = productRepository;
        this.itemRepository = itemRepository;
    }

    // Get all products with pagination
    public Page<ProductResponse> getAllProducts(int page, int size) {

        Page<Product> products =
                productRepository.findAll(
                        PageRequest.of(
                                page,
                                size,
                                Sort.by("createdOn").descending()
                        )
                );

        return products.map(this::mapToResponse);
    }

    // Get product by ID
    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: " + id
                                )
                        );

        return mapToResponse(product);
    }

    // Create product
    public ProductResponse createProduct(
            ProductRequest request,
            String username
    ) {

        Product product = Product.builder()
                .productName(request.getProductName())
                .createdBy(username)
                .build();

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // Update product
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request,
            String username
    ) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: " + id
                                )
                        );

        product.setProductName(request.getProductName());
        product.setModifiedBy(username);

        Product updatedProduct =
                productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // Delete product
    public void deleteProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: " + id
                                )
                        );

        productRepository.delete(product);
    }

    // Get items for a product
    public List<ItemResponse> getProductItems(Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return itemRepository
                .findByProductId(productId)
                .stream()
                .map(item ->
                        ItemResponse.builder()
                                .id(item.getId())
                                .quantity(item.getQuantity())
                                .build()
                )
                .toList();
    }

    // Convert Product entity to ProductResponse
    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .createdBy(product.getCreatedBy())
                .createdOn(product.getCreatedOn())
                .modifiedBy(product.getModifiedBy())
                .modifiedOn(product.getModifiedOn())
                .build();
    }
}