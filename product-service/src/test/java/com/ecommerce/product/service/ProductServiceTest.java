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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private org.springframework.data.elasticsearch.core.ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;
    private Category category;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        
        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .build();

        productRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Gaming Laptop")
                .price(BigDecimal.valueOf(1500.0))
                .stockQuantity(10)
                .categoryName("Electronics")
                .build();

        product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(BigDecimal.valueOf(1500.0))
                .stockQuantity(10)
                .category(category)
                .build();
    }

    @Test
    void createProduct_ShouldReturnSuccess() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productSearchRepository.save(any(ProductDocument.class))).thenReturn(null); // Document doesn't return anything needed

        ApiResponse<ProductResponse> response = productService.createProduct(productRequest);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("Laptop", response.getData().getName());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
    }

    @Test
    void getProduct_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ApiResponse<ProductResponse> response = productService.getProduct(productId);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(productId, response.getData().getId());
    }

    @Test
    void getProduct_WhenNotExists_ShouldReturnError() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ApiResponse<ProductResponse> response = productService.getProduct(productId);

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        ApiResponse<List<ProductResponse>> response = productService.getAllProducts();

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void deductInventory_WhenStockAvailable_ShouldDeduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deductInventory(productId, 5);

        assertEquals(5, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deductInventory_WhenInsufficientStock_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.deductInventory(productId, 15));
    }

    @Test
    void restoreInventory_ShouldIncreaseStock() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        int initialStock = product.getStockQuantity();

        productService.restoreInventory(productId, 5);

        assertEquals(initialStock + 5, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void searchProducts_ShouldReturnResults() {
        ProductDocument doc = ProductDocument.builder().name("Laptop").build();
        org.springframework.data.elasticsearch.core.SearchHit<ProductDocument> hit = mock(org.springframework.data.elasticsearch.core.SearchHit.class);
        when(hit.getContent()).thenReturn(doc);
        
        org.springframework.data.elasticsearch.core.SearchHits<ProductDocument> hits = mock(org.springframework.data.elasticsearch.core.SearchHits.class);
        when(hits.stream()).thenReturn(java.util.stream.Stream.of(hit));
        when(hits.getTotalHits()).thenReturn(1L);
        
        when(elasticsearchOperations.search(any(org.springframework.data.elasticsearch.core.query.Query.class), eq(ProductDocument.class)))
                .thenReturn(hits);

        Pageable pageable = PageRequest.of(0, 10);
        ApiResponse<Page<ProductDocument>> response = productService.searchProducts("Laptop", null, null, null, "price_asc", pageable);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals("Laptop", response.getData().getContent().get(0).getName());
    }
}