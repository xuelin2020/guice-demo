package com.tdd.di;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ContainerTest {

    @Nested
    public class ComponentConstruction{

        interface Component{

        }

        //TODO: instance
        @Test
        void 1() {

            Context context = new Context();

            Component instance = new Component();

            context.bind(Compont.class, instance);

            assertSame(instance, context.get(Component.class));

        }


        //TODO: abstract class
        //TODO: interface

        @Nested
        public class ConstructorInjection{
            //TODO: No args constructor
            //TODO: with dependencies
            //TODO: A -> B -> C
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
