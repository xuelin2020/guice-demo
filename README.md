## 徐昊 TDD 项目实战 70 讲 - 笔记

### 12 | 实战中的 TDD: RESTful API的开发框架

+ 第一个场景是开发一个 mini 版本的 Dropwizard① 或者 Spring MVC.
  + 功能范围包含一个依赖注入容器（Dependency Injection Container/IoC Container）和一个支持 RESTful API 构建的 Web 框架.

+ 我们会以 Jakarta EE② 中的 Jakarta Dependency Injection 和 Jakarta RESTful Web Services 为主要功能参考.

#### 预期结果 通过以下代码实现 RESTful API



    @Path("/students")
    public class StudentsResource {
    
        private StudentRepository repository;
        
        @Inject
        public StudentsResource(StudentRepository repository) {
            this.repository = repository;
        }
        
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public List<Student> all() {
            return repository.all();
        }
        
        @GET
        @Path("{id}")
        @Produces(MediaType.APPLICATION_JSON)
        public Response findById(@PathParam("id") long id) {
            return repository.findById(id).map(Response::ok)
                    .orElse(Response.status(Response.Status.NOT_FOUND)).build();
        }
    }


## 依赖注入容器的大致功能 
+ 首先让我们从依赖注入容器开始。关于依赖注入的来龙去脉可以参看 Martin Fowler 在 2004 年写的文章
+ Jakarta Dependency Injection 的功能主要分为三部分：组件的构造、依赖的选择以及生命周期控制。





---
名词解释 (作为本世纪入行三年的开发,原谅我这些框架都没听过)   
①Dropwizard 是一款开发运维友好、高效、RESTful web服务的框架。
②Jakarta EE 并不是新技术，他的前身就是大家熟悉的Java EE，老一辈的程序员可能还记得J2EE，是的，他们都是同一个东西，至于为什么会改来改去，这里面就有很多故事了。(非重点待续)