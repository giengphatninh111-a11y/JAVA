package com.noithat.noithatnhacua.controller;

import com.noithat.noithatnhacua.model.*;
import com.noithat.noithatnhacua.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Xem giỏ hàng
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        BigDecimal total = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("loggedUser", (User) session.getAttribute("loggedUser"));
        model.addAttribute("cartError", session.getAttribute("cartError"));
        model.addAttribute("cartCount", cart.stream().mapToInt(CartItem::getQuantity).sum());
        session.removeAttribute("cartError");
        return "cart/cart";
    }

    // Thêm vào giỏ
    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return "redirect:/products";

        if (product.getStock() <= 0) {
            return "redirect:/products/" + productId;
        }

        List<CartItem> cart = getCart(session);
        CartItem existing = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst().orElse(null);

        if (existing != null) {
            if (existing.getQuantity() + quantity > product.getStock()) {
                session.setAttribute("cartError", "Số lượng vượt quá tồn kho! Chỉ còn " + product.getStock() + " sản phẩm.");
                return "redirect:/cart";
            }
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    quantity
            ));
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Cập nhật số lượng
    @PostMapping("/cart/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return "redirect:/cart";

        if (quantity > product.getStock()) {
            session.setAttribute("cartError", "Số lượng đặt vượt quá tồn kho! Chỉ còn " + product.getStock() + " sản phẩm.");
            return "redirect:/cart";
        }

        if (quantity < 1) return "redirect:/cart";

        List<CartItem> cart = getCart(session);
        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Xóa khỏi giỏ
    @GetMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Đặt hàng
    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String address,
                           @RequestParam String phone,
                           HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        BigDecimal total = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(loggedUser);
        order.setTotalPrice(total);
        order.setAddress(address);
        order.setPhone(phone);
        order.setStatus(Order.Status.PENDING);
        order.setCancelledByUser(false);
        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(Math.max(newStock, 0));
                productRepository.save(product);
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            orderDetailRepository.save(detail);
        }

        session.removeAttribute("cart");
        return "redirect:/profile?ordered";
    }

    // Trang profile
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model,
                          @RequestParam(required = false) String ordered) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("orders", orderRepository.findByUserOrderByCreatedAtDesc(loggedUser));
        model.addAttribute("ordered", ordered);
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cartCount", cart != null ? cart.stream().mapToInt(CartItem::getQuantity).sum() : 0);
        return "profile";
    }

    // Cập nhật thông tin đơn hàng
    @PostMapping("/orders/{id}/update")
    public String updateOrder(@PathVariable Long id,
                              @RequestParam String address,
                              @RequestParam String phone,
                              HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || !order.getUser().getId().equals(loggedUser.getId())) {
            return "redirect:/profile";
        }
        if (order.getStatus() != Order.Status.PENDING) {
            return "redirect:/profile";
        }
        order.setAddress(address);
        order.setPhone(phone);
        orderRepository.save(order);
        return "redirect:/profile";
    }

    // Huỷ đơn hàng — đánh dấu cancelledByUser = true
    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || !order.getUser().getId().equals(loggedUser.getId())) {
            return "redirect:/profile";
        }
        if (order.getStatus() == Order.Status.PENDING) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                if (product != null) {
                    product.setStock(product.getStock() + detail.getQuantity());
                    productRepository.save(product);
                }
            }
            order.setStatus(Order.Status.CANCELLED);
            order.setCancelledByUser(true); // đánh dấu user tự huỷ
            orderRepository.save(order);
        }
        return "redirect:/profile";
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}