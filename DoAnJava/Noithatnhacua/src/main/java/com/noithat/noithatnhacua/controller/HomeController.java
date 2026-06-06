package com.noithat.noithatnhacua.controller;

import com.noithat.noithatnhacua.model.CartItem;
import com.noithat.noithatnhacua.model.User;
import com.noithat.noithatnhacua.repository.CategoryRepository;
import com.noithat.noithatnhacua.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("categories", categoryRepository.findAll());
        // Lấy 12 sản phẩm có giá trên 1 triệu, mới nhất
        model.addAttribute("products", productRepository.findFeaturedProducts(
                new BigDecimal("1000000"), PageRequest.of(0, 12)));
        User loggedUser = (User) session.getAttribute("loggedUser");
        model.addAttribute("loggedUser", loggedUser);
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cartCount", cart != null ? cart.stream().mapToInt(CartItem::getQuantity).sum() : 0);
        return "index";
    }
}