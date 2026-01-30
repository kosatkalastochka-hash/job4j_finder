package org.example.finder;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {

    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (values.get(key) == null) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    private void validate(String arg) {
        switch (arg) {
            case String s when s.matches("^(?!-).*") -> throw new IllegalArgumentException(String.format("Error: "
                    + "This argument '%s' does not start with a '-' character", arg));
            case String s when s.matches("^[^=]*$") -> throw new IllegalArgumentException(String.format("Error: "
                    + "This argument '%s' does not contain an equal sign", arg));
            case String s when s.matches("^[^=]+=$") -> throw new IllegalArgumentException(String.format("Error: "
                    + "This argument '%s' does not contain a value", arg));
            case String s when s.matches("^-=.*") -> throw new IllegalArgumentException(String.format("Error: "
                    + "This argument '%s' does not contain a key", arg));
            default -> {
            }
        }
    }

    protected Map<String, String> getValues() {
        return values;
    }

    protected void parse(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String arg : args) {
            validate(arg);
            String[] strings = arg.split("=", 2);
            values.put(strings[0].substring(1), strings[1]);
        }
    }

    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }
}

