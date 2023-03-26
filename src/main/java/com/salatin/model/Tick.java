package com.salatin.model;

import java.math.BigDecimal;

public record Tick (
    String ticker,
    BigDecimal bid,
    int bidVolume,
    BigDecimal ask,
    int askVolume
) {}
