package project.repository.specification.car;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import project.model.car.Car;
import project.repository.specification.SpecificationProvider;

@Component
public class CarDailyFeeInSpecification implements SpecificationProvider<Car> {
    private static final String FIELD_NAME = "dailyFee";
    private static final String FILTER = "dailyFeeIn";

    @Override
    public Specification<Car> getSpecification(String[] dailyFees) {
        return ((root, query, cb) -> {
            CriteriaBuilder.In<String> predicate = cb.in(root.get(FIELD_NAME));
            for (String value : dailyFees) {
                predicate.value(value);
            }
            return cb.and(predicate, predicate);
        });
    }

    @Override
    public String getFilter() {
        return FILTER;
    }
}
