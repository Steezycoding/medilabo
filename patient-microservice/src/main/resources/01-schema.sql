CREATE TABLE IF NOT EXISTS patient
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    birth_date   DATE        NOT NULL,
    gender       CHAR(1)     NOT NULL,
    address      VARCHAR(255),
    phone_number VARCHAR(20)
);
