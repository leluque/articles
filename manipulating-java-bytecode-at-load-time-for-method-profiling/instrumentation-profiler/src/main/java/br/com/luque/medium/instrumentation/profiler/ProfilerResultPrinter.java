package br.com.luque.medium.instrumentation.profiler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProfilerResultPrinter {
    private final Collection<Clazz> classes;

    public ProfilerResultPrinter(Collection<Clazz> classes) {
        this.classes = Objects.requireNonNull(classes);
    }

    public String asString() {
        StringBuilder result = new StringBuilder();
        classes.forEach(clazz -> {
            result.append("=====================================").append(System.lineSeparator());
            result.append("Class: %s%n".formatted(clazz.getName()));
            clazz.getMethods().forEach(method -> {
                if(method.isConstructor()) {
                    result.append("  Constructor: %s%n".formatted(method.toString()));
                } else {
                    result.append("  Method: %s%n".formatted(method.toString()));
                }
                result.append("    Calls: %d%n".formatted(method.countCalls()));
                result.append("    Total time: %d ns%n".formatted(method.getTotalTimeNano()));
                result.append("    Average time: %.2f ns%n".formatted(method.getAverageTimeNano()));
            });
        });
        return result.toString();
    }
}

