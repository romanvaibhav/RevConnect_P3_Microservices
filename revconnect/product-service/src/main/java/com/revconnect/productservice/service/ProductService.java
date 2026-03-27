package com.revconnect.productservice.service;

import com.revconnect.productservice.client.UserServiceClient;
import com.revconnect.productservice.dto.CreateProductRequest;
import com.revconnect.productservice.dto.ProductDTO;
import com.revconnect.productservice.dto.UpdateProductRequest;
import com.revconnect.productservice.dto.UserDTO;
import com.revconnect.productservice.entity.Product;
import com.revconnect.productservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserServiceClient userServiceClient;

    public ProductService(ProductRepository productRepository, UserServiceClient userServiceClient) {
        this.productRepository = productRepository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public ProductDTO createProduct(Long businessUserId, CreateProductRequest request) {
        UserDTO user = userServiceClient.getUserById(businessUserId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!"BUSINESS".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only business users can create products");
        }

        Product product = new Product();
        product.setBusinessUserId(businessUserId);
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setProductImagePath(request.getProductImagePath());

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToDTO(product);
    }

    public List<ProductDTO> getProductsByBusiness(Long businessUserId) {
        List<Product> products = productRepository.findByBusinessUserId(businessUserId);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, Long businessUserId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!product.getBusinessUserId().equals(businessUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own products");
        }

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getProductImagePath() != null) {
            product.setProductImagePath(request.getProductImagePath());
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long productId, Long businessUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!product.getBusinessUserId().equals(businessUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own products");
        }

        productRepository.delete(product);
    }

    public List<ProductDTO> searchProducts(String query) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(query);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setBusinessUserId(product.getBusinessUserId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setProductImagePath(product.getProductImagePath());
        return dto;
    }
}
