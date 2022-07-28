package com.tdd.di;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<Class<?>, Class<?>> componentImplementations = new HashMap<>();
    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <ComponentType> void bind(Class<ComponentType> type, ComponentType instance) {
        providers.put(type, (Provider<ComponentType>) () -> instance);
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(Class<ComponentType> type, Class<ComponentImplementation> implementation) {
        componentImplementations.put(type, implementation);
    }

    public <ComponentType> ComponentType get(Class<ComponentType> type) {
        if (providers.containsKey(type))
            return (ComponentType) providers.get(type).get();

        Class<?> implemetation = componentImplementations.get(type);
        try {
            return (ComponentType) implemetation.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
