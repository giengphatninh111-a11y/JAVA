package com.noithat.noithatnhacua.controller;

import com.noithat.noithatnhacua.model.CartItem;
import com.noithat.noithatnhacua.model.Product;
import com.noithat.noithatnhacua.model.User;
import com.noithat.noithatnhacua.repository.CategoryRepository;
import com.noithat.noithatnhacua.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        Pageable pageable = PageRequest.of(page, 12);
        BigDecimal min = minPrice != null ? minPrice : BigDecimal.ZERO;
        BigDecimal max = maxPrice != null ? maxPrice : new BigDecimal("999999999999");

        Page<Product> productPage = productRepository.findWithFilters(
                keyword, categoryId, min, max, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("loggedUser", (User) session.getAttribute("loggedUser"));
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cartCount", cart != null ? cart.stream().mapToInt(CartItem::getQuantity).sum() : 0);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts",
                productRepository.findByCategory_Id(product.getCategory().getId()));
        model.addAttribute("loggedUser", (User) session.getAttribute("loggedUser"));
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cartCount", cart != null ? cart.stream().mapToInt(CartItem::getQuantity).sum() : 0);
        return "products/detail";
    }
}