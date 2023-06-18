package project.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class RequestParamParser {
    public static List<Sort.Order> toSortOrders(String requestParam) {
        List<Sort.Order> orders = new ArrayList<>();
        if (requestParam.contains(":")) {
            String[] sortingFields = requestParam.split(";");
            for (String field : sortingFields) {
                Sort.Order order;
                if (field.contains(":")) {
                    String[] fieldAndDirection = field.split(":");
                    order = new Sort.Order(Sort.Direction.valueOf(fieldAndDirection[1]),
                            fieldAndDirection[0]);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, field);
                }
                orders.add(order);
            }
        } else {
            Sort.Order order = new Sort.Order(Sort.Direction.ASC, requestParam);
            orders.add(order);
        }
        return orders;
    }
}
