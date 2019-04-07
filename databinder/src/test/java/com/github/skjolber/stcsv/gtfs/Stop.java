package com.github.skjolber.stcsv.gtfs;

import java.util.HashSet;
import java.util.Set;


public class Stop {

    private static final int MISSING_VALUE = -999;

	private String id;
	
	private String name;
	
    private double lat;

    private double lon;

    private String code;

    private String desc;

    private String zoneId;

    private String url;

    private int locationType = 0;
    
    private int wheelchairBoarding = 0;

    private String direction;

    private String timezone;

    private int vehicleType = MISSING_VALUE;

    private String platformCode;
		
	private Stop parentStation;
	
	private Set<Stop> children;
	
	public Set<Stop> getChildren() {
		return children;
	}
	
	public boolean isChildren() {
		return children != null && !children.isEmpty();
	}
	
	public void setChildren(Set<Stop> children) {
		this.children = children;
	}
	
	public void addChild(Stop stop) {
		if(children == null) {
			children = new HashSet<>();
		}
		children.add(stop);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Stop getParentStation() {
		return parentStation;
	}
	
	public void setParentStation(Stop parentStation) {
		this.parentStation = parentStation;
	}

	public boolean isParentStation() {
		return parentStation != null;
	}

	
	
	@Override
	public String toString() {
		return "Stop [id=" + id + ", name=" + name + ", lat=" + lat + ", lon=" + lon + ", code=" + code + ", desc="
				+ desc + ", zoneId=" + zoneId + ", url=" + url + ", locationType=" + locationType
				+ ", wheelchairBoarding=" + wheelchairBoarding + ", direction=" + direction + ", timezone=" + timezone
				+ ", vehicleType=" + vehicleType + ", platformCode=" + platformCode + ", parentStation=" + parentStation
				+ ", children=" + children + "]";
	}

	public Stop top() {
		Stop toStop = this;
		while(toStop.isParentStation()) {
			toStop = toStop.getParentStation();
		}
		return toStop;
	}


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public void setWheelchairBoarding(int wheelchairBoarding) {
        this.wheelchairBoarding = wheelchairBoarding;
    }

    public int getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isVehicleTypeSet() {
        return vehicleType != MISSING_VALUE;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void clearVehicleType() {
        vehicleType = MISSING_VALUE;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

	public boolean hasParentStation() {
		return parentStation != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + locationType;
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentStation == null) ? 0 : parentStation.hashCode());
		result = prime * result + ((platformCode == null) ? 0 : platformCode.hashCode());
		result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + vehicleType;
		result = prime * result + wheelchairBoarding;
		result = prime * result + ((zoneId == null) ? 0 : zoneId.hashCode());
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
		Stop other = (Stop) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (locationType != other.locationType)
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentStation == null) {
			if (other.parentStation != null)
				return false;
		} else if (!parentStation.equals(other.parentStation))
			return false;
		if (platformCode == null) {
			if (other.platformCode != null)
				return false;
		} else if (!platformCode.equals(other.platformCode))
			return false;
		if (timezone == null) {
			if (other.timezone != null)
				return false;
		} else if (!timezone.equals(other.timezone))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (vehicleType != other.vehicleType)
			return false;
		if (wheelchairBoarding != other.wheelchairBoarding)
			return false;
		if (zoneId == null) {
			if (other.zoneId != null)
				return false;
		} else if (!zoneId.equals(other.zoneId))
			return false;
		return true;
	}
	
	
}
