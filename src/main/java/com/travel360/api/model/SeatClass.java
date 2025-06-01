package com.travel360.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SeatClass {
    FIRST_CLASS("FIRST_CLASS", "FIRST"),
    BUSINESS_CLASS("BUSINESS_CLASS", "BUSINESS"),
    ECONOMY_CLASS("ECONOMY_CLASS", "ECONOMY");

    private final String value;
    private final String alias;

    SeatClass(String value, String alias) {
        this.value = value;
        this.alias = alias;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getAlias() {
        return alias;
    }

    @JsonCreator
    public static SeatClass fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        String upperText = text.trim().toUpperCase();
        
        for (SeatClass seatClass : SeatClass.values()) {
            // Check exact match with enum name
            if (seatClass.name().equals(upperText)) {
                return seatClass;
            }
            // Check match with value
            if (seatClass.value.equals(upperText)) {
                return seatClass;
            }
            // Check match with alias
            if (seatClass.alias.equals(upperText)) {
                return seatClass;
            }
        }
        
        throw new IllegalArgumentException("Invalid seat class: " + text + 
            ". Valid values are: FIRST_CLASS, BUSINESS_CLASS, ECONOMY_CLASS (or their aliases: FIRST, BUSINESS, ECONOMY)");
    }

    @Override
    public String toString() {
        return value;
    }
} 