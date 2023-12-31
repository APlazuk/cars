package pl.aplazuk.cars.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.aplazuk.cars.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarService {

    private List<Car> carList;

    public CarService() {
        this.carList = new ArrayList<>();
        carList.add(new Car(1L, "VW", "blue"));
        carList.add(new Car(2L, "BMW", "black"));
        carList.add(new Car(3L, "AUDI", "blue"));
        carList.add(new Car(4L, "VOLVO", "pink"));
        carList.add(new Car(5L, "MERCEDES", "yellow"));
    }

    public List<Car> getAllCars() {
        return carList;
    }

    public Optional<Car> getCarById(long id) {
        return carList.stream().filter(car -> car.getId() == id).findFirst();
    }

    public List<Car> getCarsByColor(String color) {
        return carList.stream().filter(car -> color.equals(car.getColor())).collect(Collectors.toList());
    }

    public boolean addCar(Car car) {
        return carList.add(car);
    }

    public boolean modCar(Car newCar) {
        Optional<Car> carById = getCarById(newCar.getId());
        if (carById.isPresent()) {
            carList.remove(carById.get());
            return carList.add(newCar);
        }
        return false;
    }

    public Car modCarById(long id, JsonPatch patch) {
        Optional<Car> carById = getCarById(id);

        try {
            if (carById.isPresent()) {
               return applyPatchToCar(patch, carById.get());
            }
        } catch (JsonPatchException | JsonProcessingException e) {
            log.error("Car with id: {} cannot be modified", id);
        }
        return null;
    }

    public Car removeCarById(long id) {
        Optional<Car> carById = getCarById(id);
        if (carById.isPresent()){
            carList.remove(carById.get());
            return carById.get();
        }
        return null;
    }

    private Car applyPatchToCar(JsonPatch patch, Car car) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patchedCar = patch.apply(objectMapper.convertValue(car, JsonNode.class));
        return objectMapper.treeToValue(patchedCar, Car.class);
    }
}
