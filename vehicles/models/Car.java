package vehicles.models;

import exceptions.*;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.PassengerCarrier;
import vehicles.types.LandVehicle;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity = 5;
    private int currentPassengers;
    private boolean maintenanceNeeded;

    // Constructor for CLI
    public Car(String id, String model, double maxSpeed, double currentMileage, int currentPassengers) throws OverloadException{
        super(id, model, maxSpeed, currentMileage, 4);
        this.fuelLevel = 0;
        try {
            boardPassengers(currentPassengers);
        } catch (OverloadException e) {
            throw new OverloadException(e.getMessage() + " for Vehicle ID: " + id);
        }
        scheduleMaintenance();
    }

    // Constructor for CSV reading
    public Car(String id, String model, double maxSpeed, double currentMileage, int currentPassengers, double fuelLevel) throws OverloadException{
        this(id, model, maxSpeed, currentMileage, currentPassengers);
        this.fuelLevel = fuelLevel;
    }

    // Vehicle Methods
    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be less than 0");
        }
        try {
            consumeFuel(distance);
        } catch (InsufficientFuelException e) {
            throw new InvalidOperationException(e.getMessage() + " for vehicle ID: " + getId());
        }
        System.out.println("Driving on the road...");
        setCurrentMileage(getCurrentMileage() + distance);
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }

    // FuelConsumable Methods
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel Amount should be greater than 0");
        }
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        // If we have 2 L fuel then we can go 30 km distance
        double consumedFuel = distance / calculateFuelEfficiency();
        if (consumedFuel > getFuelLevel()) {
            throw new InsufficientFuelException("Not enough fuel");
        }
        fuelLevel -= consumedFuel;
        return consumedFuel;
    }

    // PassengerCarrier Methods
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (count > getPassengerCapacity()) {
            throw new OverloadException("Not enough Capacity");
        }
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > getCurrentPassengers()) {
            throw new InvalidOperationException("Not enough Passengers");
        }
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    // Maintainable Methods
    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = needsMaintenance();
    }

    @Override
    public boolean needsMaintenance() {
        if (getCurrentMileage() > 10000) {
            return true;
        }
        return false;
    }

    @Override
    public void performMaintenance() {
        if (maintenanceNeeded) {
            maintenanceNeeded = false;
            System.out.println("Maintenance Completed for vehicle ID: " + getId());
        }
        else {
            System.out.println("Maintenance not needed for vehicle ID: " + getId());
        }
    }
}
