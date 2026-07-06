package com.cypr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        // We do this manually because Hibernate's ddl-auto=update can't add NOT NULL columns
        // without a default on a non-empty table.
        try {
            // Check if column already exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_name = 'users' AND column_name = 'enabled'",
                Integer.class
            );

            if (count == null || count == 0) {
                // Column does not exist — add it with a default so existing rows get FALSE
                jdbcTemplate.execute(
                    "ALTER TABLE users ADD COLUMN enabled boolean NOT NULL DEFAULT false"
                );
                System.out.println("'enabled' column added to users table.");
            }

            // Enable all pre-existing users (they were registered before email verification existed)
            int updated = jdbcTemplate.update(
                "UPDATE users SET enabled = true WHERE enabled = false AND created_at < NOW() - INTERVAL '1 minute'"
            );
            if (updated > 0) {
                System.out.println(updated + " pre-existing user(s) automatically enabled.");
            }
        } catch (Exception e) {
            System.err.println("'enabled' column migration failed: " + e.getMessage());
        }

        System.out.println("CYPR Backend is running...");
    }
}
