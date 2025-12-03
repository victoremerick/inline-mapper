package com.emerick.inlinemapper.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Describes how a full file should be split into line-based entities.
 * Supports single line positions, fixed ranges, and a wildcard list section.
 */
public class FileLayout {
    private final List<Segment<?>> segments;
    private final boolean hasWildcard;

    private FileLayout(List<Segment<?>> segments, boolean hasWildcard) {
        this.segments = Collections.unmodifiableList(segments);
        this.hasWildcard = hasWildcard;
    }

    List<Segment<?>> getSegments() {
        return segments;
    }

    boolean hasWildcard() {
        return hasWildcard;
    }

    /**
     * Creates a new builder for file layouts.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder used to compose file layouts.
     */
    public static final class Builder {
        private final List<Segment<?>> segments = new ArrayList<>();
        private boolean hasWildcard;

        /**
         * Adds a single line position mapped to an entity.
         *
         * @param id       identifier used to retrieve the mapped instance
         * @param position 1-based line index. Negative values address lines from the end (-1 is the last line).
         * @param type     entity class for the line
         * @param <T>      entity type
         * @return the builder
         */
        public <T> Builder line(String id, int position, Class<T> type) {
            Objects.requireNonNull(type, "type is required");
            addSegment(id, new LinePosition(position), new LinePosition(position), type, false, false);
            return this;
        }

        /**
         * Adds a fixed range of lines mapped as a list of entities.
         *
         * @param id              identifier used to retrieve the mapped list
         * @param startInclusive  1-based starting line. Negative values address lines from the end.
         * @param endInclusive    1-based ending line. Negative values address lines from the end.
         * @param type            entity class for each line in the range
         * @param <T>             entity type
         * @return the builder
         */
        public <T> Builder range(String id, int startInclusive, int endInclusive, Class<T> type) {
            Objects.requireNonNull(type, "type is required");
            addSegment(id, new LinePosition(startInclusive), new LinePosition(endInclusive), type, true, false);
            return this;
        }

        /**
         * Adds a wildcard range that consumes every line between the previous and the next known segment.
         * Useful when the list size is unknown at design time.
         *
         * @param id   identifier used to retrieve the mapped list
         * @param type entity class for each line in the wildcard section
         * @param <T>  entity type
         * @return the builder
         */
        public <T> Builder wildcard(String id, Class<T> type) {
            Objects.requireNonNull(type, "type is required");
            if (hasWildcard) {
                throw new IllegalArgumentException("Only one wildcard section is supported per layout");
            }
            addSegment(id, null, null, type, true, true);
            hasWildcard = true;
            return this;
        }

        private <T> void addSegment(String id, LinePosition start, LinePosition end, Class<T> type, boolean list, boolean wildcard) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("id is required");
            }
            segments.add(new Segment<>(id, start, end, type, list, wildcard));
        }

        /**
         * Builds the layout definition.
         *
         * @return immutable layout definition
         */
        public FileLayout build() {
            return new FileLayout(new ArrayList<>(segments), hasWildcard);
        }
    }

    static final class Segment<T> {
        private final String id;
        private final LinePosition start;
        private final LinePosition end;
        private final Class<T> type;
        private final boolean list;
        private final boolean wildcard;

        Segment(String id, LinePosition start, LinePosition end, Class<T> type, boolean list, boolean wildcard) {
            if (!wildcard) {
                Objects.requireNonNull(start, "start is required");
                Objects.requireNonNull(end, "end is required");
            }
            this.id = id;
            this.start = start;
            this.end = end;
            this.type = type;
            this.list = list;
            this.wildcard = wildcard;
        }

        String getId() {
            return id;
        }

        LinePosition getStart() {
            return start;
        }

        LinePosition getEnd() {
            return end;
        }

        Class<T> getType() {
            return type;
        }

        boolean isList() {
            return list;
        }

        boolean isWildcard() {
            return wildcard;
        }
    }

    static final class LinePosition {
        private final int rawIndex;

        LinePosition(int rawIndex) {
            if (rawIndex == 0) {
                throw new IllegalArgumentException("Line index cannot be zero");
            }
            this.rawIndex = rawIndex;
        }

        int resolve(int totalLines) {
            if (rawIndex > 0) {
                return rawIndex;
            }
            int resolved = totalLines + rawIndex + 1;
            if (resolved < 1) {
                throw new IllegalArgumentException("Line index " + rawIndex + " is out of bounds for " + totalLines + " lines");
            }
            return resolved;
        }
    }
}
