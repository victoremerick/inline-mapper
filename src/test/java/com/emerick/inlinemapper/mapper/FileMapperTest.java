package com.emerick.inlinemapper.mapper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileMapperTest {

    @Test
    public void mapsWildcardSectionBetweenAnchors() {
        List<String> lines = Arrays.asList(
                "HDR12345",
                "INFACME ",
                "DTL005",
                "DTL003",
                "TRL008",
                "FTRXYZ"
        );

        FileLayout layout = FileLayout.builder()
                .line("header", 1, HeaderLine.class)
                .line("info", 2, InfoLine.class)
                .wildcard("details", DetailLine.class)
                .line("trailer", -2, TrailerLine.class)
                .line("footer", -1, FooterLine.class)
                .build();

        FileMapper mapper = new FileMapper(layout);
        FileMappingResult result = mapper.map(lines);

        HeaderLine header = result.getSingle("header", HeaderLine.class);
        InfoLine info = result.getSingle("info", InfoLine.class);
        List<DetailLine> details = result.getList("details", DetailLine.class);
        TrailerLine trailer = result.getSingle("trailer", TrailerLine.class);
        FooterLine footer = result.getSingle("footer", FooterLine.class);

        assertEquals("12345", header.fileId);
        assertEquals("ACME", info.owner.trim());
        assertEquals(2, details.size());
        assertEquals(Integer.valueOf(5), details.get(0).quantity);
        assertEquals(Integer.valueOf(3), details.get(1).quantity);
        assertEquals(Integer.valueOf(8), trailer.declaredTotal);
        assertEquals("XYZ", footer.checksum);
    }

    @Test
    public void mapsFixedRangeAsList() {
        List<String> lines = Arrays.asList(
                "HDR99999",
                "DTL001",
                "DTL002",
                "DTL003",
                "FTREND"
        );

        FileLayout layout = FileLayout.builder()
                .line("header", 1, HeaderLine.class)
                .range("details", 2, 4, DetailLine.class)
                .line("footer", -1, FooterLine.class)
                .build();

        FileMapper mapper = new FileMapper(layout);
        FileMappingResult result = mapper.map(lines);

        HeaderLine header = result.getSingle("header", HeaderLine.class);
        List<DetailLine> details = result.getList("details", DetailLine.class);
        FooterLine footer = result.getSingle("footer", FooterLine.class);

        assertEquals("99999", header.fileId);
        assertEquals(3, details.size());
        assertEquals(Integer.valueOf(1), details.get(0).quantity);
        assertEquals(Integer.valueOf(3), details.get(2).quantity);
        assertEquals("END", footer.checksum);
        assertTrue(result.has("details"));
    }

    @Test
    public void buildsLayoutFromAnnotations() {
        List<String> lines = Arrays.asList(
                "HDR12345",
                "INFACME ",
                "DTL005",
                "DTL003",
                "TRL008",
                "FTRXYZ"
        );

        FileMapper mapper = new FileMapper(FileLayoutBuilder.fromAnnotations(AnnotatedFileDefinition.class));
        FileMappingResult result = mapper.map(lines);

        assertEquals("12345", result.getSingle("header", HeaderLine.class).fileId);
        assertEquals(2, result.getList("details", DetailLine.class).size());
        assertEquals(Integer.valueOf(8), result.getSingle("trailer", TrailerLine.class).declaredTotal);
    }
}
