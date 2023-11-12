package pl.aplazuk.cars.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car extends RepresentationModel<Car> {

    private long id;
    private String mark;
    private String color;
}
