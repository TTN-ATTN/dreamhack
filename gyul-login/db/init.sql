DROP DATABASE IF EXISTS chall;
CREATE DATABASE IF NOT EXISTS chall;
USE chall;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(100)
);

INSERT INTO users (username,password) VALUES ('Steve','Steve');
INSERT INTO users (username,password) VALUES ('Alex','Alex');
INSERT INTO users (username,password) VALUES ('Rootsquare','FAKEPASSWORD');
INSERT INTO users (username,password) VALUES ('admin','FAKEPASSWORD');