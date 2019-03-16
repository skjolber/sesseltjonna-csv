package com.github.skjolber.stcsv.gtfs;

public class Trip {

	private String routeId;
	private String tripId;
	private String serviceId;
	private String tripHeadsign;
	private int directionId;
	private String shapeId;
	private int wheelchairAccessible;
	
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getTripId() {
		return tripId;
	}
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getTripHeadsign() {
		return tripHeadsign;
	}
	public void setTripHeadsign(String tripHeadsign) {
		this.tripHeadsign = tripHeadsign;
	}
	public int getDirectionId() {
		return directionId;
	}
	public void setDirectionId(int directionId) {
		this.directionId = directionId;
	}
	public String getShapeId() {
		return shapeId;
	}
	public void setShapeId(String shapeId) {
		this.shapeId = shapeId;
	}
	public int getWheelchairAccessible() {
		return wheelchairAccessible;
	}
	public void setWheelchairAccessible(int wheelchairAccessible) {
		this.wheelchairAccessible = wheelchairAccessible;
	}
	@Override
	public String toString() {
		return "Trip [routeId=" + routeId + ", tripId=" + tripId + ", serviceId=" + serviceId + ", tripHeadsign="
				+ tripHeadsign + ", directionId=" + directionId + ", shapeId=" + shapeId + ", wheelchairAccessible="
				+ wheelchairAccessible + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + directionId;
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
		result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
		result = prime * result + ((shapeId == null) ? 0 : shapeId.hashCode());
		result = prime * result + ((tripHeadsign == null) ? 0 : tripHeadsign.hashCode());
		result = prime * result + ((tripId == null) ? 0 : tripId.hashCode());
		result = prime * result + wheelchairAccessible;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trip other = (Trip) obj;
		if (directionId != other.directionId)
			return false;
		if (routeId == null) {
			if (other.routeId != null)
				return false;
		} else if (!routeId.equals(other.routeId))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (shapeId == null) {
			if (other.shapeId != null)
				return false;
		} else if (!shapeId.equals(other.shapeId))
			return false;
		if (tripHeadsign == null) {
			if (other.tripHeadsign != null)
				return false;
		} else if (!tripHeadsign.equals(other.tripHeadsign))
			return false;
		if (tripId == null) {
			if (other.tripId != null)
				return false;
		} else if (!tripId.equals(other.tripId))
			return false;
		if (wheelchairAccessible != other.wheelchairAccessible)
			return false;
		return true;
	}

	

	
	
}
