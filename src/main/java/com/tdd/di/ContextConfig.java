package com.tdd.di;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class ContextConfig{

    private Map<Class<?>, ComponentProvider<?>> provider = new HashMap<>();
    private Map<Class<?>, List<Class<?>>> dependencies = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        provider.put(type, context -> instance);
        dependencies.put(type, List.of());
    }

    public <Type, Implementation extends Type>
    void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<Implementation> injectConstructor = getInjectConstructor(implementation);
        provider.put(type, new ConstructorInjectionProvider(type, injectConstructor));
        dependencies.put(type, stream(injectConstructor.getParameters()).map(parameter -> parameter.getType()).collect(Collectors.toList()));
    }

    public Context getContext(){
        // TODO check
        return new Context() {
            @Override
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(provider.get(type)).map(provider -> (Type) provider.get(this));
            }
        };
    }

    interface ComponentProvider<T>{
        T get(Context context);
    }

    class ConstructorInjectionProvider<T> implements ComponentProvider<T>{
        private Class<?> componentType;
        private Constructor<T> injectConstructor;
        private boolean constructing = false;

        public ConstructorInjectionProvider(Class<?> componentType, Constructor<T> injectConstructor) {
            this.componentType = componentType;
            this.injectConstructor = injectConstructor;
        }

        @Override
        public T get(Context context) {
            if (constructing) throw new CyclicDependenciesFound();
            try {
                constructing = true;
                Object[] dependencies = stream(injectConstructor.getParameters())
                        .map(p -> context.get(p.getType())
                                .orElseThrow(() -> new DependencyNotFoundException(componentType, p.getType())))
                        .toArray(Object[]::new);
                return injectConstructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            finally {
                constructing = false;
            }
        }
    }

    private <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {
        List<Constructor<?>> injectContractors = stream(implementation.getConstructors()).filter(
                c -> c.isAnnotationPresent(Inject.class)
        ).collect(Collectors.toList());

        if (injectContractors.size() > 1) throw new IllegalComponentException();

        return (Constructor<Type>) injectContractors.stream().findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalComponentException();
            }
        });
    }

}
