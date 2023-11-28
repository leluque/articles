package br.com.luque.medium.instrumentation.profiler;

import java.util.*;

public class Method {
    private String name;
    private String returnType;
    private String[] parameterTypes;
    private boolean constructor;
    private final Collection<MethodCall> methodCalls = new ArrayList<>();

    public Method(boolean constructor, String name, String returnType, String... parameterTypes) {
        setConstructor(constructor);
        setName(name);
        setReturnType(returnType);
        setParameterTypes(parameterTypes);
    }

    public boolean isConstructor() {
        return constructor;
    }

    private void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        Objects.requireNonNull(name);
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Cannot be empty!");
        }
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean hasReturnType() {
        return returnType != null && !returnType.isEmpty();
    }

    private void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    private void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void addCall(MethodCall methodCall) {
        methodCalls.add(methodCall);
    }

    public List<MethodCall> getCalls() {
        return new ArrayList<>(methodCalls);
    }

    public int countCalls() {
        return methodCalls.size();
    }

    public double getAverageTimeNano() {
        return methodCalls.stream()
            .mapToLong(MethodCall::getDurationNano)
            .average()
            .orElse(0);
    }

    public long getTotalTimeNano() {
        return methodCalls.stream()
            .mapToLong(MethodCall::getDurationNano)
            .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasReturnType()) {
            sb.append(returnType).append(" ");
        }
        sb.append(name).append("(");
        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(parameterTypes[i]);
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return constructor == method.constructor && Objects.equals(name, method.name) && Objects.equals(returnType, method.returnType) && Arrays.equals(parameterTypes, method.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, returnType, constructor);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }
}
