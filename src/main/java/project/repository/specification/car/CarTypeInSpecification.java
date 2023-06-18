package project.repository.specification.car;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import project.model.car.Car;
import project.model.car.CarType;
import project.repository.specification.SpecificationProvider;

@Component
public class CarTypeInSpecification implements SpecificationProvider<Car> {
    private static final String FIELD_NAME = "carType";
    private static final String FILTER = "carTypeIn";

    @Override
    public Specification<Car> getSpecification(String[] carTypes) {
        return ((root, query, cb) -> {
            CriteriaBuilder.In<CarType> predicate = cb.in(root.get(FIELD_NAME));
            for (String value : carTypes) {
                predicate.value(CarType.valueOf(value));
            }
            return cb.and(predicate);
        });
    }

    @Override
    public String getFilter() {
        return FILTER;
    }
}
