package com.cypr.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PermissionRequestDTO {

    @NotBlank(message = "Permission name is required")
    @Size(max = 50, message = "Permission name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Module is required")
    @Size(max = 50, message = "Module name must not exceed 50 characters")
    private String module;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
