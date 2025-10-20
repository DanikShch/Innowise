package minispring;

import minispring.annotations.Autowired;
import minispring.annotations.Component;
import minispring.annotations.Scope;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class MiniApplicationContext {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private final Set<Class<?>> prototypeBeanClasses = new HashSet<>();

    public MiniApplicationContext(String basePackage) throws IOException, IllegalAccessException {
        scanPackage(basePackage);
        injectDependencies();
    }

    private void scanPackage(String basePackage) throws IOException {
        String path = basePackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.getFile());
                scanDirectory(directory, basePackage);
            }
        }
    }

    private void scanDirectory(File directory, String packageName) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                processClass(className);
            }
        }
    }

    private void processClass(String className) {
        try {
            Class<?> procClass = Class.forName(className);
            if (procClass.isAnnotationPresent(Component.class)) {
                if (procClass.isAnnotationPresent(Scope.class) && "prototype".equals(procClass.getAnnotation(Scope.class).value())) {
                    prototypeBeanClasses.add(procClass);
                } else {
                    Object bean = procClass.getDeclaredConstructor().newInstance();
                    singletonBeans.put(procClass, bean);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to process class: " + className);
        }
    }

    public <T> T getBean(Class<T> type) {
        if (singletonBeans.containsKey(type)) {
            return type.cast(singletonBeans.get(type));
        }
        if (prototypeBeanClasses.contains(type)) {
            return createNewInstance(type);
        }
        return null;
    }

    private void injectDependencies() throws IllegalAccessException {
        for (Object bean : singletonBeans.values()) {
            injectDependenciesForBean(bean);
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
        }
    }

    private void injectDependenciesForBean(Object bean) throws IllegalAccessException {
        Class<?> procClass = bean.getClass();
        Field[] fields = procClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object dependency = getBean(field.getType());
                field.setAccessible(true);
                field.set(bean, dependency);
            }
        }
    }

    private <T> T createNewInstance(Class<T> type) {
        try {
            T bean = type.getDeclaredConstructor().newInstance();
            injectDependenciesForBean(bean);
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean", e);
        }
    }

}
