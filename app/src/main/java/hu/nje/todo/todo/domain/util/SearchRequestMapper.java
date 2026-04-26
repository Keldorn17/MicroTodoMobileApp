package hu.nje.todo.todo.domain.util;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

import hu.nje.todo.todo.domain.model.SearchRequest;

@UtilityClass
public class SearchRequestMapper {

    public static Map<String, String> toMap(SearchRequest request) {
        Map<String, String> map = new HashMap<>();
        if (request.getSearch() != null && !request.getSearch().isEmpty()) {
            map.put("search", request.getSearch());
        }
        if (request.getSort() != null) {
            map.put("sort", request.getSort());
        }
        if (request.getPageNumber() != null) {
            map.put("pageNumber", String.valueOf(request.getPageNumber()));
        }
        if (request.getPageSize() != null) {
            map.put("pageSize", String.valueOf(request.getPageSize()));
        }
        if (request.getQueryMode() != null) {
            map.put("mode", request.getQueryMode().name());
        }
        return map;
    }

}
