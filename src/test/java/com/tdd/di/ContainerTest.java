package com.tdd.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

    @Nested
    public class ComponentConstruction{

        Context context;
        @BeforeEach
        void setUp() {
            context = new Context();
        }

        //TODO: instance
        @Test
        void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            context.bind(Component.class, instance);
            assertSame(instance, context.get_(Component.class).orElseThrow(() -> new DependencyNotFoundException()));
        }

        //TODO: abstract class
        //TODO: interface
        //TODO: component does not exist
        @Test
        void should_return_empty_if_component_not_defined() {
            Optional<Component> component = context.get_(Component.class);
            assertTrue(component.isEmpty());
        }

        @Nested
        public class ConstructorInjection{

            @Test
            void should_bind_type_to_a_class_with_default_constructor() {

                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = context.get_(Component.class).orElseThrow(() -> new DependencyNotFoundException());

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            @Test
            void should_bind_type_to_a_class_with_inject_constructor() {

                Dependency dependency = new Dependency(){

                };

                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                Component instance = context.get_(Component.class).orElseThrow(() -> new DependencyNotFoundException());
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            @Test
            void should_bind_type_to_a_class_with_transitive_dependencies() {
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, "indirect dependency");

                Component instance = context.get_(Component.class).orElseThrow(() -> new DependencyNotFoundException());

                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);

                assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
            }
        }

        @Test
        void should_throw_exception_if_multi_inject_constructors_provided() {
            assertThrows(IllegalComponentException.class,
                    () -> context.bind(Component.class, ComponentWithMultiInjectConstructors.class));
        }

        @Test
        void should_throw_exception_if_no_inject_nor_default_constructors_provided() {
            assertThrows(IllegalComponentException.class,
                    () -> context.bind(Component.class, ComponentWithNoInjectConstructorNorDefaultConstructor.class));
        }

        @Test
        void should_throw_exception_if_dependency_not_found() {
            context.bind(Component.class, ComponentWithInjectConstructor.class);

            assertThrows(DependencyNotFoundException.class, () -> context.get_(Component.class).orElseThrow(() -> new DependencyNotFoundException()));
        }

        @Nested
        public class FieldInjection{

        }

        @Nested
        public class MethodInjection{

        }

    }


    @Nested
    public class DependenciesSelection{

    }


    @Nested
    public class LifecycleManagement{


    }

}

interface Component{

}

interface Dependency{

}

class ComponentWithDefaultConstructor implements Component{
    public ComponentWithDefaultConstructor() {
    }
}

class ComponentWithInjectConstructor implements Component{
    private Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}

class ComponentWithMultiInjectConstructors implements Component{

    @Inject
    public ComponentWithMultiInjectConstructors(String name, Double value) {
    }

    @Inject
    public ComponentWithMultiInjectConstructors(String name) {
    }
}

class ComponentWithNoInjectConstructorNorDefaultConstructor implements Component{
    public ComponentWithNoInjectConstructorNorDefaultConstructor(String name) {
    }
}

class DependencyWithInjectConstructor implements Dependency{
    String dependency;

    @Inject
    public DependencyWithInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}
