-- Create airplanes table
CREATE TABLE airplanes (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    size VARCHAR(20) NOT NULL CHECK (size IN ('SMALL', 'MEDIUM', 'LARGE')),
    first_class_capacity INTEGER NOT NULL CHECK (first_class_capacity >= 0),
    business_class_capacity INTEGER NOT NULL CHECK (business_class_capacity >= 0),
    economy_class_capacity INTEGER NOT NULL CHECK (economy_class_capacity >= 0),
    current_airport_id BIGINT,
    manufacturing_year INTEGER,
    max_range_km INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign key constraints
    CONSTRAINT fk_airplanes_current_airport 
        FOREIGN KEY (current_airport_id) REFERENCES airports(id)
);

-- Create indexes for better performance
CREATE INDEX idx_airplanes_registration_number ON airplanes(registration_number);
CREATE INDEX idx_airplanes_model ON airplanes(model);
CREATE INDEX idx_airplanes_size ON airplanes(size);
CREATE INDEX idx_airplanes_current_airport ON airplanes(current_airport_id);
CREATE INDEX idx_airplanes_active ON airplanes(active);

-- Add comments for documentation
COMMENT ON TABLE airplanes IS 'Airplane fleet information for the Travel360 system';
COMMENT ON COLUMN airplanes.registration_number IS 'Unique aircraft registration number';
COMMENT ON COLUMN airplanes.size IS 'Aircraft size category: SMALL, MEDIUM, or LARGE';
COMMENT ON COLUMN airplanes.current_airport_id IS 'Current location of the aircraft'; 