package com.travel360.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.model.SeatClass;
import com.travel360.api.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void testSearchDirectFlights() throws Exception {
        // Prepare test data
        FlightSearchRequest searchRequest = new FlightSearchRequest();
        searchRequest.setOriginAirportId(1L);
        searchRequest.setDestinationAirportId(2L);
        searchRequest.setDepartureDate(LocalDate.now().plusDays(7));
        searchRequest.setSeatClass(SeatClass.ECONOMY_CLASS);

        List<FlightDto> mockFlights = Collections.singletonList(createMockFlightDto());

        // Mock service behavior
        when(flightService.searchDirectFlights(any(FlightSearchRequest.class))).thenReturn(mockFlights);

        // Perform the test
        mockMvc.perform(post("/api/flights/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("TL123"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser
    public void testSearchConnectingFlights() throws Exception {
        // Prepare test data
        FlightSearchRequest searchRequest = new FlightSearchRequest();
        searchRequest.setOriginAirportId(1L);
        searchRequest.setDestinationAirportId(3L);
        searchRequest.setDepartureDate(LocalDate.now().plusDays(7));
        searchRequest.setSeatClass(SeatClass.ECONOMY_CLASS);
        searchRequest.setIncludeTransits(true);

        FlightDto flight1 = createMockFlightDto();
        FlightDto flight2 = createMockFlightDto();
        flight2.setId(2L);
        flight2.setFlightNumber("TL456");

        List<List<FlightDto>> mockConnectingFlights = Collections.singletonList(Arrays.asList(flight1, flight2));

        // Mock service behavior
        when(flightService.searchConnectingFlights(any(FlightSearchRequest.class))).thenReturn(mockConnectingFlights);

        // Perform the test
        mockMvc.perform(post("/api/flights/search/connecting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0]", hasSize(2)))
                .andExpect(jsonPath("$.data[0][0].flightNumber").value("TL123"))
                .andExpect(jsonPath("$.data[0][1].flightNumber").value("TL456"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser
    public void testGetAllFlights() throws Exception {
        // Prepare mock data
        List<FlightDto> mockFlights = Arrays.asList(
                createMockFlightDto(),
                createAnotherMockFlightDto()
        );

        // Mock service behavior
        when(flightService.getAllFlights()).thenReturn(mockFlights);

        // Perform the test
        mockMvc.perform(get("/api/flights/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("TL123"))
                .andExpect(jsonPath("$.data[1].flightNumber").value("TL456"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    private FlightDto createMockFlightDto() {
        FlightDto flightDto = new FlightDto();
        flightDto.setId(1L);
        flightDto.setFlightNumber("TL123");
        
        AirportDto originAirport = new AirportDto();
        originAirport.setId(1L);
        originAirport.setCode("LHR");
        originAirport.setName("London Heathrow");
        originAirport.setCity("London");
        originAirport.setCountry("United Kingdom");
        
        AirportDto destinationAirport = new AirportDto();
        destinationAirport.setId(2L);
        destinationAirport.setCode("JFK");
        destinationAirport.setName("John F. Kennedy International");
        destinationAirport.setCity("New York");
        destinationAirport.setCountry("United States");
        
        flightDto.setOriginAirport(originAirport);
        flightDto.setDestinationAirport(destinationAirport);
        flightDto.setDepartureTime(LocalDateTime.now().plusDays(7));
        flightDto.setArrivalTime(LocalDateTime.now().plusDays(7).plusHours(8));
        flightDto.setFirstClassPrice(new BigDecimal("1299.99"));
        flightDto.setBusinessClassPrice(new BigDecimal("799.99"));
        flightDto.setEconomyClassPrice(new BigDecimal("399.99"));
        flightDto.setFirstClassAvailableSeats(10);
        flightDto.setBusinessClassAvailableSeats(20);
        flightDto.setEconomyClassAvailableSeats(150);
        flightDto.setStatus(FlightStatus.SCHEDULED);
        flightDto.setAirplaneModel("Boeing 777");
        flightDto.setAirplaneRegistration("TL-BOEING1");
        flightDto.setDistanceKm(5500);
        flightDto.setDurationMinutes(480);
        
        return flightDto;
    }
    
    private FlightDto createAnotherMockFlightDto() {
        FlightDto flightDto = new FlightDto();
        flightDto.setId(2L);
        flightDto.setFlightNumber("TL456");
        
        AirportDto originAirport = new AirportDto();
        originAirport.setId(2L);
        originAirport.setCode("JFK");
        originAirport.setName("John F. Kennedy International");
        originAirport.setCity("New York");
        originAirport.setCountry("United States");
        
        AirportDto destinationAirport = new AirportDto();
        destinationAirport.setId(3L);
        destinationAirport.setCode("LAX");
        destinationAirport.setName("Los Angeles International");
        destinationAirport.setCity("Los Angeles");
        destinationAirport.setCountry("United States");
        
        flightDto.setOriginAirport(originAirport);
        flightDto.setDestinationAirport(destinationAirport);
        flightDto.setDepartureTime(LocalDateTime.now().plusDays(8));
        flightDto.setArrivalTime(LocalDateTime.now().plusDays(8).plusHours(6));
        flightDto.setFirstClassPrice(new BigDecimal("999.99"));
        flightDto.setBusinessClassPrice(new BigDecimal("599.99"));
        flightDto.setEconomyClassPrice(new BigDecimal("299.99"));
        flightDto.setFirstClassAvailableSeats(8);
        flightDto.setBusinessClassAvailableSeats(15);
        flightDto.setEconomyClassAvailableSeats(120);
        flightDto.setStatus(FlightStatus.SCHEDULED);
        flightDto.setAirplaneModel("Airbus A320");
        flightDto.setAirplaneRegistration("TL-AIRBUS1");
        flightDto.setDistanceKm(4000);
        flightDto.setDurationMinutes(360);
        
        return flightDto;
    }
} 