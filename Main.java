import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import vehicles.Vehicle;
import vehicles.models.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FleetManager fleet = new FleetManager();

        // vehicles.csv contains some pre-defined vehicle data for the program.
        // Use Menu Option 8, then enter vehicles.csv if you want to use the data
        CLI(fleet);
    }

    private static void CLI(FleetManager fleet) {
        String menuString = """
                Available Options:
                0. Print Menu Options
                1. Add Vehicle
                2. Remove Vehicle
                3. Start Journey
                4. Refuel All
                5. Perform Maintenance
                6. Generate Report
                7. Save Fleet
                8. Load Fleet
                9. Search by Type
                10. List Vehicles Needing Maintenance
                11. Exit
                """;

        System.out.println(menuString);

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.matches("\\s*0\\s*")) {
                System.out.println(menuString);
            }
            else if (input.matches("\\s*1\\s*")) {
                System.out.println("Please enter the type of vehicle (Car/Bus/Truck/Airplane/CargoShip: ");

                String type = scanner.nextLine();
                type = type.toLowerCase().trim();

                ArrayList<String> data = new ArrayList<>(List.of(type));

                switch (type) {
                    case "car" -> {
                        getDetails(scanner, data);
                        getPassengers(scanner, data);
                    }
                    case "truck" -> {
                        getDetails(scanner, data);
                        getCargo(scanner, data);
                    }
                    case "bus" -> {
                        getDetails(scanner, data);
                        getPassengers(scanner, data);
                        getCargo(scanner, data);
                    }
                    case "cargoship" -> {
                        getDetails(scanner, data);
                        System.out.println("Does the ship have sail or not? (True/False): ");
                        String hasSail;
                        while (true) {
                            hasSail = scanner.nextLine();
                            if (hasSail.equalsIgnoreCase("true") || hasSail.equalsIgnoreCase("false")) {
                                break;
                            }
                            else {
                                System.out.println("Please enter True/False value");
                            }
                        }
                        data.add(hasSail);
                        getCargo(scanner, data);
                    }
                    case "airplane" -> {
                        getDetails(scanner, data);
                        System.out.println("Enter the Maximum Altitude: ");

                        double maxAltitude = getADoubleValue(scanner);

                        data.add(Double.toString(maxAltitude));
                        getPassengers(scanner, data);
                        getCargo(scanner, data);
                    }
                    default -> System.out.println("Please enter a supported vehicle name");
                }
                Vehicle newVehicle = createVehicle(data);
                if (newVehicle != null) {
                    addVehicle(fleet, newVehicle);
                    System.out.println("Vehicle added successfully: " + newVehicle.getId());
                }
            }
            else if (input.matches("\\s*2\\s*")) {
                System.out.println("Please enter Vehicle ID: ");
                String Id = scanner.nextLine();

                try {
                    fleet.removeVehicle(Id);
                } catch (InvalidOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (input.matches("\\s*3\\s*")) {
                System.out.println("Please enter the distance: ");
                double distance = getADoubleValue(scanner);
                fleet.startAllJourneys(distance);
            }
            else if (input.matches("\\s*4\\s*")) {
                System.out.println("Please enter the fuel amount: ");
                double fuelAmount = getADoubleValue(scanner);
                fleet.refuelAll(fuelAmount);
            }
            else if (input.matches("\\s*5\\s*")) {
                fleet.maintainAll();
            }
            else if (input.matches("\\s*6\\s*")) {
                System.out.println(fleet.generateReport());
            }
            else if (input.matches("\\s*7\\s*")) {
                System.out.println("Please enter the file name: ");
                String filename = scanner.nextLine();

                fleet.saveToFile(filename);
            }
            else if (input.matches("\\s*8\\s*")) {
                System.out.println("Please enter the file name: ");
                String filename = scanner.nextLine();

                fleet.loadFromFile(filename);
            }
            else if (input.matches("\\s*9\\s*")) {
                System.out.println("Please enter the vehicle type: (LandVehicle/AirVehicle/WaterVehicle/Car/Bus/Truck/Airplane/CargoShip");
                System.out.println("\t\t FuelConsumable/PassengerCarrier/CargoCarrier/Maintainable)");
                String type = scanner.nextLine();

                List<Vehicle> byType;
                Class<?> cl = null;

                if (type.matches("^(?i)\\s*(land\\s*vehicle|air\\s*vehicle|water\\s*vehicle)\\s*$")) {
                    try {
                        cl = Class.forName("vehicles.types." + getFinalClass(type));
                    } catch (ClassNotFoundException e) {
                        System.out.println("Please enter a valid class name");
                    }
                }
                else if (type.matches("^(?i)\\s*(car|bus|truck|air\\s*plane|cargo\\s*ship)\\s*$")) {
                    try {
                        cl = Class.forName("vehicles.models." + getFinalClass(type));
                    } catch (ClassNotFoundException e) {
                        System.out.println("Please enter a valid class name");
                    }
                }
                else if (type.matches("^(?i)\\s*(fuel\\s*consumable|passenger\\s*carrier|cargo\\s*carrier|maintainable)\\s*$")) {
                    try {
                        cl = Class.forName("vehicles.interfaces." + getFinalClass(type));
                    } catch (ClassNotFoundException e) {
                        System.out.println("Please enter a valid class name");
                    }
                }
                if (cl != null) {
                    byType = fleet.searchByType(cl);
                    if (!byType.isEmpty()) {
                        for (var v : byType) {
                            v.displayInfo();
                            System.out.println();
                        }
                    }
                    else {
                        System.out.println("No vehicles found for the given class");
                    }
                }
                else {
                    System.out.println("Please enter a valid class name");
                }
            }
            else if (input.matches("\\s*10\\s*")) {
                List<Vehicle> needsMaintenance = fleet.getVehiclesNeedingMaintenance();

                if (needsMaintenance.isEmpty()) {
                    System.out.println("None of the vehicles need maintenance.");
                }
                for (var i : needsMaintenance) {
                    System.out.printf("Vehicle ID %s needs maintenance.%n", i.getId());
                }
            }
            else if (input.matches("\\s*11\\s*")) {
                break;
            }
            else {
                System.out.println("Please enter a number between 0-11");
            }
        }
    }

    private static String getFinalClass(String input) {
        String finalClass = input.trim().replaceAll("\\s+", "").toLowerCase();
        return switch (finalClass) {
            case "landvehicle" -> "LandVehicle";
            case "airvehicle" -> "AirVehicle";
            case "watervehicle" -> "WaterVehicle";
            case "car" -> "Car";
            case "bus" -> "Bus";
            case "truck" -> "Truck";
            case "airplane" -> "Airplane";
            case "cargoship" -> "CargoShip";
            case "fuelconsumable" -> "FuelConsumable";
            case "passengercarrier"-> "PassengerCarrier";
            case "cargocarrier"-> "CargoCarrier";
            case "maintainable"-> "Maintainable";
            default -> null;
        };
    }

    private static double getADoubleValue(Scanner scanner) {
        double doubleVal;
        while (true) {
            try {
                doubleVal = scanner.nextDouble();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number value");
                scanner.nextLine();
            }
        }
        return doubleVal;
    }

    private static void getDetails(Scanner scanner, ArrayList<String> data) {
        System.out.println("Enter ID: ");
        String id;

        while (true) {
            id = scanner.nextLine();
            if (!id.equalsIgnoreCase("")) {
                break;
            }
            else {
                System.out.println("Please enter a valid ID");
            }
        }

        System.out.println("Enter Model: ");
        String model = scanner.nextLine();

        System.out.println("Enter Max Speed: ");
        double maxSpeed = getADoubleValue(scanner);

        System.out.println("Enter the current Mileage of the Vehicle: ");
        double currentMileage = getADoubleValue(scanner);
        data.addAll(List.of(id, model, Double.toString(maxSpeed), Double.toString(currentMileage)));
    }

    private static void getPassengers(Scanner scanner, ArrayList<String> data) {
        System.out.println("Enter current number of passengers");
        int currentPassengers;
        while (true) {
            try {
                currentPassengers = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number value");
                scanner.nextLine();
            }
        }
        data.add(Integer.toString(currentPassengers));
    }

    private static void getCargo(Scanner scanner, ArrayList<String> data) {
        System.out.println("Enter current load of cargo");
        double currentCargo = getADoubleValue(scanner);
        data.add(Double.toString(currentCargo));
    }

    private static void addVehicle(FleetManager fleet, Vehicle v) {
        try {
            fleet.addVehicle(v);
        }
        catch (InvalidOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Vehicle createVehicle(ArrayList<String> data) {
        // Car -> 6, Bus -> 7, Truck -> 6, Airplane -> 8, CargoShip -> 7
        switch (data.size()) {
            case 6 : {
                if (data.get(0).equalsIgnoreCase("car")) {
                    try {
                        return new Car(data.get(1), data.get(2), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Integer.parseInt(data.get(5)));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
                else if (data.get(0).equalsIgnoreCase("truck")) {
                    try {
                        return new Truck(data.get(1), data.get(2), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Double.parseDouble(data.get(5)));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
            }
            case 7 : {
                if (data.get(0).equalsIgnoreCase("bus")) {
                    try {
                        return new Bus(data.get(1), data.get(2), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Integer.parseInt(data.get(5)), Double.parseDouble(data.get(6)));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
                else if (data.get(0).equalsIgnoreCase("cargoship")) {
                    try {
                        return new CargoShip(data.get(1), data.get(2), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Boolean.parseBoolean(data.get(5)), Double.parseDouble(data.get(6)));
                    } catch (OverloadException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
            }
            case 8 : {
                try {
                    return new Airplane(data.get(1), data.get(2), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Double.parseDouble(data.get(5)), Integer.parseInt(data.get(6)), Double.parseDouble(data.get(7)));
                } catch (OverloadException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            default: {
                return null;
            }
        }
    }
}
