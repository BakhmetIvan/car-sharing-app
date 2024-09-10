package com.carsharingapp.controller;

import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.dto.user.UserUpdateDto;
import com.carsharingapp.dto.user.UserUpdateRoleDto;
import com.carsharingapp.model.User;
import com.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "User profile controller", description = "Endpoints for profile operations")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me")
    @Operation(summary = "Get user info",
            description = "Get info about user from db")
    public UserResponseDto getInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role",
            description = "Allows admins update user role")
    public UserResponseDto updateRole(@PathVariable @Positive Long id,
                                      @RequestBody @Valid UserUpdateRoleDto updateRoleDto) {
        return userService.updateRole(id, updateRoleDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/me")
    @Operation(summary = "Update user info",
            description = "Allows users update their info")
    public UserResponseDto updateInfo(Authentication authentication,
                                      @Valid @RequestBody UserUpdateDto updateDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user, updateDto);
    }
}
