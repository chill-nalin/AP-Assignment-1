package vehicles.interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;

public interface CargoCarrier {

    void loadCargo(double weight) throws OverloadException;

    void unloadCargo(double weight) throws InvalidOperationException;

    double getCargoCapacity();

    double getCurrentCargo();
}
