CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    timezone VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL
);


-- Insert 2 Teachers
INSERT INTO users (name, timezone, role) VALUES 
('Ananya Sharma', 'Asia/Kolkata', 'TEACHER'),
('David Smith', 'Europe/London', 'TEACHER');

-- Insert 4 Parents
INSERT INTO users (name, timezone, role) VALUES 
('Rahul Verma', 'Asia/Kolkata', 'PARENT'),
('Emily Johnson', 'America/New_York', 'PARENT'),
('Liam Davies', 'Europe/London', 'PARENT'),
('Akiko Tanaka', 'Asia/Tokyo', 'PARENT');