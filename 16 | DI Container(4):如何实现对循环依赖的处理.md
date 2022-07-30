# 16 | DI Container(4)

### 回顾

- 在已有的测试上实现功能，解决循环依赖问题。


####  
1. 匿名类 变为 类，置一个标志，标志位
   - 进入构造是，将 constructing 设为 true
   - 结束构造时，将 constructing 设为 false
   - 如果进入时，已经在构造中，则 throw exception
```java
class ConstructorInjectionProvider<T> implements Provider<T>{
        private Constructor<T> injectConstructor;
        private boolean constructing = false;

        public ConstructorInjectionProvider(Constructor<T> injectConstructor) {
            this.injectConstructor = injectConstructor;
        }

        @Override
        public T get() {
            if (constructing) throw new CyclicDependenciesFound();
            try {
                constructing = true;
                Object[] dependencies = stream(injectConstructor.getParameters())
                        .map(p -> Context.this.get(p.getType()).orElseThrow(() -> new DependencyNotFoundException()))
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
```
2. 