CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    country VARCHAR(255),
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    street VARCHAR(255),
    latitude INTEGER,
    longitude INTEGER,
    altitude DECIMAL(6,2),
    temperature DECIMAL(6,2)
);