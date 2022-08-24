package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// int inHour = ticket.getInTime().getHours();
		// int outHour = ticket.getOutTime().getHours();

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		long duration = outHour - inHour;
		double durationMinut = duration / 1000 / 60;
		double durationHour = durationMinut / 60;
		// FIRST 30 Minutes for free
		if (durationMinut <= 30) {
			ticket.setPrice(0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			// case CAR: {
			// ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			case CAR: {
				ticket.setPrice(durationHour * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				// ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				ticket.setPrice(durationHour * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

	}
}