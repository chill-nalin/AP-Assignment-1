# Transportation Fleet Management System

**Author:** Nalin Gupta  
**Course:** CSE201 Advanced Programming

## Introduction

This project implements a Transport Fleet Management System in Java using principles of object-oriented programming. The program allows vehicles to be created, moved, maintained, and persisted in files. The solution emphasizes good object-oriented design and the use of Java features such as abstract classes and interfaces.

## Class Hierarchy

The system is organized around a root abstract class `Vehicle`, which defines common properties such as `id`, `model`, `maxSpeed`, and `currentMileage`. It also declares abstract methods for movement, fuel efficiency, and journey time estimation. The system also includes interfaces such as `FuelConsumable`, `PassengerCarrier`, `CargoCarrier`, and `Maintainable`, which define the functionality for the concrete classes. Concrete classes include `Car`, `Bus`, `Truck`, `Airplane`, and `CargoShip`. Custom exceptions include `InvalidOperationException`, `InsufficientFuelException`, and `OverloadException`.

## Application of OOP

### Inheritance

In this system, the abstract class `Vehicle` defines properties common to all vehicles, such as `id`, `model`, `maxSpeed`, and `currentMileage`. It also provides shared functionality, for example the `displayInfo()` method.

```java
public abstract class LandVehicle extends Vehicle {
    @Override
    public double estimateJourneyTime(double distance) {
        // Add 10% traffic delay
    }
}
```

`LandVehicle` overrides `estimateJourneyTime()` to add a 10% traffic delay, while keeping other functionality inherited from `Vehicle`.

### Polymorphism

The `FleetManager` stores vehicles in a `List<Vehicle>`. Even though the list is typed to the abstract `Vehicle`, the runtime type determines which implementation of methods such as `move()` or `calculateFuelEfficiency()` is executed.

```java
public class FleetManager {
    private ArrayList<Vehicle> fleet;
    
    public FleetManager() {
        fleet = new ArrayList<>();
    }
    
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        // Check for ID then add vehicle of any type to fleet array
    }
}
```

### Abstract Classes

The `Vehicle` class is declared abstract. It defines common fields and concrete methods (e.g., `displayInfo()`), but leaves type-specific behavior such as movement, fuel efficiency, and journey time estimation as abstract.

```java
public abstract class Vehicle {
    public abstract void move(double distance) throws InvalidOperationException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);
}
```

### Interfaces

The system defines multiple interfaces to model additional behaviors:

- **FuelConsumable**: methods for refueling and fuel tracking
- **PassengerCarrier**: methods for boarding and unboarding passengers
- **CargoCarrier**: methods for loading and unloading cargo
- **Maintainable**: methods for scheduling and performing maintenance

The concrete classes implement these interfaces and define their own functionality for each method.

```java
public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    // PassengerCarrier Methods
    @Override
    public void boardPassengers(int count) throws OverloadException {}
    public void disembarkPassengers(int count) throws InvalidOperationException {}
    public int getPassengerCapacity() {}
    public int getCurrentPassengers() {}
    // Similarly implement all methods from other Interfaces
}
```

## Project Structure

```
src/
├── exceptions/
│   ├── InsufficientFuelException.java
│   ├── InvalidOperationException.java
│   └── OverloadException.java
├── vehicles/
│   ├── interfaces/
│   │   ├── CargoCarrier.java
│   │   ├── FuelConsumable.java
│   │   ├── Maintainable.java
│   │   └── PassengerCarrier.java
│   ├── types/
│   │   ├── AirVehicle.java
│   │   ├── LandVehicle.java
│   │   └── WaterVehicle.java
│   ├── models/
│   │   ├── Airplane.java
│   │   ├── Bus.java
│   │   ├── Car.java
│   │   ├── CargoShip.java
│   │   └── Truck.java
│   └── Vehicle.java
├── FleetManager.java
└── Main.java
```

### Package Organization

