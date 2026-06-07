-- ================================================
-- Smart Rural Health Link - Database Setup
-- Run this in MySQL before starting the backend
-- ================================================

CREATE DATABASE IF NOT EXISTS rural_health_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rural_health_db;

-- The tables below are auto-created by Spring Boot (ddl-auto=update)
-- This file is for reference and manual setup if needed

-- Sample Admin user (password: admin123)
-- INSERT INTO users (full_name, mobile, email, password, role, enabled, created_at, updated_at)
-- VALUES ('Admin', '9999999999', 'admin@ruralhealth.in',
--         '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RRSHbCzmCCCHlzxO2',
--         'ADMIN', true, NOW(), NOW());

-- Sample Doctor (password: doctor123)
-- INSERT INTO users (full_name, mobile, email, password, role, enabled, created_at, updated_at)
-- VALUES ('Dr. Ramesh Kumar', '9876543210', 'ramesh@ruralhealth.in',
--         '$2a$10$slYQmyNdgTY18LonKleiWuGWI4lvFZP72dZtBK.sZr5OzJ6PH0/Dm',
--         'DOCTOR', true, NOW(), NOW());
