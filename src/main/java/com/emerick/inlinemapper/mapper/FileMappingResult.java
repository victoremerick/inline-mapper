package com.emerick.inlinemapper.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Holds the entities produced by {@link FileMapper}.
 */
public class FileMappingResult {
    private final Map<String, Object> singles;
    private final Map<String, List<?>> lists;

    FileMappingResult(Map<String, Object> singles, Map<String, List<?>> lists) {
        Map<String, Object> singlesCopy = new HashMap<>(singles);
        Map<String, List<?>> listsCopy = new HashMap<>();

        for (Map.Entry<String, List<?>> entry : lists.entrySet()) {
            listsCopy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }

        this.singles = Collections.unmodifiableMap(singlesCopy);
        this.lists = Collections.unmodifiableMap(listsCopy);
    }

    /**
     * Retrieves a single mapped entity by id.
     *
     * @param id   identifier provided in the layout
     * @param type expected type
     * @param <T>  entity type
     * @return mapped entity or null if not present
     */
    public <T> T getSingle(String id, Class<T> type) {
        Objects.requireNonNull(type, "type is required");
        Object value = singles.get(id);
        return value != null ? type.cast(value) : null;
    }

    /**
     * Retrieves a list of mapped entities by id.
     *
     * @param id          identifier provided in the layout
     * @param elementType expected element type
     * @param <T>         entity type
     * @return immutable list (empty when not present)
     */
    public <T> List<T> getList(String id, Class<T> elementType) {
        Objects.requireNonNull(elementType, "elementType is required");
        List<?> values = lists.get(id);
        if (values == null) {
            return Collections.emptyList();
        }

        List<T> typed = new ArrayList<>(values.size());
        for (Object value : values) {
            typed.add(elementType.cast(value));
        }
        return Collections.unmodifiableList(typed);
    }

    /**
     * Checks if any single or list entry exists for the id.
     *
     * @param id identifier provided in the layout
     * @return true when present
     */
    public boolean has(String id) {
        return singles.containsKey(id) || lists.containsKey(id);
    }

    /**
     * Returns all identifiers present in the result.
     *
     * @return set of ids
     */
    public Set<String> ids() {
        Set<String> keys = new HashSet<>();
        keys.addAll(singles.keySet());
        keys.addAll(lists.keySet());
        return Collections.unmodifiableSet(keys);
    }
}
