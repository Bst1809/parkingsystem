package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability

		// THEN
		assertNotNull(ticket);
		assertFalse(ticket.getParkingSpot().isAvailable());

	}

	@Test
	public void testParkingLotExit() {
		// testParkingACar(); // FIRST rule => Test must be isolated

		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		Date inTime = new Date();
		try {
			Thread.sleep(1000); // Car waits for 1 sec until it comes out
		} catch (InterruptedException ie) {

		}

		// WHEN
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		int regCount = ticketDAO.getRegCount("ABCDEF");
		long outTime = ticket.getOutTime().getTime();

		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		// THEN
		assertEquals(0, ticket.getPrice()); // Vehicle goes out of parking after 1sec, fare should be 0 as it's less
											// than 30minutes
		assertThat(outTime).isCloseTo((inTime.getTime() + 1000), within(1000L)); // Vehicle goes out of parking after
																					// 1sec, outTime should be equals to
																					// inTime +1sec
		assertEquals(1, regCount); // Verify regCount was populated
	}

}
