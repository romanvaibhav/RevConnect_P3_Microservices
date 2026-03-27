package com.revconnect.productservice.controller;

import com.revconnect.productservice.dto.CreateProductRequest;
import com.revconnect.productservice.dto.ProductDTO;
import com.revconnect.productservice.dto.UpdateProductRequest;
import com.revconnect.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO product = productService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/business/{businessUserId}")
    public ResponseEntity<List<ProductDTO>> getProductsByBusiness(@PathVariable Long businessUserId) {
        List<ProductDTO> products = productService.getProductsByBusiness(businessUserId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDTO product = productService.updateProduct(productId, userId, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long userId) {
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        List<ProductDTO> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
}
