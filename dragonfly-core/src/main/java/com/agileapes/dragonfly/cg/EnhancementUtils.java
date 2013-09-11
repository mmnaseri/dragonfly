package com.agileapes.dragonfly.cg;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:32)
 */
public abstract class EnhancementUtils {

    public static Method getCallerMethod() {
        return getMethod(getCaller());
    }

    public static StackTraceElement getCaller() {
        return getCaller(Thread.currentThread().getStackTrace());
    }

    public static StackTraceElement getCaller(StackTraceElement[] stackTraceElements) {
        StackTraceElement last = null;
        for (int i = stackTraceElements.length - 1; i >= 0; i--) {
            if (stackTraceElements[i].getClassName().matches(".*?\\$ENHANCED\\$[^\\$]+?\\$\\d+")) {
                return last;
            }
            last = stackTraceElements[i];
        }
        return last;
    }

    public static Method getMethod(final StackTraceElement stackTraceElement) {
        final String stackTraceClassName = stackTraceElement.getClassName();
        final String stackTraceMethodName = stackTraceElement.getMethodName();
        final int stackTraceLineNumber = stackTraceElement.getLineNumber();
        Class<?> stackTraceClass;
        try {
            stackTraceClass = Class.forName(stackTraceClassName);
        } catch (ClassNotFoundException e) {
            throw new Error("No such class: " + stackTraceClassName);
        }
        final AtomicReference<String> methodDescriptorReference = new AtomicReference<String>();
        String classFileResourceName = "/" + stackTraceClassName.replaceAll("\\.", "/") + ".class";
        InputStream classFileStream = stackTraceClass.getResourceAsStream(classFileResourceName);

        if (classFileStream == null) {
            throw new Error("Could not acquire the class file containing for the calling class");
        }

        try {
            ClassReader classReader;
            try {
                classReader = new ClassReader(classFileStream);
            } catch (IOException e) {
                throw new Error("Failed to initialize class reader");
            }
            classReader.accept(
                    new EmptyVisitor() {
                        @Override
                        public MethodVisitor visitMethod(int access, final String name, final String desc, String signature, String[] exceptions) {
                            if (!name.equals(stackTraceMethodName)) {
                                return null;
                            }

                            return new EmptyVisitor() {
                                @Override
                                public void visitLineNumber(int line, Label start) {
                                    if (line == stackTraceLineNumber) {
                                        methodDescriptorReference.set(desc);
                                    }
                                }
                            };
                        }
                    },
                    0
            );
        } finally {
            try {
                classFileStream.close();
            } catch (IOException ignored) {
            }
        }

        String methodDescriptor = methodDescriptorReference.get();

        if (methodDescriptor == null) {
            throw new Error("Could not find line " + stackTraceLineNumber);
        }

        for (Method method : stackTraceClass.getMethods()) {
            if (stackTraceMethodName.equals(method.getName()) && methodDescriptor.equals(Type.getMethodDescriptor(method))) {
                return method;
            }
        }

        throw new Error("Could not find the calling method");
    }
}