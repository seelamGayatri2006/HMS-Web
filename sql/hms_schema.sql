CREATE DATABASE IF NOT EXISTS hms_db;
USE hms_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','DOCTOR','RECEPTIONIST') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    specialisation VARCHAR(100) NOT NULL,
    qualification VARCHAR(150),
    phone VARCHAR(15),
    email VARCHAR(100),
    consultation_fee DECIMAL(10,2) DEFAULT 500.00,
    available_days VARCHAR(100),
    available_from TIME,
    available_to TIME,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    dob DATE,
    gender ENUM('MALE','FEMALE','OTHER') NOT NULL,
    blood_group VARCHAR(5),
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(15),
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appt_date DATE NOT NULL,
    appt_time TIME NOT NULL,
    reason TEXT,
    status ENUM('SCHEDULED','COMPLETED','CANCELLED','NO_SHOW') DEFAULT 'SCHEDULED',
    notes TEXT,
    booked_by INT,
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (booked_by) REFERENCES users(user_id) ON DELETE SET NULL,
    UNIQUE KEY no_double_book (doctor_id,appt_date,appt_time)
);

CREATE TABLE IF NOT EXISTS bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT,
    patient_id INT NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    discount DECIMAL(10,2) DEFAULT 0.00,
    tax DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_status ENUM('PENDING','PAID','PARTIAL') DEFAULT 'PENDING',
    payment_method ENUM('CASH','CARD','UPI','INSURANCE') DEFAULT 'CASH',
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    generated_by INT,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bill_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    description VARCHAR(200) NOT NULL,
    category ENUM('CONSULTATION','TEST','MEDICINE','ROOM','OTHER') NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category ENUM('TABLET','CAPSULE','SYRUP','INJECTION','OINTMENT','OTHER') NOT NULL,
    manufacturer VARCHAR(100),
    unit_price DECIMAL(10,2) DEFAULT 0.00,
    stock_quantity INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    room_type ENUM('GENERAL','SEMI_PRIVATE','PRIVATE','ICU','EMERGENCY','OT') NOT NULL,
    floor INT DEFAULT 1,
    price_per_day DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('AVAILABLE','OCCUPIED','MAINTENANCE','RESERVED') DEFAULT 'AVAILABLE',
    current_patient_id INT,
    admitted_at TIMESTAMP NULL,
    notes TEXT,
    FOREIGN KEY (current_patient_id) REFERENCES patients(patient_id) ON DELETE SET NULL
);

INSERT IGNORE INTO users (username,password_hash,role,full_name,email) VALUES
('admin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ADMIN','System Administrator','admin@hms.com'),
('dr.sharma','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','DOCTOR','Dr. Priya Sharma','priya@hms.com'),
('reception','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','RECEPTIONIST','Front Desk','reception@hms.com');

INSERT IGNORE INTO doctors (user_id,full_name,specialisation,qualification,phone,consultation_fee,available_days,available_from,available_to) VALUES
(2,'Dr. Priya Sharma','Cardiology','MBBS, MD','9876543210',800.00,'MON,TUE,WED,THU,FRI','09:00:00','17:00:00');

INSERT IGNORE INTO rooms (room_number,room_type,floor,price_per_day) VALUES
('G-101','GENERAL',1,500),('G-102','GENERAL',1,500),('G-103','GENERAL',1,500),
('SP-201','SEMI_PRIVATE',2,1200),('SP-202','SEMI_PRIVATE',2,1200),
('P-301','PRIVATE',3,2500),('P-302','PRIVATE',3,2500),
('ICU-01','ICU',4,5000),('ICU-02','ICU',4,5000),
('EM-01','EMERGENCY',1,3000),('OT-01','OT',5,8000);

INSERT IGNORE INTO medicines (name,category,manufacturer,unit_price,stock_quantity,reorder_level,expiry_date) VALUES
('Paracetamol 500mg','TABLET','Sun Pharma',2.50,500,50,'2026-12-31'),
('Amoxicillin 250mg','CAPSULE','Cipla',8.00,200,30,'2026-06-30'),
('Cough Syrup 100ml','SYRUP','Dr Reddy',45.00,80,20,'2026-12-31'),
('Insulin Injection','INJECTION','Novo Nordisk',120.00,100,15,'2026-03-31'),
('Betadine Ointment','OINTMENT','Win Medicare',55.00,60,10,'2027-01-31');
