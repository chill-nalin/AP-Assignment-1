package vehicles.types;

import vehicles.Vehicle;

public abstract class LandVehicle extends Vehicle {

    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, double currentMileage, int numWheels) {
        super(id, model, maxSpeed, currentMileage);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double time = distance / getMaxSpeed();
        return time * 1.1;
    }
}
