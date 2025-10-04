package vehicles.types;

import vehicles.Vehicle;

public abstract class WaterVehicle extends Vehicle {

    private boolean hasSail;

    public WaterVehicle(String id, String model, double maxSpeed, double currentMileage, boolean hasSail) {
        super(id, model, maxSpeed, currentMileage);
        this.hasSail = hasSail;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double time = distance / getMaxSpeed();
        return time * 1.15;
    }

    public boolean getHasSail() {
        return hasSail;
    }
}
