package com.revconnect.productservice.dto;

public class ProductDTO {
    private Long id;
    private Long businessUserId;
    private String productName;
    private String description;
    private Double price;
    private String productImagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessUserId() {
        return businessUserId;
    }

    public void setBusinessUserId(Long businessUserId) {
        this.businessUserId = businessUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductImagePath() {
        return productImagePath;
    }

    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }
}
