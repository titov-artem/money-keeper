package com.github.money.keeper.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.util.structure.TrieMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Proxy between JS and Java to serialize/desirialize JSON object and perform invocations
 * <p>
 * Idea: make web controller work in local application for future reuse in web application
 */
public class Endpoint {
    private static final Logger log = LoggerFactory.getLogger(Endpoint.class);

    private static final String[] EMPTY_ARGS = new String[0];

    private final TrieMap<ClassEndpoint> endpoints = new TrieMap<>();

    public void register(Object controller) {
        Class<?> cClass = controller.getClass();
        Path pathAnnotation = cClass.getAnnotation(Path.class);
        String classPath = slashEnded(pathAnnotation == null ? cClass.getSimpleName() : pathAnnotation.value());
        if (endpoints.put(classPath, new ClassEndpoint(cClass, controller)) != null) {
            throw new IllegalArgumentException("Duplicate class endpoints for path " + classPath);
        }
    }

    public void setControllers(List<?> controllers) {
        for (Object controller : controllers) {
            register(controller);
        }
    }

    public String get(@Nonnull String path, JSObject args) {
        return invoke(RMethod.GET, path, args);
    }

    public String post(@Nonnull String path, JSObject args) {
        return invoke(RMethod.POST, path, args);
    }

    public String put(@Nonnull String path, JSObject args) {
        return invoke(RMethod.PUT, path, args);
    }

    public String delete(@Nonnull String path, JSObject args) {
        return invoke(RMethod.DELETE, path, args);
    }

    private String invoke(RMethod method, String path, JSObject args) {
        try {
            return invokeUnsafe(method, path, args);
        } catch (Exception e) {
            log.error("Failed to invoke method for path " + path + " and args [" + args + "] due to exception", e);
            throw new RuntimeException(e);
        }
    }

    private String invokeUnsafe(RMethod method, String path, JSObject args) {
        path = slashEnded(path);
        Pair<String, ClassEndpoint> endpoint = endpoints.getByFirstPrefix(path);
        if (endpoint == null) {
            throw new IllegalArgumentException("No method bind to path " + path);
        }
        String methodPath = slashStarted(slashEnded(path.substring(endpoint.getKey().length())));
        MethodEndpoint methodEndpoint = endpoint.getValue().endpoints.get(Pair.of(method, methodPath));
        return methodEndpoint.invoke(args == null ? EMPTY_ARGS : convert(args));
    }

    private static String[] convert(JSObject args) {
        String[] out = new String[(Integer) args.getMember("length")];
        for (int i = 0; i < out.length; i++) {
            out[i] = (String) args.getSlot(i);
        }
        return out;
    }

    private static String slashStarted(String source) {
        return source.startsWith("/") ? source : "/" + source;
    }

    private static String slashEnded(String source) {
        return source.endsWith("/") ? source : source + "/";
    }

    private static final class ClassEndpoint {
        private final Map<Pair<RMethod, String>, MethodEndpoint> endpoints = Maps.newHashMap();

        public ClassEndpoint(Class<?> clazz, Object controller) {
            for (Method method : clazz.getMethods()) {
                RMethod requestMethod = RMethod.forMethod(method);
                if (requestMethod == null) continue;

                Path pathAnnotation = method.getAnnotation(Path.class);
                String path = slashEnded(pathAnnotation == null ? "" : pathAnnotation.value());

                if (endpoints.put(Pair.of(requestMethod, path), new MethodEndpoint(method, controller)) != null) {
                    throw new IllegalArgumentException("Duplicate method endpoints for path " + path);
                }
            }
        }
    }

    private static class MethodEndpoint {
        private final Method method;
        private final Object controller;
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public MethodEndpoint(Method method, Object controller) {
            this.method = method;
            this.controller = controller;
        }

        public String invoke(String... args) {
            Class<?>[] argTypes = method.getParameterTypes();
            Preconditions.checkArgument(argTypes.length == args.length,
                    String.format("Wrong number of arguments. Expected %d, got %d", argTypes.length, args.length));
            Object[] parsedArgs = new Object[argTypes.length];
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> argType = argTypes[i];
                String arg = args[i];
                try {
                    if (argType.equals(String.class)) {
                        parsedArgs[i] = arg;
                    } else if (Byte.class.equals(argType) || byte.class.equals(argType)) {
                        parsedArgs[i] = Byte.parseByte(arg);
                    } else if (Short.class.equals(argType) || short.class.equals(argType)) {
                        parsedArgs[i] = Short.parseShort(arg);
                    } else if (Integer.class.equals(argType) || int.class.equals(argType)) {
                        parsedArgs[i] = Integer.parseInt(arg);
                    } else if (Long.class.equals(argType) || long.class.equals(argType)) {
                        parsedArgs[i] = Long.parseLong(arg);
                    } else if (Float.class.equals(argType) || float.class.equals(argType)) {
                        parsedArgs[i] = Float.parseFloat(arg);
                    } else if (Double.class.equals(argType) || double.class.equals(argType)) {
                        parsedArgs[i] = Double.parseDouble(arg);
                    } else if (Boolean.class.equals(argType) || boolean.class.equals(argType)) {
                        parsedArgs[i] = Boolean.parseBoolean(arg);
                    } else if (Character.class.equals(argType) || char.class.equals(argType)) {
                        parsedArgs[i] = arg.charAt(0);
                    } else if (argType.isEnum()) {
                        parsedArgs[i] = Enum.valueOf((Class<Enum>) argType, arg);
                    } else {
                        parsedArgs[i] = objectMapper.readValue(arg, argType);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format("Failed to parse parameter %s as type %s from source ",
                            arg, argType));
                }

            }
            Class<?> returnType = method.getReturnType();
            try {
                if (void.class.equals(returnType)) {
                    method.invoke(controller, parsedArgs);
                    return null;
                } else {
                    Object result = method.invoke(controller, parsedArgs);
                    if (result == null) return null;
                    if (String.class.equals(returnType)) return result.toString();
                    return objectMapper.writeValueAsString(result);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private enum RMethod {
        GET(javax.ws.rs.GET.class),
        POST(javax.ws.rs.POST.class),
        PUT(javax.ws.rs.PUT.class),
        DELETE(javax.ws.rs.DELETE.class);

        private final Class annotation;

        RMethod(Class annotation) {
            this.annotation = annotation;
        }

        public static RMethod forMethod(Method method) {
            for (final RMethod m : values()) {
                if (method.getAnnotation(m.annotation) != null) {
                    return m;
                }
            }
            return null;
        }
    }
}
