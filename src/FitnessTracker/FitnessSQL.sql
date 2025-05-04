-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS fitness_tracker;
USE fitness_tracker;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    weight DOUBLE NOT NULL
);

-- Create workout_log table
CREATE TABLE IF NOT EXISTS workout_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    workout_type VARCHAR(50) NOT NULL,
    calories DOUBLE DEFAULT 0,
    workout_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);

-- Create activity_log table
CREATE TABLE IF NOT EXISTS activity_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    steps INT NOT NULL,
    distance DOUBLE NOT NULL,
    activity_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);


USE fitness_tracker;

ALTER TABLE workout_log
ADD COLUMN calories DOUBLE DEFAULT 0;
SHOW COLUMNS FROM workout_log;
ALTER TABLE workout_log ADD COLUMN duration INT DEFAULT 0;

USE fitness_tracker;

SELECT * from users;
SELECT * from activity_log;
SELECT * from workout_log;
DELETE FROM users WHERE username = 'sid';
DELETE FROM users WHERE username = '123';