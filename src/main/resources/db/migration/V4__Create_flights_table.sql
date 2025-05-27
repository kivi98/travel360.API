-- Create flights table
CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL UNIQUE,
    airplane_id BIGINT NOT NULL,
    origin_airport_id BIGINT NOT NULL,
    destination_airport_id BIGINT NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    first_class_price DECIMAL(10,2) NOT NULL CHECK (first_class_price >= 0),
    business_class_price DECIMAL(10,2) NOT NULL CHECK (business_class_price >= 0),
    economy_class_price DECIMAL(10,2) NOT NULL CHECK (economy_class_price >= 0),
    first_class_available_seats INTEGER NOT NULL DEFAULT 0,
    business_class_available_seats INTEGER NOT NULL DEFAULT 0,
    economy_class_available_seats INTEGER NOT NULL DEFAULT 0,
    distance_km INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' 
        CHECK (status IN ('SCHEDULED', 'BOARDING', 'DEPARTED', 'IN_AIR', 'LANDED', 'DELAYED', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign key constraints
    CONSTRAINT fk_flights_airplane 
        FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    CONSTRAINT fk_flights_origin_airport 
        FOREIGN KEY (origin_airport_id) REFERENCES airports(id),
    CONSTRAINT fk_flights_destination_airport 
        FOREIGN KEY (destination_airport_id) REFERENCES airports(id),
    
    -- Business logic constraints
    CONSTRAINT chk_flights_different_airports 
        CHECK (origin_airport_id != destination_airport_id),
    CONSTRAINT chk_flights_departure_before_arrival 
        CHECK (departure_time < arrival_time)
);

-- Create indexes for better performance
CREATE INDEX idx_flights_flight_number ON flights(flight_number);
CREATE INDEX idx_flights_airplane ON flights(airplane_id);
CREATE INDEX idx_flights_origin_airport ON flights(origin_airport_id);
CREATE INDEX idx_flights_destination_airport ON flights(destination_airport_id);
CREATE INDEX idx_flights_departure_time ON flights(departure_time);
CREATE INDEX idx_flights_arrival_time ON flights(arrival_time);
CREATE INDEX idx_flights_status ON flights(status);
CREATE INDEX idx_flights_active ON flights(active);

-- Composite indexes for common queries
CREATE INDEX idx_flights_route_date ON flights(origin_airport_id, destination_airport_id, departure_time);
CREATE INDEX idx_flights_airplane_date ON flights(airplane_id, departure_time);

-- Add comments for documentation
COMMENT ON TABLE flights IS 'Flight schedules and information for the Travel360 system';
COMMENT ON COLUMN flights.flight_number IS 'Unique flight identifier';
COMMENT ON COLUMN flights.status IS 'Current flight status';
COMMENT ON COLUMN flights.distance_km IS 'Flight distance in kilometers'; 