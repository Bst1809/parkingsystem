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
		double durationMinute = duration / 1000 / 60;
		double durationHour = durationMinute / 60;
		// FIRST 30 Minutes for free
		if (durationMinute <= 30) {
			ticket.setPrice(0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				if (ticket.getRegCount() > 0) {
					ticket.setPrice(durationHour * Fare.CAR_RATE_PER_HOUR * 0.95);
				} else {
					ticket.setPrice(durationHour * Fare.CAR_RATE_PER_HOUR);
				}
				break;
			}
			case BIKE: {
				if (ticket.getRegCount() > 0) {
					ticket.setPrice(durationHour * Fare.BIKE_RATE_PER_HOUR * 0.95);
				} else {
					ticket.setPrice(durationHour * Fare.BIKE_RATE_PER_HOUR);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

	}
}