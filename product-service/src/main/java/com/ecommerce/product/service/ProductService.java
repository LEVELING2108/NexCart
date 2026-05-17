package com.ecommerce.product.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final CategoryRepository categoryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(request.getCategoryName()).build()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        // Save to Elasticsearch
        ProductDocument document = ProductDocument.builder()
                .id(savedProduct.getId().toString())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .category(category.getName())
                .build();
        productSearchRepository.save(document);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product created successfully")
                .data(mapToResponse(savedProduct))
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<ProductResponse> getProduct(UUID id) {
        return productRepository.findById(id)
                .map(product -> ApiResponse.<ProductResponse>builder()
                        .success(true)
                        .message("Product fetched successfully")
                        .data(mapToResponse(product))
                        .build())
                .orElse(ApiResponse.<ProductResponse>builder()
                        .success(false)
                        .message("Product not found")
                        .build());
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Products fetched successfully")
                .data(products)
                .build();
    }

    public ApiResponse<Page<ProductDocument>> searchProducts(String query, String category, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, Pageable pageable) {
        log.info("Searching products with query: {}, category: {}, price range: [{}, {}], sortBy: {}, page: {}", 
                query, category, minPrice, maxPrice, sortBy, pageable.getPageNumber());

        Criteria criteria = new Criteria();

        if (query != null && !query.isEmpty()) {
            criteria = criteria.subCriteria(new Criteria("name").contains(query)
                    .or(new Criteria("description").contains(query)));
        }

        if (category != null && !category.isEmpty()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }

        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice.doubleValue()));
        }

        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice.doubleValue()));
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        criteriaQuery.setPageable(pageable);
        
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort sort = switch (sortBy.toLowerCase()) {
                case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
                case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
                case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
                case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
                default -> Sort.unsorted();
            };
            criteriaQuery.addSort(sort);
        }

        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(criteriaQuery, ProductDocument.class);
        
        List<ProductDocument> results = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        Page<ProductDocument> page = new PageImpl<>(results, pageable, searchHits.getTotalHits());

        return ApiResponse.<Page<ProductDocument>>builder()
                .success(true)
                .message("Search completed")
                .data(page)
                .build();
    }

    @Transactional
    public void deductInventory(UUID productId, Integer quantity) {
        log.info("Deducting inventory for product: {}, quantity: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        // Update Elasticsearch
        productSearchRepository.findById(productId.toString()).ifPresent(doc -> {
            // Document doesn't have stockQuantity field currently based on earlier read, 
            // but let's re-verify ProductDocument.java
        });
    }
    
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}