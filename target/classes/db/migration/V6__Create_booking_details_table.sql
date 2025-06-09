-- Create booking_details table
CREATE TABLE booking_details (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    passenger_name VARCHAR(200) NOT NULL,
    passport_number VARCHAR(50),
    seat_class VARCHAR(20) NOT NULL 
        CHECK (seat_class IN ('FIRST_CLASS', 'BUSINESS_CLASS', 'ECONOMY_CLASS')),
    seat_number VARCHAR(10),
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    checked_in BOOLEAN NOT NULL DEFAULT false,
    is_transit BOOLEAN NOT NULL DEFAULT false,
    special_requirements TEXT,
    connecting_booking_detail_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign key constraints
    CONSTRAINT fk_booking_details_booking 
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_details_flight 
        FOREIGN KEY (flight_id) REFERENCES flights(id),
    CONSTRAINT fk_booking_details_connecting 
        FOREIGN KEY (connecting_booking_detail_id) REFERENCES booking_details(id)
);

-- Create indexes for better performance
CREATE INDEX idx_booking_details_booking ON booking_details(booking_id);
CREATE INDEX idx_booking_details_flight ON booking_details(flight_id);
CREATE INDEX idx_booking_details_passenger_name ON booking_details(passenger_name);
CREATE INDEX idx_booking_details_passport_number ON booking_details(passport_number);
CREATE INDEX idx_booking_details_seat_class ON booking_details(seat_class);
CREATE INDEX idx_booking_details_checked_in ON booking_details(checked_in);
CREATE INDEX idx_booking_details_connecting ON booking_details(connecting_booking_detail_id);
CREATE INDEX idx_booking_details_active ON booking_details(active);

-- Composite indexes for common queries
CREATE INDEX idx_booking_details_flight_seat ON booking_details(flight_id, seat_number);
CREATE INDEX idx_booking_details_booking_passenger ON booking_details(booking_id, passenger_name);

-- Add comments for documentation
COMMENT ON TABLE booking_details IS 'Individual passenger details for flight bookings';
COMMENT ON COLUMN booking_details.seat_class IS 'Seat class: FIRST_CLASS, BUSINESS_CLASS, or ECONOMY_CLASS';
COMMENT ON COLUMN booking_details.checked_in IS 'Whether passenger has checked in for the flight';
COMMENT ON COLUMN booking_details.is_transit IS 'Whether this is a transit/connecting flight';
COMMENT ON COLUMN booking_details.connecting_booking_detail_id IS 'Reference to connecting flight booking detail'; 