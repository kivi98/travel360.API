-- Create bookings table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_reference VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED' 
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign key constraints
    CONSTRAINT fk_bookings_user 
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_created_by 
        FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_bookings_booking_reference ON bookings(booking_reference);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_booking_date ON bookings(booking_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_by ON bookings(created_by);
CREATE INDEX idx_bookings_active ON bookings(active);

-- Add comments for documentation
COMMENT ON TABLE bookings IS 'Flight bookings for the Travel360 system';
COMMENT ON COLUMN bookings.booking_reference IS 'Unique booking reference number';
COMMENT ON COLUMN bookings.status IS 'Booking status: PENDING, CONFIRMED, CANCELLED, or COMPLETED';
COMMENT ON COLUMN bookings.created_by IS 'User who created the booking (for operator bookings)'; 