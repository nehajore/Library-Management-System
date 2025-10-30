-- This file is optional and can be used to initialize admin user
-- Note: For Spring Boot 3.x, use data.sql or implement a DataInitializer component
-- The password is 'admin123' encrypted with BCrypt

-- To use this, you would need to:
-- 1. Enable spring.jpa.defer-datasource-initialization=true in application.properties
-- 2. Or create a @Component class with @PostConstruct to initialize admin

-- Example BCrypt hash for 'admin123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- This is just an example - in production, generate a proper hash

