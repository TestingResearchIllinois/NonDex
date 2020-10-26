package edu.illinois.nondex.common;

public enum Level {
    ALL(Integer.MIN_VALUE, "ALL"),
    FINEST(300, "FINEST"),
    FINER(400, "FINER"),
    FINE(500, "FINE"),
    CONFIG(700, "CONFIG"),
    INFO(800, "INFO"),
    WARNING(900, "WARNING"),
    SEVERE(1000, "SEVERE"),
    OFF(Integer.MAX_VALUE, "OFF");

    private final int severity;

    private Level(int severity, String name) {
        this.severity = severity;
    }

    public static Level parse(String name) {
        return Level.valueOf(name);
    }

    public final String getName() {
        return name();
    }

    public final int intValue() {
        return severity;
    }
}