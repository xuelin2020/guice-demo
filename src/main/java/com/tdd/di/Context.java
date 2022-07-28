package com.tdd.di;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, Implementation extends Type>
    void bind(Class<Type> type, Class<Implementation> implementation) {
        providers.put(type, (Provider<Type>) () -> {
            try {
                return (Type) ((Class<?>) implementation).getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }
}
