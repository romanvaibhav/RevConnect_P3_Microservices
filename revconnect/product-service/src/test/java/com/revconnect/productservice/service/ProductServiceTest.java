package com.revconnect.productservice.service;

import com.revconnect.productservice.client.UserServiceClient;
import com.revconnect.productservice.dto.CreateProductRequest;
import com.revconnect.productservice.dto.ProductDTO;
import com.revconnect.productservice.dto.UserDTO;
import com.revconnect.productservice.entity.Product;
import com.revconnect.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private UserDTO businessUser;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setBusinessUserId(1L);
        mockProduct.setProductName("Test Product");
        mockProduct.setDescription("A test product");
        mockProduct.setPrice(99.99);

        businessUser = new UserDTO();
        businessUser.setId(1L);
        businessUser.setUsername("bizuser");
        businessUser.setRole("BUSINESS");
    }

    @Test
    void createProduct_shouldSave_whenUserIsBusiness() {
        when(userServiceClient.getUserById(1L)).thenReturn(businessUser);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        CreateProductRequest req = new CreateProductRequest();
        req.setProductName("Test Product");
        req.setDescription("A test product");
        req.setPrice(99.99);

        ProductDTO result = productService.createProduct(1L, req);
        assertNotNull(result);
        assertEquals("Test Product", result.getProductName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_shouldThrow_whenUserIsNotBusiness() {
        UserDTO regularUser = new UserDTO();
        regularUser.setId(2L);
        regularUser.setRole("USER");
        when(userServiceClient.getUserById(2L)).thenReturn(regularUser);

        CreateProductRequest req = new CreateProductRequest();
        req.setProductName("Test");
        req.setPrice(10.0);

        assertThrows(Exception.class, () -> productService.createProduct(2L, req));
        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductById_shouldReturn_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        ProductDTO result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProductById_shouldThrow_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.getProductById(99L));
    }

    @Test
    void getProductsByBusiness_shouldReturnList() {
        when(productRepository.findByBusinessUserId(1L)).thenReturn(List.of(mockProduct));
        List<ProductDTO> result = productService.getProductsByBusiness(1L);
        assertEquals(1, result.size());
    }

    @Test
    void deleteProduct_shouldDelete_whenOwner() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        productService.deleteProduct(1L, 1L);
        verify(productRepository).delete(mockProduct);
    }

    @Test
    void deleteProduct_shouldThrow_whenNotOwner() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        assertThrows(Exception.class, () -> productService.deleteProduct(1L, 99L));
    }
}
