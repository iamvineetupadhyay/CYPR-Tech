package com.cypr.modules.users.dto;

import java.util.UUID;

public class PermissionResponseDTO {

    private UUID id;
    private String name;
    private String module;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
