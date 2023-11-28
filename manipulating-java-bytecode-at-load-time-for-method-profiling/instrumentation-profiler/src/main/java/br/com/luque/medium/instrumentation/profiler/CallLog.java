package br.com.luque.medium.instrumentation.profiler;


import java.util.*;

public enum CallLog {
    INSTANCE;

    private final Collection<Clazz> classes = new HashSet<>();
    private final Map<String, MethodCall> executionIdToMethodCall = new HashMap<>();

    public Clazz getClazz(String className) {
        Clazz clazz = classes.stream()
            .filter(c -> c.getName().equals(className))
            .findFirst()
            .orElse(null);
        if(null == clazz) {
            clazz = new Clazz(className);
            classes.add(clazz);
        }
        return clazz;
    }

    public Method getMethod(String className, boolean constructor, String methodName, String methodReturnType, String... methodParameterTypes) {
        Clazz clazz = getClazz(className);
        Method method = clazz.getMethods().stream().filter(m -> m.getName().equals(methodName)
            && m.getReturnType().equals(methodReturnType)
            && Arrays.equals(m.getParameterTypes(), methodParameterTypes))
            .findFirst()
            .orElse(null);
        if(null == method) {
            method = new Method(constructor, methodName, methodReturnType, methodParameterTypes);
            clazz.addMethod(method);
        }
        return method;
    }

    public MethodCall getMethodCall(String executionId, long threadId, String className, boolean constructor, String methodName, String methodReturnType, String... methodParameterTypes) {
        MethodCall methodCall = executionIdToMethodCall.get(executionId);
        if(null == methodCall) {
            Method method = getMethod(className, constructor, methodName, methodReturnType, methodParameterTypes);
            methodCall = new MethodCall(threadId);
            method.addCall(methodCall);
            executionIdToMethodCall.put(executionId, methodCall);
        }
        return methodCall;
    }

    public void trackStart(String executionId, long threadId, String className, boolean constructor, String methodName, String methodReturnType, String... methodParameterTypes) {
        MethodCall methodCall = getMethodCall(executionId, threadId, className, constructor, methodName, methodReturnType, methodParameterTypes);
        methodCall.markStartTimeNano();
    }

    public void trackEnd(String executionId, String objectId) {
        MethodCall methodCall = executionIdToMethodCall.get(executionId);
        methodCall.markEndTimeNano();
        if(null != objectId && !objectId.isEmpty()) {
            methodCall.setObjectId(objectId);
        }
        executionIdToMethodCall.remove(executionId);
    }

    public Collection<Clazz> getResults() {
        return new ArrayList<>(classes);
    }
}
