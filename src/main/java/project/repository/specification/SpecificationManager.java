package project.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface SpecificationManager<T> {
    Specification<T> get(String filterKey, String[] params);

}
