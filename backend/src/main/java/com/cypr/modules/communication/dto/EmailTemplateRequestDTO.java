package com.cypr.modules.communication.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class EmailTemplateRequestDTO {

    @NotBlank
    private String templateCode;

    @NotBlank
    private String subject;

    @NotBlank
    private String bodyHtml;

    private String variables;

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }
    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }
}
