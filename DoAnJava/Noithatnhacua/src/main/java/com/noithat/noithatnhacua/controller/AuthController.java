package com.noithat.noithatnhacua.controller;

import com.noithat.noithatnhacua.model.User;
import com.noithat.noithatnhacua.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    // Trang đăng ký
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "auth/register";
        }
        user.setRole(User.Role.USER);
        userRepository.save(user);
        return "redirect:/login?registered";
    }

    // Trang đăng nhập
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) {
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "auth/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        jakarta.servlet.http.HttpSession session) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
            return "auth/login";
        }
        session.setAttribute("loggedUser", user);
        return "redirect:/";
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}