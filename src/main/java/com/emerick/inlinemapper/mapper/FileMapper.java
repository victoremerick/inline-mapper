package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.converter.TypeConverterRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Maps a full file into multiple line-based entities according to a {@link FileLayout}.
 */
public class FileMapper {
    private final FileLayout layout;
    private final TypeConverterRegistry converterRegistry;
    private final Map<Class<?>, LineMapper<?>> lineMappers = new HashMap<>();

    /**
     * Creates a mapper with default converter registry.
     *
     * @param layout file layout describing segments
     */
    public FileMapper(FileLayout layout) {
        this(layout, new TypeConverterRegistry());
    }

    /**
     * Creates a mapper with a custom converter registry.
     *
     * @param layout             file layout describing segments
     * @param converterRegistry  registry used for line mapping
     */
    public FileMapper(FileLayout layout, TypeConverterRegistry converterRegistry) {
        this.layout = Objects.requireNonNull(layout, "layout is required");
        this.converterRegistry = Objects.requireNonNull(converterRegistry, "converterRegistry is required");
    }

    /**
     * Splits content by line separators and maps the resulting lines.
     *
     * @param content full file content
     * @return mapping result
     */
    public FileMappingResult map(String content) {
        if (content == null) {
            throw new MapperException("Content cannot be null");
        }
        return map(Arrays.asList(content.split("\\R")));
    }

    /**
     * Maps the provided lines using the configured layout.
     *
     * @param lines file lines in order
     * @return mapping result
     */
    public FileMappingResult map(List<String> lines) {
        Objects.requireNonNull(lines, "lines are required");
        List<ResolvedSegment<?>> resolvedSegments = resolveSegments(lines);

        Map<String, Object> singles = new HashMap<>();
        Map<String, List<?>> lists = new HashMap<>();

        for (ResolvedSegment<?> resolved : resolvedSegments) {
            FileLayout.Segment<?> segment = resolved.segment;

            if (segment.isList()) {
                List<Object> mappedList = new ArrayList<>();
                if (resolved.startIndexInclusive <= resolved.endIndexInclusive) {
                    List<String> slice = lines.subList(resolved.startIndexInclusive - 1, resolved.endIndexInclusive);
                    for (String line : slice) {
                        mappedList.add(mapLine(segment.getType(), line));
                    }
                }
                lists.put(segment.getId(), mappedList);
            } else {
                String line = lines.get(resolved.startIndexInclusive - 1);
                Object mapped = mapLine(segment.getType(), line);
                singles.put(segment.getId(), mapped);
            }
        }

        return new FileMappingResult(singles, lists);
    }

    private <T> T mapLine(Class<T> type, String line) {
        @SuppressWarnings("unchecked")
        LineMapper<T> mapper = (LineMapper<T>) lineMappers.computeIfAbsent(type, key -> new PositionalLineMapper<>(key, converterRegistry));
        return mapper.toObject(line);
    }

    private List<ResolvedSegment<?>> resolveSegments(List<String> lines) {
        List<FileLayout.Segment<?>> rawSegments = layout.getSegments();
        if (rawSegments.isEmpty()) {
            throw new MapperException("Layout must contain at least one segment");
        }

        int totalLines = lines.size();
        int[] resolvedStarts = new int[rawSegments.size()];
        int[] resolvedEnds = new int[rawSegments.size()];
        Arrays.fill(resolvedStarts, -1);
        Arrays.fill(resolvedEnds, -1);

        // Resolve explicit positions (non-wildcard)
        for (int i = 0; i < rawSegments.size(); i++) {
            FileLayout.Segment<?> segment = rawSegments.get(i);
            if (segment.isWildcard()) {
                continue;
            }

            int start = segment.getStart().resolve(totalLines);
            int end = segment.getEnd().resolve(totalLines);

            if (start < 1 || end < 1 || start > totalLines || end > totalLines) {
                throw new MapperException("Segment '" + segment.getId() + "' is out of bounds for " + totalLines + " lines");
            }
            if (end < start) {
                throw new MapperException("Invalid range for segment '" + segment.getId() + "': end before start");
            }

            resolvedStarts[i] = start;
            resolvedEnds[i] = end;
        }

        List<ResolvedSegment<?>> resolvedSegments = new ArrayList<>();
        int previousEnd = 0;

        for (int i = 0; i < rawSegments.size(); i++) {
            FileLayout.Segment<?> segment = rawSegments.get(i);

            if (segment.isWildcard()) {
                // Wildcard consumes every line between the previous resolved end and the next resolved start
                int start = previousEnd + 1;
                int nextStart = findNextResolvedStart(rawSegments, resolvedStarts, i + 1);
                int end = nextStart == -1 ? totalLines : nextStart - 1;
                resolvedSegments.add(new ResolvedSegment<>(segment, start, end));
                previousEnd = Math.max(previousEnd, end);
                continue;
            }

            int start = resolvedStarts[i];
            int end = resolvedEnds[i];

            if (start <= previousEnd) {
                throw new MapperException("Layout segments overlap or are out of order near '" + segment.getId() + "'");
            }

            resolvedSegments.add(new ResolvedSegment<>(segment, start, end));
            previousEnd = end;
        }

        return resolvedSegments;
    }

    private int findNextResolvedStart(List<FileLayout.Segment<?>> segments, int[] resolvedStarts, int fromIndex) {
        for (int i = fromIndex; i < segments.size(); i++) {
            if (segments.get(i).isWildcard()) {
                continue;
            }
            return resolvedStarts[i];
        }
        return -1;
    }

    private static final class ResolvedSegment<T> {
        private final FileLayout.Segment<T> segment;
        private final int startIndexInclusive;
        private final int endIndexInclusive;

        ResolvedSegment(FileLayout.Segment<T> segment, int startIndexInclusive, int endIndexInclusive) {
            this.segment = segment;
            this.startIndexInclusive = startIndexInclusive;
            this.endIndexInclusive = endIndexInclusive;
        }
    }
}
