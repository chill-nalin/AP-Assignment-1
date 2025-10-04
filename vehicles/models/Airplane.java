package vehicles.models;

import exceptions.*;
import vehicles.interfaces.*;
import vehicles.types.AirVehicle;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity = 200;
    private int currentPassengers;
    private final double cargoCapacity = 10000;
    private double currentCargo;
    private boolean maintenanceNeeded;

    // Constructor for CLI
    public Airplane(String id, String model, double maxSpeed, double currentMileage, double maxAltitude, int currentPassengers, double currentCargo) throws OverloadException{
        super(id, model, maxSpeed, currentMileage, maxAltitude);
        this.fuelLevel = 0;
        try {
            boardPassengers(currentPassengers);
        } catch (OverloadException e) {
            throw new OverloadException(e.getMessage() + " for Vehicle ID: " + id);
        }
        try {
            loadCargo(currentCargo);
        } catch (OverloadException e) {
            throw new OverloadException(e.getMessage() + " for vehicle ID: " + id);
        }
        scheduleMaintenance();
    }

    // Constructor for CSV reading
    public Airplane(String id, String model, double maxSpeed, double currentMileage, double maxAltitude, int currentPassengers, double currentCargo, double fuelLevel) throws OverloadException{
        this(id, model, maxSpeed, currentMileage, maxAltitude, currentPassengers, currentCargo);
        this.fuelLevel = fuelLevel;
    }

    // AirVehicle Methods
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
        System.out.println("Flying at " + getMaxAltitude() + "...");
        setCurrentMileage(getCurrentMileage() + distance);
    }

    @Override
    public double calculateFuelEfficiency() {
        return 5.0;
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