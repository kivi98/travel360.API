-- Insert sample users
INSERT INTO users (username, password, first_name, last_name, email, role, phone_number, created_at, updated_at, active) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldjoiKDpjIsIQSn0Daa', 'System', 'Administrator', 'admin@travel360.com', 'ADMINISTRATOR', '+1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('operator1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldjoiKDpjIsIQSn0Daa', 'John', 'Operator', 'operator@travel360.com', 'OPERATOR', '+1234567891', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('customer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldjoiKDpjIsIQSn0Daa', 'Jane', 'Customer', 'customer@travel360.com', 'CUSTOMER', '+1234567892', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- Insert sample airports
INSERT INTO airports (code, name, city, country, latitude, longitude, time_zone, created_at, updated_at, active) VALUES
('JFK', 'John F. Kennedy International Airport', 'New York', 'United States', 40.6413, -73.7781, 'America/New_York', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('LAX', 'Los Angeles International Airport', 'Los Angeles', 'United States', 33.9425, -118.4081, 'America/Los_Angeles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('LHR', 'London Heathrow Airport', 'London', 'United Kingdom', 51.4700, -0.4543, 'Europe/London', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('CDG', 'Charles de Gaulle Airport', 'Paris', 'France', 49.0097, 2.5479, 'Europe/Paris', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('NRT', 'Narita International Airport', 'Tokyo', 'Japan', 35.7720, 140.3929, 'Asia/Tokyo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- Insert sample airplanes
INSERT INTO airplanes (registration_number, model, size, first_class_capacity, business_class_capacity, economy_class_capacity, current_airport_id, manufacturing_year, max_range_km, created_at, updated_at, active) VALUES
('N12345', 'Boeing 777-300ER', 'LARGE', 8, 42, 264, 1, 2018, 13649, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('N67890', 'Airbus A320-200', 'MEDIUM', 0, 20, 150, 2, 2020, 6150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('G-ABCD', 'Boeing 787-9', 'LARGE', 6, 28, 246, 3, 2019, 14140, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('F-EFGH', 'Airbus A350-900', 'LARGE', 12, 48, 253, 4, 2021, 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('JA-IJKL', 'Boeing 737-800', 'MEDIUM', 0, 16, 162, 5, 2017, 5765, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- Insert sample flights
INSERT INTO flights (flight_number, airplane_id, origin_airport_id, destination_airport_id, departure_time, arrival_time, first_class_price, business_class_price, economy_class_price, first_class_available_seats, business_class_available_seats, economy_class_available_seats, distance_km, status, created_at, updated_at, active) VALUES
('T360001', 1, 1, 2, '2025-02-01 08:00:00', '2025-02-01 11:30:00', 2500.00, 1200.00, 450.00, 8, 42, 264, 3944, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('T360002', 2, 2, 1, '2025-02-01 14:00:00', '2025-02-01 22:15:00', 0.00, 800.00, 380.00, 0, 20, 150, 3944, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('T360003', 3, 1, 3, '2025-02-02 10:00:00', '2025-02-02 22:00:00', 3500.00, 1800.00, 650.00, 6, 28, 246, 5585, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('T360004', 4, 4, 5, '2025-02-03 15:30:00', '2025-02-04 09:45:00', 4200.00, 2100.00, 780.00, 12, 48, 253, 9714, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('T360005', 5, 5, 4, '2025-02-04 12:00:00', '2025-02-04 18:30:00', 0.00, 950.00, 420.00, 0, 16, 162, 9714, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- Add comments
COMMENT ON TABLE users IS 'Sample users: admin/password, operator1/password, customer1/password';
COMMENT ON TABLE airports IS 'Major international airports for testing';
COMMENT ON TABLE airplanes IS 'Sample aircraft fleet with different sizes and capacities';
COMMENT ON TABLE flights IS 'Sample flight schedules for testing booking functionality'; 