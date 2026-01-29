package org.example.finder;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ArgsName {

    private final Map<String, String> values = new HashMap<>();
    private final Pattern pattern = Pattern.compile("^-[dnto]{1}=.+$");

    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

    public String get(String key) {
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            validate(arg);
            String[] strings = arg.split("=", 2);
            values.put(strings[0].substring(1), strings[1]);
        }
        if (!Set.of("d", "n", "t", "o").equals(values.keySet())) {
            throw new IllegalArgumentException("Заданы не все ключи. Вызовете программу с ключом -h для справки");
        }
        validateMask();
        validateSource();
    }

    private void validate(String arg) {
        if (!pattern.matcher(arg).matches()) {
            throw new IllegalArgumentException("Не правильно заданы аргументы. Вызовете программу с ключом -h для справки");
        }
    }

    private void validateSource() {
        String source = values.get("d");
        File dir = Paths.get(source).toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(String.format("Директория поиска: '%s' не найдена", source));
        }
    }

    private void validateMask() {
        String maskType = values.get("t");
        if (!List.of("mask", "name", "regex").contains(maskType)) {
            throw new IllegalArgumentException(String.format("Задано недопустимое значение %s аргумента t", maskType));
        }
    }
}
