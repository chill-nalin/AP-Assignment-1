import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.models.*;
import vehicles.types.AirVehicle;
import vehicles.types.LandVehicle;
import vehicles.types.WaterVehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FleetManager {
    
    private ArrayList<Vehicle> fleet;

    public FleetManager() {
        fleet = new ArrayList<>();
    }
    
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (var i : fleet) {
            if (i.getId().equals(v.getId())) {
                throw new InvalidOperationException("The vehicle with same ID already exists");
            }
        }
        fleet.add(v);
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean found = false;
        Vehicle removed = null;
        for (var i : fleet) {
            if (i.getId().equals(id)) {
                found = true;
                removed = i;
            }
        }
        if (found) {
            fleet.remove(removed);
        }
        else {
            throw new InvalidOperationException("Vehicle ID not found.");
        }
    }

    public void startAllJourneys(double distance) {
        for (var v : fleet) {
            try {
                v.move(distance);
            }
            catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println();
    }

    public void refuelAll(double amount) {
        for (var v : fleet) {
            if (v instanceof FuelConsumable x) {
                try {
                    x.refuel(amount);
                }
                catch (InvalidOperationException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
        System.out.println("All vehicles refueled successfully");
    }

    public double getTotalFuelConsumption(double distance) {
        double sum = 0;
        for (var v : fleet) {
            if (v instanceof FuelConsumable x) {
                try {
                    double consumedFuel = x.consumeFuel(distance);
                    sum += consumedFuel;
                }
                catch (InsufficientFuelException e) {
                    System.out.println(e.getMessage() + " for vehicle ID: " + v.getId());
                }
            }
        }
        return Math.round(sum * 100.0) / 100.0;
    }

    public void maintainAll() {
        for (var v : fleet) {
            if (v instanceof Maintainable x) {
                x.scheduleMaintenance();
                x.performMaintenance();
            }
        }
    }

    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> returnedList = new ArrayList<>();

        for (var v : fleet) {
            if (type.isInstance(v)) {
                returnedList.add(v);
            }
        }
        return returnedList;
    }

    public void sortFleetByEfficiency() {
        fleet.sort(Collections.reverseOrder());
    }

    public String generateReport() {
        int totalCount = 0;
        int landVehicles = 0;
        int airVehicles = 0;
        int waterVehicles = 0;
        double totalMileage = 0;
        double totalEfficiency = 0;

        for (var i : fleet) {
            totalCount ++;
            totalMileage += i.getCurrentMileage();
            totalEfficiency += i.calculateFuelEfficiency();

            switch (i) {
                case LandVehicle landVehicle -> landVehicles++;
                case AirVehicle airVehicle -> airVehicles++;
                case WaterVehicle waterVehicle -> waterVehicles++;
                default -> {}
            }
        }

        StringBuilder result = new StringBuilder(String.format("Total count of vehicles: %d%nNumber of Land Vehicles: %d%nNumber of Air Vehicles: %d%nNumber of Water Vehicles: %d%nAverage Efficiency: %.2f%nTotal Mileage: %.2f%n", totalCount, landVehicles, airVehicles, waterVehicles, (totalEfficiency / totalCount), totalMileage));

        List<Vehicle> needsMaintenance = getVehiclesNeedingMaintenance();

        for (var i : needsMaintenance) {
            result.append(String.format("Vehicle ID %s needs maintenance.%n", i.getId()));
        }

        return result.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> result = new ArrayList<>();

        for (var i : fleet) {
            if (i instanceof Maintainable x) {
                if (x.needsMaintenance()) {
                    result.add(i);
                }
            }
        }

        return result;
    }

    // Persistence Methods
    public void saveToFile(String filename) {
        File file = new File(filename);
        boolean fileExists = file.exists();

        // Create file if it doesn't exist
        if (!fileExists) {
            try {
                file.createNewFile();
                System.out.println("Created file: " + filename);
            } catch (IOException e) {
                System.out.println("Unable to create the file.");
                return;
            }
            writeToFile(filename);
            System.out.println("Successfully added fleet to file: " + filename);
        }
        else {
            writeToFile(filename);
            System.out.println("Successfully added fleet to file: " + filename);
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            fleet.clear();
            String line;
            int lineNumber = 1;
            ArrayList<Integer> errorLines = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                Vehicle vehicle = createVehicle(line, lineNumber);
                if (vehicle != null) {
                    try {
                        addVehicle(vehicle);
                    } catch (InvalidOperationException e) {
                        System.out.println(e.getMessage() + " before line: " + lineNumber);
                        errorLines.add(lineNumber);
                    }
                }
                else {
                    errorLines.add(lineNumber);
                }
                lineNumber++;
            }
            System.out.println("Fleet loaded successfully from: " + filename);
            if (errorLines.size() == 1) {
                System.out.println("Unable to create Vehicle from line: ");
                System.out.println(errorLines.getFirst());
            }
            else if (errorLines.size() > 1) {
                System.out.println("Unable to create Vehicles from lines: ");
                for (int i = 0; i < errorLines.size(); i++) {
                    if (i == errorLines.size() - 1) {
                        System.out.printf("%d%n", errorLines.get(i));
                    }
                    else {
                        System.out.printf("%d, ", errorLines.get(i));
                    }
                }
            }

        }
        catch (IOException e) {
            System.out.println("Unable to load fleet from file: " + filename);
        }
    }

    // Helper Function for writing to a file
    private void writeToFile(String filename) {
        sortFleetByEfficiency();
        try (PrintWriter writer = new PrintWriter(filename)) {
            for (var v : fleet) {
                // Land Vehicles
                if (v instanceof Car x) {
                    //Car,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,FuelLevel
                    writer.printf("%s,%s,%s,%.2f,%.2f,%d,%d,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getPassengerCapacity(), x.getCurrentPassengers(), x.getFuelLevel());
                }
                else if (v instanceof Bus x) {
                    //Bus,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel
                    writer.printf("%s,%s,%s,%.2f,%.2f,%d,%d,%.2f,%.2f,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getPassengerCapacity(), x.getCurrentPassengers(), x.getCargoCapacity(), x.getCurrentCargo(), x.getFuelLevel());
                }
                else if (v instanceof Truck x) {
                    //Truck,ID,Model,MaxSpeed,CurrentMileage,CargoCapacity,CurrentCargo,FuelLevel
                    writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getCargoCapacity(), x.getCurrentCargo(), x.getFuelLevel());
                }
                // Air Vehicles
                else if (v instanceof Airplane x) {
                    //Airplane,ID,Model,MaxSpeed,CurrentMileage,MaxAltitude,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel
                    writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%d,%d,%.2f,%.2f,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getMaxAltitude(), x.getPassengerCapacity(), x.getCurrentPassengers(), x.getCargoCapacity(), x.getCurrentCargo(), x.getFuelLevel());
                }
                // Water Vehicles
                else if (v instanceof CargoShip x) {
                    if (!x.getHasSail()) {
                        //CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo,FuelLevel
                        writer.printf("%s,%s,%s,%.2f,%.2f,%B,%.2f,%.2f,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getHasSail(), x.getCargoCapacity(), x.getCurrentCargo(), x.getFuelLevel());
                    }
                    //CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo
                    writer.printf("%s,%s,%s,%.2f,%.2f,%B,%.2f,%.2f%n", x.getClass().getSimpleName(), x.getId(), x.getModel(), x.getMaxSpeed(), x.getCurrentMileage(), x.getHasSail(), x.getCargoCapacity(), x.getCurrentCargo());
                }
            }
        }
        catch (IOException e) {
            System.out.println("Unable to write to the file: " + filename);
        }
    }

    // Helper Function to parse lines from csv
    private static Vehicle createVehicle(String line, int lineNumber) {
        if (!line.contains(",")) {
            System.out.println("Incorrect Format for CSV at line: " + lineNumber);
            return null;
        }
        String[] fields = line.split(",");
        if (fields[0].equalsIgnoreCase("")) {
            System.out.println("No Vehicle type provided at line: " + lineNumber);
            return null;
        }
        if (fields[1].equalsIgnoreCase("")) {
            System.out.println("No ID provided at line: " + lineNumber);
            return null;
        }
        String type = fields[0].trim();

        switch (type) {
            //Car,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,FuelLevel
            case "Car" : {
                if (fields.length < 8) {
                    System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                    return null;
                }
                try {
                    try {
                        for (int i = 3; i < 8; i++) {
                            Double.parseDouble(fields[i].trim());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                        return null;
                    }
                    if (Integer.parseInt(fields[5]) != 5) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    return new Car(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Integer.parseInt(fields[6]), Double.parseDouble(fields[7]));
                }
                catch (OverloadException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            case "Bus" : {
                //Bus,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel
                if (fields.length < 10) {
                    System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                    return null;
                }
                try {
                    try {
                        for (int i = 3; i < 10; i++) {
                            Double.parseDouble(fields[i].trim());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                        return null;
                    }
                    if (Integer.parseInt(fields[5]) != 50) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    if (Double.parseDouble(fields[7]) != 500) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    return new Bus(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Integer.parseInt(fields[6]), Double.parseDouble(fields[8]), Double.parseDouble(fields[9]));
                }
                catch (OverloadException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            case "Truck" : {
                //Truck,ID,Model,MaxSpeed,CurrentMileage,CargoCapacity,CurrentCargo,FuelLevel
                if (fields.length < 8) {
                    System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                    return null;
                }
                try {
                    try {
                        for (int i = 3; i < 8; i++) {
                            Double.parseDouble(fields[i].trim());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                        return null;
                    }
                    if (Double.parseDouble(fields[5]) != 5000) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    return new Truck(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[6]), Double.parseDouble(fields[7]));
                } catch (OverloadException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            case "Airplane" : {
                //Airplane,ID,Model,MaxSpeed,CurrentMileage,MaxAltitude,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel
                if (fields.length < 11) {
                    System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                    return null;
                }
                try {
                    try {
                        for (int i = 3; i < 11; i++) {
                            Double.parseDouble(fields[i].trim());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                        return null;
                    }
                    if (Integer.parseInt(fields[6]) != 200) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    if (Double.parseDouble(fields[8]) != 10000) {
                        System.out.println("Defaults were changed at line: " + lineNumber);
                        return null;
                    }
                    return new Airplane(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[5]), Integer.parseInt(fields[7]), Double.parseDouble(fields[9]), Double.parseDouble(fields[10]));
                }
                catch (OverloadException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            case "CargoShip" : {
                //CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo
                boolean hasSail;
                if (fields[5].equalsIgnoreCase("true")) {
                    hasSail = true;
                }
                else if (fields[5].equalsIgnoreCase("false")) {
                    hasSail = false;
                }
                else {
                    System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                    return null;
                }
                if (hasSail) {
                    if (fields.length != 8) {
                        System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                        return null;
                    }
                    try {
                        try {
                            for (int i = 3; i < 8; i++) {
                                if (i == 5) {
                                    continue;
                                }
                                Double.parseDouble(fields[i].trim());
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                            return null;
                        }
                        if (Double.parseDouble(fields[6]) != 50000) {
                            System.out.println("Defaults were changed at line: " + lineNumber);
                            return null;
                        }
                        return new CargoShip(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), hasSail, Double.parseDouble(fields[7]));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
                else {
                    //CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo,FuelLevel
                    if (fields.length < 9) {
                        System.out.println("Incorrect Format for CSV at line: " + line);
                        return null;
                    }
                    try {
                        try {
                            for (int i = 3; i < 9; i++) {
                                if (i == 5) {
                                    continue;
                                }
                                Double.parseDouble(fields[i].trim());
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect Format for CSV at line: " + lineNumber);
                            return null;
                        }
                        if (Double.parseDouble(fields[6]) != 50000) {
                            System.out.println("Defaults were changed at line: " + lineNumber);
                            return null;
                        }
                        return new CargoShip(fields[1], fields[2], Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), hasSail, Double.parseDouble(fields[7]), Double.parseDouble(fields[8]));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage() + " at line: " + lineNumber);
                        return null;
                    }
                }
            }
            default: {
                System.out.println("Vehicle Type not found at line: " + lineNumber);
                return null;
            }
        }
    }
}