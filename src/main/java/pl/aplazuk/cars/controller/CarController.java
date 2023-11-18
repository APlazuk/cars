package pl.aplazuk.cars.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aplazuk.cars.model.Car;
import pl.aplazuk.cars.service.CarService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CollectionModel<Car>> getAllCars() {
        List<Car> allCars = carService.getAllCars();
        Link link = linkTo(CarController.class).withSelfRel();

        return createHypermediaResponse(allCars, link, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EntityModel<Car>> getCarById(@PathVariable long id) {
        Optional<Car> carById = carService.getCarById(id);
        Link link = linkTo(CarController.class).slash(id).withSelfRel();

        return carById.map(car -> createHypermediaResponse(car, link, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/color/{color}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CollectionModel<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> carsByColor = carService.getCarsByColor(color);
        Link link = linkTo(CarController.class).withSelfRel();

        return !carsByColor.isEmpty()
                ? createHypermediaResponse(carsByColor, link, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EntityModel<Car>> addCar(@RequestBody Car car) {
        boolean addCar = carService.addCar(car);
        Link link = linkTo(CarController.class).withSelfRel();

        return addCar ? createHypermediaResponse(car, link, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EntityModel<Car>> modCar(@RequestBody Car car) {
        boolean modCar = carService.modCar(car);
        Link link = linkTo(CarController.class).withSelfRel();

        return modCar ? createHypermediaResponse(car, link, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EntityModel<Car>> modCarById(@PathVariable long id, @RequestBody JsonPatch car) {
        Car modCarById = carService.modCarById(id, car);
        Link link = linkTo(CarController.class).slash(id).withSelfRel();

        return modCarById != null
                ? createHypermediaResponse(modCarById, link, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EntityModel<Car>> removeCar(@PathVariable long id) {
        Car removedCar = carService.removeCarById(id);
        Link link = linkTo(CarController.class).slash(id).withSelfRel();

        return removedCar != null
                ? createHypermediaResponse(removedCar, link, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<EntityModel<Car>> createHypermediaResponse(Car car, Link link, HttpStatus httpStatus) {
        EntityModel<Car> carEntityModel = EntityModel.of(car, link);
        return new ResponseEntity<>(carEntityModel, httpStatus);
    }

    private ResponseEntity<CollectionModel<Car>> createHypermediaResponse(List<Car> cars, Link link, HttpStatus httpStatus) {
        cars.forEach(car -> car.add(linkTo(CarController.class).slash(car.getId()).withSelfRel()));
        CollectionModel<Car> carCollectionModel = CollectionModel.of(cars, link);
        return new ResponseEntity<>(carCollectionModel, httpStatus);
    }

}
