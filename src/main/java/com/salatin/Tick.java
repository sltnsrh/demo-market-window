package com.salatin;

public record Tick (
    String ticker,
    double bid,
    int bidVolume,
    double ask,
    int askVolume
) {}
