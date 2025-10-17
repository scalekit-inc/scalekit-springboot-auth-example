package com.example.scalekit.controller;

import com.example.scalekit.service.UserService;
import com.scalekit.grpc.scalekit.v1.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize,
            @RequestParam(value = "pageToken", defaultValue = "") String pageToken,
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model) {
        try {
            ListOrganizationUsersResponse response = userService.listOrganizationUsers(pageSize, pageToken);
            model.addAttribute("users", response.getUsersList());
            model.addAttribute("nextPageToken", response.getNextPageToken());
            model.addAttribute("hasNextPage", !response.getNextPageToken().isEmpty());
            model.addAttribute("currentUser", oidcUser);
            return "users";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load users: " + e.getMessage());
            return "users";
        }
    }

    @GetMapping("/{userId}")
    public String getUserDetails(@PathVariable String userId, Model model) {
        try {
            GetUserResponse response = userService.getUser(userId);
            model.addAttribute("user", response.getUser());
            return "user-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load user details: " + e.getMessage());
            return "user-detail";
        }
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createUser", CreateUser.newBuilder().build());
        return "user-create";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            RedirectAttributes redirectAttributes) {
        try {
            // Build CreateUser object
            CreateUser.Builder userBuilder = CreateUser.newBuilder()
                    .setEmail(email);
            
            // Build user profile if any profile data is provided
            if (name != null || firstName != null || lastName != null || phoneNumber != null) {
                CreateUserProfile.Builder profileBuilder = CreateUserProfile.newBuilder();
                
                if (name != null && !name.trim().isEmpty()) {
                    profileBuilder.setName(name);
                }
                if (firstName != null && !firstName.trim().isEmpty()) {
                    profileBuilder.setFirstName(firstName);
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                    profileBuilder.setLastName(lastName);
                }
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    profileBuilder.setPhoneNumber(phoneNumber);
                }
                
                userBuilder.setUserProfile(profileBuilder.build());
            }
            
            CreateUser createUser = userBuilder.build();
            CreateUserAndMembershipResponse response = userService.createUserAndMembership(createUser, true);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            return "redirect:/users/" + response.getUser().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create user: " + e.getMessage());
            return "redirect:/users/create";
        }
    }

    @PostMapping("/{userId}/update")
    public String updateUser(
            @PathVariable String userId,
            @ModelAttribute UpdateUser updateUser,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(userId, updateUser);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/users/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
            return "redirect:/users/" + userId;
        }
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
            return "redirect:/users/" + userId;
        }
    }

    @PostMapping("/{userId}/resend-invite")
    public String resendInvite(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {
        try {
            userService.resendInvite(userId);
            redirectAttributes.addFlashAttribute("success", "Invitation resent successfully");
            return "redirect:/users/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to resend invitation: " + e.getMessage());
            return "redirect:/users/" + userId;
        }
    }

    @PostMapping("/{userId}/delete-membership")
    public String deleteMembership(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {
        try {
            userService.deleteMembership(userId);
            redirectAttributes.addFlashAttribute("success", "Membership removed successfully");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove membership: " + e.getMessage());
            return "redirect:/users/" + userId;
        }
    }
}
