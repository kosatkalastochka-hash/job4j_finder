package org.example.finder;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class Visitor extends SimpleFileVisitor<Path> {
    private final Set<String> paths = new HashSet<>();
    private final PathMatcher matcher;

    public Visitor(PathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (matcher.matches(file)) {
            paths.add(file.toString());
        }
        return FileVisitResult.CONTINUE;
    }

    public Set<String> getPaths() {
        return paths;
    }
}
