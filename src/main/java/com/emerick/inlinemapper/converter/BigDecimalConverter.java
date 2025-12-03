package com.emerick.inlinemapper.converter;

import java.math.BigDecimal;

/**
 * Converter for BigDecimal values.
 */
public class BigDecimalConverter extends AbstractTypeConverter<BigDecimal> {
    @Override
    public BigDecimal fromString(String value) throws NumberFormatException {
        String trimmed = safeTrim(value);
        return trimmed != null ? new BigDecimal(trimmed) : null;
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }
}
