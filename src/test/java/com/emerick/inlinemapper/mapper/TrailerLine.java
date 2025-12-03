package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.Column;
import com.emerick.inlinemapper.annotation.LineEntity;

@LineEntity
public class TrailerLine {
    @Column(position = 0, length = 3)
    public String recordType;

    @Column(position = 3, length = 3)
    public Integer declaredTotal;
}
