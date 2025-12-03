package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.FileLayout;
import com.emerick.inlinemapper.annotation.FileSegment;

import java.util.List;

@FileLayout
public class AnnotatedFileDefinition {
    @FileSegment(position = 1)
    HeaderLine header;

    @FileSegment(position = 2)
    InfoLine info;

    @FileSegment(wildcard = true)
    List<DetailLine> details;

    @FileSegment(position = -2)
    TrailerLine trailer;

    @FileSegment(position = -1)
    FooterLine footer;
}
