package br.com.luque.medium.instrumentation.profiler;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfilerAgent {
    private static final Logger logger = Logger.getLogger(ProfilerAgent.class.getName());
    public static boolean shutdownAdded = false;

    public static void premain(String agentArgs, Instrumentation inst) {
        assert(CallLog.INSTANCE != null);
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                // Packages that will be instrumented.
                String[] targetPackages = agentArgs.split(",");
                if (targetPackages.length == 0) {
                    return classfileBuffer;
                }

                String fullyQualifiedName = className == null ? null : className.replace('/','.');
                if (loader == null
                    || className == null
                    || Stream.of(targetPackages).noneMatch(fullyQualifiedName::startsWith)
                    || className.contains("$$Lambda$")
                    || className.contains("$$")) {
                    return classfileBuffer;
                }

                ClassPool classPool = ClassPool.getDefault();
                try {
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    if(ctClass.isInterface()) {
                        return classfileBuffer;
                    }
                    for (CtConstructor method : ctClass.getDeclaredConstructors()) {
                        String arguments = getMethodArguments(method);
                        String executionId = UUID.randomUUID().toString();
                        method.insertAfter("{br.com.luque.medium.instrumentation.profiler.CallLog.INSTANCE.trackStart(\"%s\",%s);}".formatted(executionId, arguments));
                        method.insertAfter("{br.com.luque.medium.instrumentation.profiler.CallLog.INSTANCE.trackEnd(\"%s\",String.valueOf(System.identityHashCode(this)));}".formatted(executionId));
                    }

                    for (CtMethod method : ctClass.getDeclaredMethods()) {
                        String arguments = getMethodArguments(method);
                        String executionId = UUID.randomUUID().toString();
                        method.insertAfter("{br.com.luque.medium.instrumentation.profiler.CallLog.INSTANCE.trackStart(\"%s\",%s);}".formatted(executionId, arguments));
                        method.insertAfter("{br.com.luque.medium.instrumentation.profiler.CallLog.INSTANCE.trackEnd(\"%s\",\"%s\");}".formatted(executionId, Modifier.isStatic(method.getModifiers()) ? "" : "String.valueOf(System.identityHashCode(this))"));
                    }

                    byte[] byteCode = ctClass.toBytecode();
                    ctClass.detach();
                    return byteCode;
                } catch (CannotCompileException | NotFoundException | IOException e) {
                    logger.warning(String.format("The class %s could not be transformed due to: %s", className, e.getMessage()));
                }
                return classfileBuffer;
            }
        });
        if (!shutdownAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(ProfilerAgent::runCleanupMethod));
            shutdownAdded = true;
        }
    }

    private static void runCleanupMethod() {
        System.out.println("##########");
        System.out.println("Profiler results:");
        System.out.println(new ProfilerResultPrinter(CallLog.INSTANCE.getResults()).asString());
    }

    private static String getMethodArguments(CtBehavior method) throws NotFoundException {
        UUID executionId = UUID.randomUUID();
        String arguments = "Thread.currentThread().getId(),"; // Thread ID.
        arguments += "\"%s\",".formatted(method.getDeclaringClass().getName()); // Class name.
        arguments += "%s,".formatted(method instanceof CtConstructor); // Is constructor.
        arguments += "\"%s\",".formatted(method.getName()); // Method name.
        if (method instanceof CtConstructor) {
            arguments += "\"\","; // Return type.
        } else {
            CtClass returnType = ((CtMethod) method).getReturnType();
            arguments += returnType == null ? "null," : "\"%s\",".formatted(returnType.getName()); // Return type.
        }
        arguments += "new String[] {";
        arguments += Stream.of(method.getParameterTypes()).map(c -> "\"%s\"".formatted(c.getName())).collect(Collectors.joining(","));
        arguments += "}";
        return arguments;
    }
}
