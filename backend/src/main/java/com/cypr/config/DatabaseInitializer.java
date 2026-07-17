package com.cypr.config;

import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import com.cypr.modules.users.entity.Permission;
import com.cypr.modules.users.entity.Role;
import com.cypr.modules.users.repository.PermissionRepository;
import com.cypr.modules.users.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing and updating database schema...");

        // Fix 1: Ensure profile_pic_url is TEXT type
        try {
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN profile_pic_url TYPE TEXT");
            System.out.println("profile_pic_url column set to TEXT.");
        } catch (Exception e) {
            System.out.println("profile_pic_url migration skipped: " + e.getMessage());
        }

        // Fix 2: Add 'enabled' column if it doesn't exist (handles existing DB with rows)
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_name = 'users' AND column_name = 'enabled'",
                Integer.class
            );

            if (count == null || count == 0) {
                jdbcTemplate.execute(
                    "ALTER TABLE users ADD COLUMN enabled boolean NOT NULL DEFAULT false"
                );
                System.out.println("'enabled' column added to users table.");
            }

            int updated = jdbcTemplate.update(
                "UPDATE users SET enabled = true WHERE enabled = false AND created_at < NOW() - INTERVAL '1 minute'"
            );
            if (updated > 0) {
                System.out.println(updated + " pre-existing user(s) automatically enabled.");
            }
        } catch (Exception e) {
            System.err.println("'enabled' column migration failed: " + e.getMessage());
        }

        seedRolesAndPermissions();
        seedSuperAdmin();

        System.out.println("CYPR Backend is running...");
    }

    private void seedRolesAndPermissions() {
        System.out.println("Seeding enterprise roles and permissions...");

        // 1. Seed Permissions
        Permission readPrivilege = createPermissionIfNotFound("READ_PRIVILEGE", "Read access to standard resources");
        Permission writePrivilege = createPermissionIfNotFound("WRITE_PRIVILEGE", "Write access to standard resources");
        Permission deletePrivilege = createPermissionIfNotFound("DELETE_PRIVILEGE", "Delete access to standard resources");
        Permission adminPrivilege = createPermissionIfNotFound("ADMIN_PRIVILEGE", "Administrative full access");

        // 2. Seed Roles
        List<String> roleNames = Arrays.asList("SUPER_ADMIN", "ADMIN", "SUPPORT", "SECURITY", "FINANCE", "DEVELOPER", "VIEWER");

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role.setDescription(roleName + " Role");

                Set<Permission> permissions = new HashSet<>();
                permissions.add(readPrivilege); // Everyone gets read by default

                if ("SUPER_ADMIN".equals(roleName) || "ADMIN".equals(roleName)) {
                    permissions.add(writePrivilege);
                    permissions.add(deletePrivilege);
                    permissions.add(adminPrivilege);
                } else if ("SECURITY".equals(roleName) || "DEVELOPER".equals(roleName) || "FINANCE".equals(roleName)) {
                    permissions.add(writePrivilege);
                }

                role.setPermissions(permissions);
                roleRepository.save(role);
                System.out.println("Created Role: " + roleName);
            }
        }
    }

    private Permission createPermissionIfNotFound(String name, String module) {
        Permission permission = permissionRepository.findByName(name).orElse(null);
        if (permission == null) {
            permission = new Permission();
            permission.setName(name);
            permission.setModule(module);
            permission = permissionRepository.save(permission);
        }
        return permission;
    }

    private void seedSuperAdmin() {
        System.out.println("Seeding super admin user if none exists...");
        String adminEmail = "admin@cypr.com";
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElse(null);
        if (userRepository.findByEmailOrUsername(adminEmail, adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName("Vineet Upadhyay");
            admin.setEmail(adminEmail);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Password123!"));
            admin.setMobile("1234567890");
            admin.setBio("Super Administrator of CYPR Enterprise Control Panel");
            admin.setEnabled(true);
            admin.setSubscriptionType("PRO");
            
            if (superAdminRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(superAdminRole);
                admin.setRoles(roles);
            }
            
            userRepository.save(admin);
            System.out.println("Super admin user successfully seeded: " + adminEmail);
        }

        if (superAdminRole != null) {
            promoteConfiguredAdmin(adminEmail, superAdminRole);
            promoteConfiguredAdmin("vineetk5704@gmail.com", superAdminRole);
        }
    }

    private void promoteConfiguredAdmin(String email, Role superAdminRole) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return;
        }

        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()) || "ADMIN".equals(role.getName()));
        if (!hasAdminRole) {
            user.getRoles().add(superAdminRole);
            userRepository.save(user);
            System.out.println("Assigned SUPER_ADMIN role to configured admin: " + email);
        }
    }
}
