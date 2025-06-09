-- Create airports table
CREATE TABLE airports (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    time_zone VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Create indexes for better performance
CREATE INDEX idx_airports_code ON airports(code);
CREATE INDEX idx_airports_city ON airports(city);
CREATE INDEX idx_airports_country ON airports(country);
CREATE INDEX idx_airports_active ON airports(active);

-- Add comments for documentation
COMMENT ON TABLE airports IS 'Airport information for the Travel360 system';
COMMENT ON COLUMN airports.code IS 'IATA airport code (3 characters)';
COMMENT ON COLUMN airports.latitude IS 'Airport latitude coordinate';
COMMENT ON COLUMN airports.longitude IS 'Airport longitude coordinate'; 