- **exceptions/**: Contains all custom exception classes used throughout the system
- **vehicles/interfaces/**: Defines behavioral contracts for different vehicle capabilities
- **vehicles/types/**: Abstract classes representing vehicle categories (Land, Air, Water)
- **vehicles/models/**: Concrete vehicle implementations
- **Vehicle.java**: Root abstract class defining common vehicle properties and behaviors
- **FleetManager.java**: Core management class handling fleet operations and persistence
- **Main.java**: Entry point with CLI implementation and demo functionality

## Compilation and Execution

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Command line terminal/command prompt
- Ensure all Java files contain appropriate package declarations

### Compilation Instructions

Navigate to the `src` directory and execute the following command:

```bash
javac *.java */*.java */*/*.java
```

This command compiles all Java files in the current directory and all subdirectories recursively, handling the package structure automatically.

### Execution Instructions

After successful compilation, run the application from the `src` directory:

```bash
java Main
```

Alternatively, you can copy all the files into an IntelliJ IDEA project and run the `Main` class from there.

## Testing

This section outlines the testing methodology, assumptions made during implementation, capabilities, demo tests, and constraints.

### Assumptions

#### Land Vehicle Wheel Configuration

For land vehicles, the `numWheels` parameter has been standardized based on typical vehicle configurations:

- **Car class**: 4 wheels (standard passenger vehicle)
- **Bus class**: 8 wheels (typical city bus configuration)  
- **Truck class**: 8 wheels (standard commercial truck)

This assumption reduces the number of constructor arguments for commonly known vehicle attributes while maintaining realistic vehicle specifications.

#### CSV File Format Specifications

The persistence system operates with predefined CSV formats for each vehicle type:

| Vehicle Type | CSV Format |
|--------------|------------|
| Car | `Car,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,FuelLevel` |
| Bus | `Bus,ID,Model,MaxSpeed,CurrentMileage,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel` |
| Truck | `Truck,ID,Model,MaxSpeed,CurrentMileage,CargoCapacity,CurrentCargo,FuelLevel` |
| Airplane | `Airplane,ID,Model,MaxSpeed,CurrentMileage,MaxAltitude,PassengerCapacity,CurrentPassengers,CargoCapacity,CurrentCargo,FuelLevel` |
| CargoShip (fuel) | `CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo,FuelLevel` |
| CargoShip (sail) | `CargoShip,ID,Model,MaxSpeed,CurrentMileage,hasSail,CargoCapacity,CurrentCargo` |

#### Saving Fleet Ordered by Efficiency

When using **Menu Option 7**, the fleet is saved to CSV in **decreasing order of fuel efficiency**, with the most efficient vehicles appearing first in the file.

### System Capabilities

#### CSV File Validation and Error Detection

The CSV parsing system incorporates sophisticated validation mechanisms:

- **Data Type Validation**: Validates that numeric fields contain valid numerical values
- **Capacity Constraint Validation**: Verifies that vehicle capacities match predefined defaults:
  - Car passenger capacity: 5
  - Bus passenger capacity: 50, cargo capacity: 500 kg
  - Truck cargo capacity: 5000 kg
  - Airplane passenger capacity: 200, cargo capacity: 10000 kg
  - CargoShip cargo capacity: 50000 kg
- **Boolean Field Validation**: Strictly validates boolean values (`true`/`false` only)
- **Field Count Validation**: Verifies correct number of fields for each vehicle type
- **Vehicle Type Validation**: Ensures vehicle type corresponds to recognized classes

#### Error Reporting and Recovery

When malformed data is encountered, the system:
- Logs specific error messages with line numbers
- Continues processing subsequent lines
- Provides comprehensive error summaries

#### CLI Input Validation

- Numeric input validation with retry mechanisms
- Menu option validation with clear error messaging
- File operation error handling
- Exception propagation for all fleet operations

### Demo Tests

#### CSV File Error Handling Tests

| Test Case | Input Data | Error Type | Expected Output |
|-----------|------------|------------|-----------------|
| 01 | `Car,C001,Toyota,ABC,1500.00,5,2,45.50` | Data Type | "Incorrect Format for CSV at line: X" |
| 02 | `Bus,B001,Mercedes,120.00,2000.00,60,10,500.00,200.00,80.00` | Capacity Constraint | "Defaults were changed at line: X" |
| 03 | `CargoShip,CS001,Maersk,25.00,5000.00,maybe,50000.00,30000.00` | Boolean | "Incorrect Format for CSV at line: X" |
| 04 | `Car,C002,Honda,150.00` | Field Count | "Incorrect Format for CSV at line: X" |
| 05 | `Motorcycle,M001,Harley,180.00,100.00,2,1,40.00` | Invalid Type | "Vehicle Type not found at line: X" |

#### Working Test Scenario: Complete Fleet Management Workflow

**Sample Fleet Creation:**

```
Menu Option 1: Add Vehicle
Vehicle Type: car
ID: C001
Model: Toyota
Max Speed: 180.0
Current Mileage: 1500.0
Current Passengers: 3
```

**Journey Simulation:**

```
Menu Option 3: Start Journey
Distance: 500

Output:
Driving on road...
Hauling cargo...
Flying at 12000.0...
Sailing with cargo...
```

**Fleet Report:**

```
Menu Option 6: Generate Report

Output:
Total count of vehicles: 4
Number of Land Vehicles: 2
Number of Air Vehicles: 1
Number of Water Vehicles: 1
Average Efficiency: 7.80
Total Mileage: 133500.00
Vehicle ID A001 needs maintenance.
Vehicle ID CS001 needs maintenance.
```

#### Exception Handling Tests

**Overload Exception:**
```
Test: Create Car with 10 passengers (capacity = 5)
Output: "Not enough Capacity for Vehicle ID: C001"
```

**Invalid Operation:**
```
Test: Remove non-existent vehicle ID "X999"
Output: "Vehicle ID not found."
```

**Insufficient Fuel:**
```
Test: Journey with 10000 km without refueling
Output: "Insufficient fuel for vehicle ID: [ID]"
```

### System Constraints

- **Manual Data Loading**: Use Menu Option 8 before other operations if pre-existing data is required
- **CSV Format Dependency**: Files must strictly adhere to specified CSV format
- **Manual Data Saving**: Use Menu Option 7 before exit to persist changes
- **Default Fuel Level**: All vehicles start with fuel level = 0; refuel before journeys
- **Maintenance Flag Persistence**: `performMaintenance()` displays message but doesn't reset the flag
- **In-Console Reporting**: Reports display in console only; no file export supported

## Menu Options

1. **Add Vehicle** - Create new vehicles interactively
2. **Remove Vehicle** - Remove vehicles by ID
3. **Start Journey** - Simulate travel for all vehicles
4. **Refuel All** - Add fuel to all fuel-consumable vehicles
5. **Perform Maintenance** - Execute maintenance on vehicles needing it
6. **Generate Report** - Display comprehensive fleet statistics
7. **Save Fleet** - Export fleet data to CSV file
8. **Load Fleet** - Import fleet data from CSV file
9. **Search by Type** - Find vehicles by class/interface type
10. **List Vehicles Needing Maintenance** - Show vehicles requiring maintenance
11. **Exit** - Close the application

## References

- [ArrayList Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ArrayList.html)
- [Custom Exceptions](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Exception.html)
- [Regular Expressions](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html)
- [Enhanced Switch Expressions](https://docs.oracle.com/en/java/javase/17/language/switch-expressions.html)
- [InputMismatchException](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/InputMismatchException.html)
- [Scanner Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html)
- [String Methods](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html)
- [StringBuilder](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/StringBuilder.html)
- [Collections](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collections.html)
- [Comparable Interface](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Comparable.html)
