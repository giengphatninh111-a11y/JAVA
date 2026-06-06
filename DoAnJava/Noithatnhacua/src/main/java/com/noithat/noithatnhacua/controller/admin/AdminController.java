package com.noithat.noithatnhacua.controller.admin;

import com.noithat.noithatnhacua.model.Category;
import com.noithat.noithatnhacua.model.Order;
import com.noithat.noithatnhacua.model.Product;
import com.noithat.noithatnhacua.model.User;
import com.noithat.noithatnhacua.repository.CategoryRepository;
import com.noithat.noithatnhacua.repository.OrderRepository;
import com.noithat.noithatnhacua.repository.ProductRepository;
import com.noithat.noithatnhacua.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // ==================== DASHBOARD ====================
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("revenueData", orderRepository.getMonthlyRevenue());
        return "admin/dashboard";
    }

    // ==================== SẢN PHẨM ====================
    @GetMapping("/products")
    public String products(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long categoryId,
                           Model model) {
        List<Product> products;

        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategory_Id(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            products = productRepository.findByCategory_Id(categoryId);
        } else {
            products = productRepository.findAll();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        if (product.getId() == null) {
            product.setIsActive(true);
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    // ==================== DANH MỤC ====================
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }

    // ==================== ĐƠN HÀNG ====================
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String status,
                         Model model) {
        List<Order> orders;

        if (keyword != null && !keyword.isEmpty() && status != null && !status.isEmpty()) {
            Order.Status orderStatus = Order.Status.valueOf(status);
            orders = orderRepository.findByKeywordAndStatus(keyword, orderStatus);
        } else if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.findByKeyword(keyword);
        } else if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatus(Order.Status.valueOf(status));
        } else {
            orders = orderRepository.findAll();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getCancelledByUser() != null && order.getCancelledByUser()) {
            return "redirect:/admin/orders";
        }

        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }

    // ==================== NGƯỜI DÙNG ====================
    @GetMapping("/users")
    public String users(@RequestParam(required = false) String keyword, Model model) {
        List<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
                    keyword, keyword, keyword);
        } else {
            users = userRepository.findAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }
}