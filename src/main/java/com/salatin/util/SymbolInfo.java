package com.salatin.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class SymbolInfo {
    public static final Map<String, BigDecimal> TICK_MIN_SIZE = new HashMap<>(1);

    static {
        TICK_MIN_SIZE.put("BRENT", BigDecimal.valueOf(0.001));
    }

    private SymbolInfo() {}
}
