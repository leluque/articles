package br.com.luque.medium.instrumentation.profiler;

import java.util.*;

public class Clazz {
    private String name;
    private final Set<Method> methods = new HashSet<>();

    public Clazz(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void addMethod(Method method) {
        methods.add(Objects.requireNonNull(method));
    }

    public List<Method> getMethods() {
        return new ArrayList<>(methods);
    }

    private void setName(String name) {
        Objects.requireNonNull(name);
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clazz clazz = (Clazz) o;
        return Objects.equals(name, clazz.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
