package com.salatin.model;

public record Tick (
    String ticker,
    double bid,
    int bidVolume,
    double ask,
    int askVolume
) {}
