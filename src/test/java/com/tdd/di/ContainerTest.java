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

        ContextConfig contextConfig;
        @BeforeEach
        void setUp() {
            contextConfig = new ContextConfig();
        }

        //TODO: instance
        @Test
        void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            contextConfig.bind(Component.class, instance);
            assertSame(instance, contextConfig.getContext().get(Component.class).get());
        }

        //TODO: abstract class
        //TODO: interface



        @Test
        void should_return_empty_if_component_not_defined() {
            Optional<Component> component = contextConfig.getContext().get(Component.class);
            assertTrue(component.isEmpty());
        }

        @Nested
        public class ConstructorInjection{
            @Test
            void should_bind_type_to_a_class_with_default_constructor() {

                contextConfig.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = contextConfig.getContext().get(Component.class).get();

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            @Test
            void should_bind_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency(){};

                contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);
                contextConfig.bind(Dependency.class, dependency);

                Component instance = contextConfig.getContext().get(Component.class).get();
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            @Test
            void should_bind_type_to_a_class_with_transitive_dependencies() {
                contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);
                contextConfig.bind(Dependency.class, DependencyWithInjectConstructor.class);
                contextConfig.bind(String.class, "indirect dependency");

                Component instance = contextConfig.getContext().get(Component.class).get();

                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);

                assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
            }
        }

        @Test
        void should_throw_exception_if_multi_inject_constructors_provided() {
            assertThrows(IllegalComponentException.class,
                    () -> contextConfig.bind(Component.class, ComponentWithMultiInjectConstructors.class));
        }

        @Test
        void should_throw_exception_if_no_inject_nor_default_constructors_provided() {
            assertThrows(IllegalComponentException.class,
                    () -> contextConfig.bind(Component.class, ComponentWithNoInjectConstructorNorDefaultConstructor.class));
        }

        @Test
        void should_throw_exception_if_dependency_not_found() {
            contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);

            DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> contextConfig.getContext());

            assertEquals(Dependency.class, exception.getDependency());
            assertEquals(Component.class, exception.getComponent());

        }

        @Test
        void should_throw_exception_if_transitive_dependency_not_found() {
            contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);
            contextConfig.bind(Dependency.class, DependencyWithInjectConstructor.class);

            DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> contextConfig.getContext());
            assertEquals(String.class, exception.getDependency());
            assertEquals(Dependency.class, exception.getComponent());
        }

        @Test
        void should_throw_exception_if_cyclic_dependencies_found() {
            contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);
            contextConfig.bind(Dependency.class, DependencyDependedOnComponent.class);

            assertThrows(CyclicDependenciesFound.class, () -> contextConfig.getContext());
        }


        @Test
        void should_throw_exception_if_transitive_cyclic_dependencies_for_found() {
            contextConfig.bind(Component.class, ComponentWithInjectConstructor.class);
            contextConfig.bind(Dependency.class, DependencyDependedOnAnotherDependency.class);
            contextConfig.bind(AnotherDependency.class, AnotherDependencyDependedOnComponent.class);

            assertThrows(CyclicDependenciesFound.class, () -> contextConfig.getContext());
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

interface AnotherDependency{

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

class DependencyDependedOnComponent implements Dependency {
    private Component component;

    @Inject
    public DependencyDependedOnComponent(Component component) {
        this.component = component;
    }
}

class AnotherDependencyDependedOnComponent implements AnotherDependency{
    private Component component;

    @Inject
    public AnotherDependencyDependedOnComponent(Component component) {
        this.component = component;
    }
}

class DependencyDependedOnAnotherDependency implements Dependency{
    private AnotherDependency anotherDependency;

    @Inject
    public DependencyDependedOnAnotherDependency(AnotherDependency anotherDependency) {
        this.anotherDependency = anotherDependency;
    }
}