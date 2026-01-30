package org.example.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

public class Finder {

    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);


    public static void main(String[] args) throws IOException {
        if (List.of(args).contains("-h")) {
            printHelp();
            return;
        }
        try {
            ArgsName argParams = ArgsName.of(args);
            validate(argParams.getValues());
            LOG.info("d {},n {},t {},o {}", argParams.get("d"), argParams.get("n"), argParams.get("t"), argParams.get("o"));
            PathMatcher matcher = getPathMatcher(argParams);
            LOG.debug("Параметры успешно валидированы");
            Visitor visitor = new Visitor(matcher);
            Files.walkFileTree(Path.of(argParams.get("d")), visitor);
            writeToFile(visitor, argParams.get("o"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    private static PathMatcher getPathMatcher(ArgsName argParams) {
        try {
            String pattern = switch (argParams.get("t")) {
                case "regex" -> String.format("regex:%s", argParams.get("n"));
                case "name" -> String.format("glob:**%s", argParams.get("n"));
                case "mask" -> String.format("glob:%s", argParams.get("n"));
                default -> "";
            };
            return FileSystems.getDefault().getPathMatcher(pattern);
        } catch (UnsupportedOperationException | PatternSyntaxException e) {
            LOG.error("Выражение {} не является регулярным выражением или маской для файла", argParams.get("n"));
            LOG.error(e.getMessage(), e);
            throw new IllegalArgumentException(("Аргумент n указан не верно. " +
                    "Выражение %s не является регулярным выражением или маской для файла").formatted(argParams.get("n")));
        }
    }

    private static void writeToFile(Visitor visitor, String targetParam) {
        if (!visitor.getPaths().isEmpty()) {
            Path target = Paths.get(targetParam);
            try (Writer writer = new FileWriter(target.toFile())) {
                visitor.getPaths().forEach(s -> {
                    try {
                        writer.write(s);
                        writer.write(System.lineSeparator());
                    } catch (IOException e) {
                        System.out.printf("Ошибка записи. Данные не были записаны в файл %s", target);
                        LOG.error("Ошибка записи в файл {}", target);
                        LOG.error(e.getMessage(), e);
                    }
                });
            } catch (IOException e) {
                System.out.printf("Ошибка открытия файла %s", target);
                LOG.error("Ошибка записи в файл {}", target);
                LOG.error(e.getMessage(), e);
            }

        }
    }

    private static void validate(Map<String,String> values){
        if (!Set.of("d", "n", "t", "o").equals(values.keySet())) {
            throw new IllegalArgumentException("Заданы не все ключи. Вызовете программу с ключом -h для справки");
        }
        validateTypeMask(values);
        validateSource(values);
    };

    private static void validateSource(Map<String,String> values) {
        String source = values.get("d");
        File dir = Paths.get(source).toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(String.format("Директория поиска: '%s' не найдена", source));
        }
    }

    private static void validateTypeMask(Map<String,String> values) {
        String maskType = values.get("t");
        if (!List.of("mask", "name", "regex").contains(maskType)) {
            throw new IllegalArgumentException(String.format("Задано недопустимое значение %s аргумента t", maskType));
        }
    }
    private static void printHelp() {
        System.out.println("""
                 Программа должна искать данные в заданном каталоге и подкаталогах.
                 Имя файла может задаваться: целиком, по маске, по регулярному выражению.
                 Программа запускаеться с параметрами, указание каждого из которых является обязательным:  -d,  -n, -t, -o\
                """);
        System.out.println();
        System.out.println("Параметры:");
        System.out.println("  -d      Директория, в которой начинать поиск");
        System.out.println("  -n      Имя файла, маска, либо регулярное выражение");
        System.out.println("  -t      Тип поиска: mask искать по маске, name по полному совпадение имени, regex по регулярному выражению");
        System.out.println("  -o      Директория, в которую записывается результат");
        System.out.println("Опции:");
        System.out.println("  -h      Показать эту справку");
        System.out.println();
    }
}

