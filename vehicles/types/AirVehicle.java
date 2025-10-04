package vehicles.types;

import vehicles.Vehicle;

public abstract class AirVehicle extends Vehicle {

    private double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double currentMileage, double maxAltitude) {
        super(id, model, maxSpeed, currentMileage);
        this.maxAltitude = maxAltitude;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double time = distance / getMaxSpeed();
        return time * 0.95;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }
}