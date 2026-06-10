package com.noithat.noithatnhacua.repository;

import com.noithat.noithatnhacua.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // List (dùng cho admin)
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByIsActiveTrue();
    List<Product> findByNameContainingIgnoreCaseAndCategory_Id(String keyword, Long categoryId);

    // Page (dùng cho phân trang user)
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategory_Id(String keyword, Long categoryId, Pageable pageable);
    Page<Product> findByIsActiveTrueAndPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Product> findByIsActiveTrueAndPriceBetweenAndCategory_Id(BigDecimal minPrice, BigDecimal maxPrice, Long categoryId, Pageable pageable);

    // Featured products (trang chủ)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price > :minPrice ORDER BY p.id DESC")
    List<Product> findFeaturedProducts(@Param("minPrice") BigDecimal minPrice, Pageable pageable);

    // Lọc kết hợp keyword + danh mục + giá
    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND p.price >= :minPrice " +
            "AND p.price <= :maxPrice")
    Page<Product> findWithFilters(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}