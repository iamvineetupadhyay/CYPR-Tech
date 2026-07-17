package com.cypr.modules.users.dto;

import com.cypr.modules.developer.dto.CreditResponseDTO;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;

import java.util.List;

public class UserProfileResponseDTO {

    private UserResponseDTO user;
    private CreditResponseDTO creditInfo;
    private List<DeviceResponseDTO> devices;
    private List<SessionResponseDTO> sessions;
    private List<SecurityLogResponseDTO> loginHistory;

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public CreditResponseDTO getCreditInfo() {
        return creditInfo;
    }

    public void setCreditInfo(CreditResponseDTO creditInfo) {
        this.creditInfo = creditInfo;
    }

    public List<DeviceResponseDTO> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceResponseDTO> devices) {
        this.devices = devices;
    }

    public List<SessionResponseDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionResponseDTO> sessions) {
        this.sessions = sessions;
    }

    public List<SecurityLogResponseDTO> getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(List<SecurityLogResponseDTO> loginHistory) {
        this.loginHistory = loginHistory;
    }
}
