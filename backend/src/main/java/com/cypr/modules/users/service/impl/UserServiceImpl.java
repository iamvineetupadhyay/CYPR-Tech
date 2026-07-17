package com.cypr.modules.users.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.developer.dto.CreditResponseDTO;
import com.cypr.modules.developer.repository.CreditRepository;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;
import com.cypr.modules.security.entity.ActivityLog;
import com.cypr.modules.security.repository.ActivityLogRepository;
import com.cypr.modules.security.repository.DeviceRepository;
import com.cypr.modules.security.repository.SecurityLogRepository;
import com.cypr.modules.security.repository.SessionRepository;
import com.cypr.modules.users.dto.UserProfileResponseDTO;
import com.cypr.modules.users.dto.UserRequestDTO;
import com.cypr.modules.users.dto.UserResponseDTO;
import com.cypr.modules.users.dto.UserSearchCriteriaDTO;
import com.cypr.modules.users.entity.Role;
import com.cypr.modules.users.mapper.UserMapper;
import com.cypr.modules.users.repository.RoleRepository;
import com.cypr.modules.users.service.UserService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CreditRepository creditRepository;
    private final DeviceRepository deviceRepository;
    private final SessionRepository sessionRepository;
    private final SecurityLogRepository securityLogRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CreditRepository creditRepository,
            DeviceRepository deviceRepository,
            SessionRepository sessionRepository,
            SecurityLogRepository securityLogRepository,
            ActivityLogRepository activityLogRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.creditRepository = creditRepository;
        this.deviceRepository = deviceRepository;
        this.sessionRepository = sessionRepository;
        this.securityLogRepository = securityLogRepository;
        this.activityLogRepository = activityLogRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(UserSearchCriteriaDTO criteria, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria != null) {
                if (criteria.getSearch() != null && !criteria.getSearch().trim().isEmpty()) {
                    String search = "%" + criteria.getSearch().toLowerCase() + "%";
                    Predicate namePred = cb.like(cb.lower(root.get("name")), search);
                    Predicate emailPred = cb.like(cb.lower(root.get("email")), search);
                    Predicate usernamePred = cb.like(cb.lower(root.get("username")), search);
                    predicates.add(cb.or(namePred, emailPred, usernamePred));
                }

                if (criteria.getSubscriptionType() != null && !criteria.getSubscriptionType().trim().isEmpty()) {
                    predicates.add(cb.equal(root.get("subscriptionType"), criteria.getSubscriptionType()));
                }

                if (criteria.getEnabled() != null) {
                    predicates.add(cb.equal(root.get("enabled"), criteria.getEnabled()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable).map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return userMapper.toResponseDTO(findUserOrThrow(id));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        User user = new User();
        userMapper.updateEntityFromRequestDTO(requestDTO, user);
        
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        
        if (requestDTO.getRoleIds() != null && !requestDTO.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(requestDTO.getRoleIds()));
            user.setRoles(roles);
        }

        user = userRepository.save(user);

        logAudit("CREATE_USER", "Admin created user with ID: " + user.getId(), user);

        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = findUserOrThrow(id);
        
        userMapper.updateEntityFromRequestDTO(requestDTO, user);

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        if (requestDTO.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(requestDTO.getRoleIds()));
            user.setRoles(roles);
        }

        user = userRepository.save(user);

        logAudit("UPDATE_USER", "Admin updated user with ID: " + user.getId(), user);

        return userMapper.toResponseDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        user.setEnabled(false); // Soft disable
        userRepository.save(user);
        logAudit("DISABLE_USER", "Admin disabled user with ID: " + user.getId(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(Long id) {
        User user = findUserOrThrow(id);
        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        
        profile.setUser(userMapper.toResponseDTO(user));
        
        creditRepository.findByUser(user).ifPresent(credit -> {
            CreditResponseDTO creditDto = new CreditResponseDTO();
            creditDto.setId(credit.getId());
            creditDto.setBalance(credit.getBalance());
            profile.setCreditInfo(creditDto);
        });

        profile.setDevices(deviceRepository.findByUser(user, Pageable.ofSize(5))
                .stream()
                .map(d -> {
                    DeviceResponseDTO dDto = new DeviceResponseDTO();
                    dDto.setId(d.getId());
                    dDto.setOs(d.getOs());
                    dDto.setBrowser(d.getBrowser());
                    dDto.setFingerprint(d.getFingerprint());
                    dDto.setUserAgent(d.getUserAgent());
                    return dDto;
                }).collect(Collectors.toList()));

        profile.setSessions(sessionRepository.findByUser(user, Pageable.ofSize(5))
                .stream()
                .map(s -> {
                    SessionResponseDTO sDto = new SessionResponseDTO();
                    sDto.setId(s.getId());
                    sDto.setActive(s.isActive());
                    sDto.setToken(s.getToken());
                    sDto.setExpiresAt(s.getExpiresAt());
                    sDto.setIpAddress(s.getIpAddress());
                    sDto.setUserAgent(s.getUserAgent());
                    sDto.setLocation(s.getLocation());
                    sDto.setClientType(s.getClientType());
                    sDto.setLastActivityAt(s.getLastActivityAt());
                    return sDto;
                }).collect(Collectors.toList()));

        profile.setLoginHistory(securityLogRepository.findByUser(user, Pageable.ofSize(5))
                .stream()
                .map(log -> {
                    SecurityLogResponseDTO lDto = new SecurityLogResponseDTO();
                    lDto.setId(log.getId());
                    lDto.setEvent(log.getEvent());
                    lDto.setSeverity(log.getSeverity());
                    lDto.setIpAddress(log.getIpAddress());
                    return lDto;
                }).collect(Collectors.toList()));

        return profile;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportUsersToCsv(UserSearchCriteriaDTO criteria) {
        List<User> users = userRepository.findAll(
                (Specification<User>) (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (criteria != null) {
                        if (criteria.getSearch() != null && !criteria.getSearch().trim().isEmpty()) {
                            String search = "%" + criteria.getSearch().toLowerCase() + "%";
                            predicates.add(cb.or(
                                    cb.like(cb.lower(root.get("name")), search),
                                    cb.like(cb.lower(root.get("email")), search),
                                    cb.like(cb.lower(root.get("username")), search)
                            ));
                        }
                        if (criteria.getSubscriptionType() != null && !criteria.getSubscriptionType().trim().isEmpty()) {
                            predicates.add(cb.equal(root.get("subscriptionType"), criteria.getSubscriptionType()));
                        }
                        if (criteria.getEnabled() != null) {
                            predicates.add(cb.equal(root.get("enabled"), criteria.getEnabled()));
                        }
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                }
        );

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Name,Email,Username,Mobile,Enabled,Credits,SubscriptionType,CreatedAt\n");

        for (User user : users) {
            csvBuilder.append(user.getId()).append(",")
                    .append(escapeCsv(user.getName())).append(",")
                    .append(escapeCsv(user.getEmail())).append(",")
                    .append(escapeCsv(user.getUsername())).append(",")
                    .append(escapeCsv(user.getMobile())).append(",")
                    .append(user.isEnabled()).append(",")
                    .append(user.getCredits()).append(",")
                    .append(escapeCsv(user.getSubscriptionType())).append(",")
                    .append(user.getCreatedAt()).append("\n");
        }

        return csvBuilder.toString().getBytes();
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
    }

    private void logAudit(String action, String details, User targetUser) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setDetails(details);
        log.setEntityType("User");
        log.setEntityId(targetUser.getId() != null ? targetUser.getId().toString() : "NEW");
        log.setUser(targetUser); // Assigning to the target user since admin logic is separated for now
        activityLogRepository.save(log);
    }

    private String escapeCsv(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
