Dragonfly
=========

The lightweight object-relational mapping tool for Java.

There are showcases for what this ORM does differently than other such products, as well as some of its
basic features is available under `com.agileapes.dragonfly.sample.cases` in the `dragonfly-sample` module.

Sample Execution
----------------

Sample module can be executed by running `com.agileapes.dragonfly.sample.Main`. This will execute all
test cases using the defined in the aforementioned package.

Before you can do that, however, you will need to prepare the database. For your convenience, there are
scripts that will do just that:

  * First, you will need to run the `dragonfly.sql` script to create the user.
  * Next, you will need to run the various procedure definition SQL files to create the stored procedures
  used by the tests.

The Magic
---------

Some of the magic that happens behind the scenes can be glimpsed here.

### Method Level Security

Dragonfly let's you define method level security:

    dataSecurityManager.addPolicy(new ImmutableDataSecurityPolicy("my policy", new ActorFilter() {
        @Override
        public boolean accepts(Actor actor) {
            return actor.getMethod().getName().equals("myMethod");
        }
    }, new MethodSubjectFilter(new Filter<MethodDescriptor>() {
        @Override
        public boolean accepts(MethodDescriptor descriptor) {
            return Arrays.asList("save", "insert", "update", "delete").contains(descriptor.getName());
        }
    }), PolicyDecisionType.ALLOW));

This means that the `save`, `insert`, `update`, or `delete` methods can only be called through a method
named `myMethod`. Otherwise, you will get a security exception.

### Support for basic collections

Basic collections are supported as is declared by the draft of the next JPA standard.

    @BasicCollection
    public List<Date> getDates() {
        return this.dates;
    }

This means that the values for this property will be stored in a single column.

### Complex values

Complex property values are handled automatically. This means you can now persist property values that are beyond the
primitive types supported by data stores.

### Binding non-entities to queries

You can create specific entities used for a single query.

    @Partial(targetEntity = Person.class, query = "getStatement")
    public class Statement {
        :
    }

This means that you can retrieve the statement (whose properties might not correspond directly to
those of the target entity) this way:

    dataAccess.executeQuery(Statement.class);

### Special API for accessing stored procedures

Stored procedures are supported through annotations augmenting and complementing those of JPA.

### Support for delegating calls

You can now create callbacks to support requests for special kinds of entities that are not supposed to be handled by
the original data access session.

This means, if you have an entity whose CRUD handling should be done via a REST endpoint, you can write a callback for that
entity and do the CRUD via the data access interface. This allows for a unified CRUD infrastructure for all the project.

Also, this means you can set up multi-tenant data storage very easily by creating the right data access callbacks.

### Extensible dialects

Dialects need only specify where they are different from the standard SQL specification. This allows for
very compact dialect specifications. Actually, this means you can write new dialects with 50-60 lines of
code.

As a proof, you can take the very short Mysql5 dialect that is shipped with this product.

### Events

There are event hooking points for virtually every action that is taken via this framework.

### Statement augmentation

All native queries are simple SQL statements. You have the power of Freemarker added, so that you can
write templates instead of SQL statements.

### Fluent API

There is a fluent API available for selecting data from the database:

    List<Person> people = dataAccess.from(person).where(person.getName()).isEqualTo("Milad").select();

### Extensions

You can extend table definitions themselves for certain entities:

    @Extension(filter = "having method (* myMethod(..))")
    public class MyExtension implements Erasable {

        private String name;

        @Column
        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void erase() {
            this.name = "[ERASED]";
        }

    }

This means all entities having a method called `myMethod` will be extended by `MyExtension`.

This extension adds a column named `name` to those entities. Also, it means that all such entities,
when retrieved from the database can be cast to `Erasable` (and all other interfaces this extension
implements):

    final Person person = dataAccess.find(Person.class, 1L);
    ((Erasable) person).erase();

This happens automatically, without `Person` needing to implement this interface.

### Auditing

By virtue of extensions, auditing is as simple as annotating your entity with `@Audited`.

### Monitoring

By annotating an entity with `@Monitored` you will have a history of all modifications done to the
entities via normal data access operations and you can query the history of your operations as well.

### Crud repositories

Taking the notion of CRUD repositories from Spring data, you can now define repository interfaces and
query methods:

    public interface PersonRepository extends CrudRepository<Person, Long> {

        List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    }

You can now use this interface as though it was already implemented:

    @Autowired
    private PersonRepository repository;

    public void doSomething() {
        final Person milad = repository.findByFirstNameAndLastName("Milad", "Naseri);
        //do something
    }

### Domain-driven persistence

If you get instances of your entities from the entity context or are working with entities that have been
retrieved via the default data access instance, you can always use the domain-driven approach:

    @Autowired
    private EntityContext entityContext;

    public void doSomething() {
        final Person sample = entityContext.getInstance(Person.class);
        sample.setName("Milad");
        final List<Person> milads = ((DataAccessObject<Person, Long>) sample).findLike();
        //do something
        //later on, you can do stuff with these entities in a domain-driven fashion
        final Person first = milads.get(0);
        first.setName("John");
        ((DataAccessObject) first).save();
        //or you can delete it, after which any method call or property access on the object will
        //result in an exception
        ((DataAccessObject) first).delete();
    }

### Other features

There are many other features as well. I have benchmarked many of the same operations with Hibernate 4 and
Dragonfly 1.0 and in most cases Dragonfly has emerged the superior in terms of performance.

While Dragonfly does not offer the same range of functionalities and data storage support as Hibernate,
I believe its inherent thread-safe, and session-free nature makes it more suitable to large applications
where performance is of greater import.