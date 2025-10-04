package vehicles.models;

import exceptions.*;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.types.LandVehicle;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {

    private double fuelLevel;
    private final double cargoCapacity = 5000;
    private double currentCargo;
    private boolean maintenanceNeeded;

    // Constructor for CLI
    public Truck(String id, String model, double maxSpeed, double currentMileage, double currentCargo) throws OverloadException{
        super(id, model, maxSpeed, currentMileage, 8);
        this.fuelLevel = 0;
        try {
            loadCargo(currentCargo);
        } catch (OverloadException e) {
            throw new OverloadException(e.getMessage() + " for vehicle ID: " + id);
        }
        scheduleMaintenance();
    }

    // Constructor for CSV reading
    public Truck(String id, String model, double maxSpeed, double currentMileage, double currentCargo, double fuelLevel) throws OverloadException{
        this(id, model, maxSpeed, currentMileage, currentCargo);
        this.fuelLevel = fuelLevel;
    }

    // LandVehicle Methods
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
        System.out.println("Hauling Cargo...");
        setCurrentMileage(getCurrentMileage() + distance);
    }

    @Override
    public double calculateFuelEfficiency() {
        // Efficiency is reduced by 10% if CargoCapacity > 50%
        if (getCurrentCargo() > getCargoCapacity() / 2) {
            return 7.2;
        }
        return 8.0;
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
        double consumedFuel = distance / calculateFuelEfficiency();
        if (consumedFuel > getFuelLevel()) {
            throw new InsufficientFuelException("Not enough fuel");
        }
        fuelLevel -= consumedFuel;
        return consumedFuel;
    }

    // CargoCarrier Methods
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (weight > getCargoCapacity()) {
            throw new OverloadException("Weight exceeds the Capacity");
        }
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > getCurrentCargo()) {
            throw new InvalidOperationException("Not enough cargo");
        }
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }

    @Override
    public double getCurrentCargo() {
        return currentCargo;
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
