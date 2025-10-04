package vehicles.models;

import exceptions.*;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.types.WaterVehicle;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {

    public final double cargoCapacity = 50000;
    public double currentCargo;
    public boolean maintenanceNeeded;
    public double fuelLevel;

    // Constructor for CLI
    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail, double currentCargo) throws OverloadException{
        super(id, model, maxSpeed, currentMileage, hasSail);
        if (!hasSail) {
            fuelLevel = 0;
        }
        try {
            loadCargo(currentCargo);
        } catch (OverloadException e) {
            throw new OverloadException(e.getMessage() + " for vehicle ID: " + id);
        }
        scheduleMaintenance();
    }

    // Constructor for CSV reading (If hasSail = false)
    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail, double currentCargo, double fuelLevel) throws OverloadException{
        this(id, model, maxSpeed, currentMileage, hasSail, currentCargo);
        this.fuelLevel = fuelLevel;
    }

    // WaterVehicle Methods
    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be less than 0");
        }
        if (!getHasSail()) {
            try {
                consumeFuel(distance);
            } catch (InsufficientFuelException e) {
                throw new InvalidOperationException(e.getMessage() + " for vehicle ID: " + getId());
            }
        }
        System.out.println("Sailing with cargo...");
        setCurrentMileage(getCurrentMileage() + distance);
    }

    @Override
    public double calculateFuelEfficiency() {
        if (getHasSail()) {
            return 0.0;
        }
        return 4.0;
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

    // FuelConsumable Methods
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel Amount should be greater than 0");
        }
        if (!getHasSail()) {
            fuelLevel += amount;
        }
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (!getHasSail()) {
            double consumedFuel = distance / calculateFuelEfficiency();
            if (consumedFuel > getFuelLevel()) {
                throw new InsufficientFuelException("Not enough fuel");
            }
            fuelLevel -= consumedFuel;
            return consumedFuel;
        }
        return 0.0;
    }
}
