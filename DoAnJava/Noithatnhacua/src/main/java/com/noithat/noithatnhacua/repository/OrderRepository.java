package com.noithat.noithatnhacua.repository;

import com.noithat.noithatnhacua.model.Order;
import com.noithat.noithatnhacua.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT MONTH(o.createdAt), SUM(o.totalPrice) FROM Order o WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND o.status = 'DELIVERED' GROUP BY MONTH(o.createdAt) ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenue();

    // Tìm theo tên hoặc SĐT
    @Query("SELECT o FROM Order o WHERE o.user.fullName LIKE %:keyword% OR o.phone LIKE %:keyword%")
    List<Order> findByKeyword(@Param("keyword") String keyword);

    // Tìm theo tên/SĐT + trạng thái
    @Query("SELECT o FROM Order o WHERE (o.user.fullName LIKE %:keyword% OR o.phone LIKE %:keyword%) AND o.status = :status")
    List<Order> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Order.Status status);

    // Lọc theo trạng thái
    List<Order> findByStatus(Order.Status status);
}