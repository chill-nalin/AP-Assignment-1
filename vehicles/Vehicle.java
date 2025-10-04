package vehicles;

import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {

    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed, double currentMileage) {
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = currentMileage;
    }

    public abstract void move(double distance) throws InvalidOperationException;

    public abstract double calculateFuelEfficiency();

    public abstract double estimateJourneyTime(double distance);

    public void displayInfo(){
        System.out.println("Vehicle ID: " + id);
        System.out.println("Vehicle Model: " + model);
        System.out.println("Vehicle Max Speed: " + maxSpeed);
        System.out.println("Vehicle Current Mileage: " + currentMileage);
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public String getId() {
        return id;
    }

    public void setCurrentMileage(double currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getModel() {
        return model;
    }

    @Override
    public int compareTo(Vehicle o) {
        return Double.compare(this.calculateFuelEfficiency(), o.calculateFuelEfficiency());
    }
}
