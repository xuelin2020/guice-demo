package com.example;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ComponentConstructionTest {

    interface Car{
        Engine getEngine();
    }

    interface Engine {
        String getName();
    }

    static class V6Engine implements Engine {
        @Override
        public String getName() {
            return "v6";
        }
    }

    static class V8Engine implements Engine {
        @Override
        public String getName() {
            return "v8";
        }
    }

    @Nested
    class ConstructorInjection{

        static class CarInjectConstructor implements Car {

            private final Engine engine;

            @Inject
            public CarInjectConstructor(Engine engine) {
                this.engine = engine;
            }

            @Override
            public Engine getEngine() {
                return engine;
            }
        }

        @Test
        void constructor_injection(){
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).to(V8Engine.class);
                    bind(Car.class).to(CarInjectConstructor.class);
                }
            });

            Car car = injector.getInstance(Car.class);
            assertEquals("v8", car.getEngine().getName());
        }

        static class CarInjectField implements Car {
            @Inject
            private Engine engine;

            @Override
            public Engine getEngine() {
                return engine;
            }

            private void install(Engine engine) {
                this.engine = engine;
            }
        }

        static class CarInjectMethod implements Car {

            @Inject
            private Engine engine;

            @Override
            public Engine getEngine() {
                return engine;
            }

            private void install(Engine engine) {
                this.engine = engine;
            }
        }


    }

}
