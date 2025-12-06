CREATE DATABASE presupex;

USE presupex;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL,
    type ENUM('income', 'expense') NOT NULL,
    description TEXT,
    date VARCHAR(20),
    image_url TEXT
);
