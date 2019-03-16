package com.github.skjolber.stcsv.gtfs;

import java.util.ArrayList;
import java.util.List;

public class TripList {

	private List<Trip> trips = new ArrayList<>();

	public boolean add(Trip e) {
		return trips.add(e);
	}

}
