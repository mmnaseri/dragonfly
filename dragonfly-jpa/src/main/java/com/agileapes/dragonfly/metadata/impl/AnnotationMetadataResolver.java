package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.annotations.StoredProcedure;
import com.agileapes.dragonfly.annotations.StoredProcedureParameter;
import com.agileapes.dragonfly.annotations.StoredProcedures;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.error.InvalidForeignReferenceDefinition;
import com.agileapes.dragonfly.error.NoSuchColumnError;
import com.agileapes.dragonfly.error.UnsupportedColumnTypeError;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Types;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:57)
 */
public class AnnotationMetadataResolver implements MetadataResolver {

    private static final String NO_SCHEMA = "";

    @Override
    public <E> TableMetadata<E> resolve(final Class<E> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new EntityDefinitionError("Entity is not annotated with @Entity: " + entityType.getCanonicalName());
        }
        final String tableName;
        final String schema;
        final Set<Set<String>> uniqueColumns = new HashSet<Set<String>>();
        final Set<String> keyColumns = new HashSet<String>();
        final Set<String> foreignKeys = new HashSet<String>();
        final HashSet<ReferenceMetadata<E, ?>> foreignReferences = new HashSet<ReferenceMetadata<E, ?>>();
        if (entityType.isAnnotationPresent(Table.class)) {
            final Table table = entityType.getAnnotation(Table.class);
            tableName = table.name().isEmpty() ? entityType.getSimpleName() : table.name();
            schema = table.schema();
            for (UniqueConstraint constraint : table.uniqueConstraints()) {
                final HashSet<String> columns = new HashSet<String>();
                uniqueColumns.add(columns);
                Collections.addAll(columns, constraint.columnNames());
            }
        } else {
            tableName = entityType.getSimpleName();
            schema = NO_SCHEMA;
        }
        final Set<StoredProcedureMetadata> storedProcedures = new HashSet<StoredProcedureMetadata>();
        if (entityType.isAnnotationPresent(StoredProcedure.class)) {
            storedProcedures.add(getStoredProcedureMetadata(entityType.getAnnotation(StoredProcedure.class)));
        } else if (entityType.isAnnotationPresent(StoredProcedures.class)) {
            final StoredProcedure[] procedures = entityType.getAnnotation(StoredProcedures.class).value();
            for (StoredProcedure procedure : procedures) {
                storedProcedures.add(getStoredProcedureMetadata(procedure));
            }
        }
        final Collection<SequenceMetadata> sequences = new HashSet<SequenceMetadata>();
        final HashSet<ConstraintMetadata> constraints = new HashSet<ConstraintMetadata>();
        //noinspection unchecked
        final Collection<ColumnMetadata> tableColumns = withMethods(entityType)
        .keep(new AnnotatedElementFilter(Column.class, JoinColumn.class))
        .keep(new GetterMethodFilter())
        .drop(new AnnotatedElementFilter(OneToMany.class))
        .transform(new Transformer<Method, ColumnMetadata>() {
            @Override
            public ColumnMetadata map(Method method) {
                final Column column = method.getAnnotation(Column.class);
                final JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
                final String propertyName = ReflectionUtils.getPropertyName(method.getName());
                final Class<?> propertyType = method.getReturnType();
                String name = column != null ? column.name() : joinColumn.name();
                if (name.isEmpty()) {
                    name = propertyName;
                }
                final boolean nullable = column != null ? column.nullable() : joinColumn.nullable();
                final int length = column != null ? column.length() : 0;
                final int precision = column != null ? column.precision() : 0;
                final int scale = column != null ? column.scale() : 0;
                final ValueGenerationType generationType = determineValueGenerationType(method);
                final String valueGenerator = determineValueGenerator(method);
                final ColumnMetadata foreignColumn = joinColumn == null ? null : determineForeignReference(method);
                final int type = getColumnType(method, foreignColumn);
                final ResolvedColumnMetadata columnMetadata = new ResolvedColumnMetadata(new UnresolvedTableMetadata<E>(entityType), name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, foreignColumn);
                if (foreignColumn != null) {
                    foreignKeys.add(name);
                }
                if (method.isAnnotationPresent(Id.class)) {
                    keyColumns.add(name);
                }
                if (method.isAnnotationPresent(SequenceGenerator.class)) {
                    final SequenceGenerator annotation = method.getAnnotation(SequenceGenerator.class);
                    sequences.add(new DefaultSequenceMetadata(annotation.name(), annotation.initialValue(), annotation.allocationSize()));
                }
                if (joinColumn != null) {
                    final RelationType relationType = getRelationType(method);
                    final CascadeMetadata cascadeMetadata = getCascadeMetadata(method);
                    final boolean isLazy = determineLaziness(method);
                    final ImmutableReferenceMetadata<E, Object> reference = new ImmutableReferenceMetadata<E, Object>(null, columnMetadata.getPropertyName(), null, null, relationType, cascadeMetadata, isLazy);
                    reference.setForeignColumn(foreignColumn);
                    foreignReferences.add(reference);
                }
                return columnMetadata;
            }
        }).list();
        //noinspection unchecked
        withMethods(entityType)
        .keep(new GetterMethodFilter())
        .keep(new AnnotatedElementFilter(OneToMany.class))
        .each(new Processor<Method>() {
            @Override
            public void process(Method method) {
                if (!Collection.class.isAssignableFrom(method.getReturnType())) {
                    throw new EntityDefinitionError("One to many relations must be collections. Error in " + method);
                }
                final OneToMany annotation = method.getAnnotation(OneToMany.class);
                Class<?> foreignEntity = annotation.targetEntity().equals(void.class) ? ((Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]) : annotation.targetEntity();
                String foreignColumnName = annotation.mappedBy();
                final String propertyName = ReflectionUtils.getPropertyName(method.getName());
                //noinspection unchecked
                final UnresolvedColumnMetadata foreignColumn = new UnresolvedColumnMetadata(foreignColumnName, new UnresolvedTableMetadata<Object>((Class<Object>) foreignEntity));
                final ImmutableReferenceMetadata<E, Object> reference = new ImmutableReferenceMetadata<E, Object>(null, propertyName, null, null, getRelationType(method), getCascadeMetadata(method), determineLaziness(method));
                reference.setForeignColumn(foreignColumn);
                foreignReferences.add(reference);
            }
        });
        final HashSet<NamedQueryMetadata> namedQueries = new HashSet<NamedQueryMetadata>();
        if (entityType.isAnnotationPresent(NamedNativeQueries.class)) {
            final NamedNativeQuery[] queries = entityType.getAnnotation(NamedNativeQueries.class).value();
            for (NamedNativeQuery query : queries) {
                namedQueries.add(new ImmutableNamedQueryMetadata(query.name(), query.query()));
            }
        }
        if (entityType.isAnnotationPresent(SequenceGenerator.class)) {
            final SequenceGenerator annotation = entityType.getAnnotation(SequenceGenerator.class);
            sequences.add(new DefaultSequenceMetadata(annotation.name(), annotation.initialValue(), annotation.allocationSize()));
        }
        final ResolvedTableMetadata<E> tableMetadata = new ResolvedTableMetadata<E>(entityType, schema, tableName, constraints, tableColumns, namedQueries, sequences, storedProcedures, foreignReferences);
        if (!keyColumns.isEmpty()) {
            constraints.add(new PrimaryKeyConstraintMetadata(tableMetadata, with(keyColumns).transform(new Transformer<String, ColumnMetadata>() {
                @Override
                public ColumnMetadata map(String columnName) {
                    return getColumnMetadata(columnName, tableColumns, entityType);
                }
            }).list()));
        }
        constraints.addAll(with(uniqueColumns).sort().transform(new Transformer<Set<String>, Set<ColumnMetadata>>() {
            @Override
            public Set<ColumnMetadata> map(Set<String> columns) {
                return with(columns).transform(new Transformer<String, ColumnMetadata>() {
                    @Override
                    public ColumnMetadata map(String columnName) {
                        return getColumnMetadata(columnName, tableColumns, entityType);
                    }
                }).set();
            }
        }).transform(new Transformer<Set<ColumnMetadata>, UniqueConstraintMetadata>() {
            @Override
            public UniqueConstraintMetadata map(Set<ColumnMetadata> columns) {
                return new UniqueConstraintMetadata(tableMetadata, columns);
            }
        }).list());
        constraints.addAll(with(foreignKeys).sort().transform(new Transformer<String, ColumnMetadata>() {
            @Override
            public ColumnMetadata map(String columnName) {
                return getColumnMetadata(columnName, tableColumns, entityType);
            }
        }).transform(new Transformer<ColumnMetadata, ForeignKeyConstraintMetadata>() {
            @Override
            public ForeignKeyConstraintMetadata map(ColumnMetadata columnMetadata) {
                if (columnMetadata.getForeignReference().getTable().getEntityType().equals(entityType)) {
                    String columnName = columnMetadata.getForeignReference().getName();
                    final ColumnMetadata metadata;
                    if (columnName.isEmpty()) {
                        final PrimaryKeyConstraintMetadata primaryKey = tableMetadata.getPrimaryKey();
                        if (primaryKey.getColumns().size() != 1) {
                            throw new InvalidForeignReferenceDefinition("Cannot determine foreign reference in " + entityType.getCanonicalName() + "." + columnMetadata.getName());
                        }
                        metadata = primaryKey.getColumns().iterator().next();
                    } else {
                        metadata = getColumnMetadata(columnName, tableColumns, entityType);
                    }
                    ((ResolvedColumnMetadata) columnMetadata).setForeignReference(metadata);
                }
                return new ForeignKeyConstraintMetadata(tableMetadata, columnMetadata);
            }
        }).list());
        return tableMetadata;
    }

    private static boolean determineLaziness(Method method) {
        return method.isAnnotationPresent(OneToOne.class) && method.getAnnotation(OneToOne.class).fetch().equals(FetchType.LAZY)
                || method.isAnnotationPresent(OneToMany.class) && method.getAnnotation(OneToMany.class).fetch().equals(FetchType.LAZY)
                || method.isAnnotationPresent(ManyToOne.class) && method.getAnnotation(ManyToOne.class).fetch().equals(FetchType.LAZY)
                || method.isAnnotationPresent(ManyToMany.class) && method.getAnnotation(ManyToMany.class).fetch().equals(FetchType.LAZY);
    }

    private static CascadeMetadata getCascadeMetadata(Method method) {
        final List<CascadeType> cascadeTypes = new ArrayList<CascadeType>();
        if (method.isAnnotationPresent(OneToOne.class)) {
            cascadeTypes.addAll(Arrays.asList(method.getAnnotation(OneToOne.class).cascade()));
        } else if (method.isAnnotationPresent(OneToMany.class)) {
            cascadeTypes.addAll(Arrays.asList(method.getAnnotation(OneToMany.class).cascade()));
        } else if (method.isAnnotationPresent(ManyToOne.class)) {
            cascadeTypes.addAll(Arrays.asList(method.getAnnotation(ManyToOne.class).cascade()));
        } else if (method.isAnnotationPresent(ManyToMany.class)) {
            cascadeTypes.addAll(Arrays.asList(method.getAnnotation(ManyToMany.class).cascade()));
        }
        final boolean cascadeAll = cascadeTypes.contains(CascadeType.ALL);
        return new ImmutableCascadeMetadata(
                cascadeAll || cascadeTypes.contains(CascadeType.PERSIST),
                cascadeAll || cascadeTypes.contains(CascadeType.MERGE),
                cascadeAll || cascadeTypes.contains(CascadeType.REMOVE),
                cascadeAll || cascadeTypes.contains(CascadeType.REFRESH));
    }

    private static RelationType getRelationType(Method method) {
        if (method.isAnnotationPresent(OneToMany.class)) {
            return RelationType.ONE_TO_MANY;
        } else if (method.isAnnotationPresent(ManyToOne.class)) {
            return RelationType.MANY_TO_ONE;
        } else if (method.isAnnotationPresent(ManyToMany.class)) {
            return RelationType.MANY_TO_MANY;
        }
        return RelationType.ONE_TO_ONE;
    }

    private StoredProcedureMetadata getStoredProcedureMetadata(StoredProcedure annotation) {
        final ArrayList<ParameterMetadata> parameters = new ArrayList<ParameterMetadata>();
        final StoredProcedureMetadata metadata = new ImmutableStoredProcedureMetadata(annotation.name(), annotation.resultType(), parameters);
        for (StoredProcedureParameter parameter : annotation.parameters()) {
            parameters.add(new ImmutableParameterMetadata(parameter.mode(), getColumnType(parameter.type(), null, null), parameter.type()));
        }
        return metadata;
    }

    private static int getColumnType(Method method, ColumnMetadata foreignReference) {
        return getColumnType(method.getReturnType(), method, foreignReference);
    }
    
    private static int getColumnType(Class<?> javaType, Method method, ColumnMetadata foreignReference) {
        final int dimensions = ReflectionUtils.getArrayDimensions(javaType);
        javaType = ReflectionUtils.mapType(ReflectionUtils.getComponentType(javaType));
        if (dimensions > 1) {
            throw new UnsupportedColumnTypeError("Arrays of dimension > 1 are not supported");
        }
        if (Byte.class.equals(javaType) && dimensions == 0) {
            return Types.TINYINT;
        } else if (Short.class.equals(javaType)) {
            return Types.SMALLINT;
        } else if (Integer.class.equals(javaType)) {
            return Types.INTEGER;
        } else if (Long.class.equals(javaType)) {
            return Types.BIGINT;
        } else if (Float.class.equals(javaType)) {
            return Types.FLOAT;
        } else if (Double.class.equals(javaType)) {
            return Types.DOUBLE;
        } else if (BigDecimal.class.equals(javaType)) {
            return Types.DECIMAL;
        } else if (BigInteger.class.equals(javaType)) {
            return Types.NUMERIC;
        } else if (Character.class.equals(javaType)) {
            return Types.CHAR;
        } else if (String.class.equals(javaType)) {
            if (method != null && method.isAnnotationPresent(Column.class) && method.getAnnotation(Column.class).length() > 0) {
                return Types.VARCHAR;
            } else {
                return Types.LONGVARCHAR;
            }
        } else if (Date.class.isAssignableFrom(javaType)) {
            if (javaType.equals(java.sql.Date.class)) {
                return Types.DATE;
            } else if (javaType.equals(Time.class)) {
                return Types.TIME;
            } else if (javaType.equals(java.sql.Timestamp.class)) {
                return Types.TIMESTAMP;
            }
            final TemporalType temporalType = method != null && method.isAnnotationPresent(Temporal.class) ?  method.getAnnotation(Temporal.class).value() : null;
            return (temporalType == null || temporalType.equals(TemporalType.TIMESTAMP)) ? Types.TIMESTAMP : (temporalType.equals(TemporalType.DATE) ? Types.DATE : Types.TIME);
        } else if (Byte.class.equals(javaType) && dimensions > 0) {
            return Types.VARBINARY;
        }
        if (foreignReference != null) {
            return Integer.MIN_VALUE;
        }
        throw new UnsupportedColumnTypeError("Column type not supported: " + javaType.getCanonicalName());
    }

    private static <E> ColumnMetadata getColumnMetadata(String columnName, Collection<ColumnMetadata> tableColumns, Class<E> entityType) {
        final ColumnMetadata metadata = with(tableColumns).keep(new ColumnNameFilter(columnName)).first();
        if (metadata == null) {
            throw new NoSuchColumnError(entityType, columnName);
        }
        return metadata;
    }

    private static ColumnMetadata determineForeignReference(Method method) {
        final String name;
        final Class<?> entityType;
        if (method.isAnnotationPresent(OneToOne.class)) {
            final OneToOne annotation = method.getAnnotation(OneToOne.class);
            name = annotation.mappedBy();
            entityType = annotation.targetEntity().equals(void.class) ? method.getReturnType() : annotation.targetEntity();
        } else if (method.isAnnotationPresent(ManyToOne.class)) {
            final ManyToOne annotation = method.getAnnotation(ManyToOne.class);
            name = "";
            entityType = annotation.targetEntity().equals(void.class) ? method.getReturnType() : annotation.targetEntity();
        } else {
            throw new UnsupportedOperationException();
        }
        //noinspection unchecked
        return new UnresolvedColumnMetadata(name, new UnresolvedTableMetadata<Object>((Class<Object>) entityType));
    }

    private static String determineValueGenerator(Method method) {
        return method.isAnnotationPresent(GeneratedValue.class) ? method.getAnnotation(GeneratedValue.class).generator() : null;
    }

    private static ValueGenerationType determineValueGenerationType(Method method) {
        if (method.isAnnotationPresent(GeneratedValue.class)) {
            final GeneratedValue generatedValue = method.getAnnotation(GeneratedValue.class);
            return generatedValue.strategy().equals(GenerationType.AUTO) ? ValueGenerationType.AUTO : (
                    generatedValue.strategy().equals(GenerationType.IDENTITY) ? ValueGenerationType.IDENTITY : (
                            generatedValue.strategy().equals(GenerationType.SEQUENCE) ? ValueGenerationType.SEQUENCE : (
                                    generatedValue.strategy().equals(GenerationType.TABLE) ? ValueGenerationType.TABLE : null
                            )
                    )
            );
        } else {
            return null;
        }
    }

    @Override
    public boolean accepts(Class<?> entityType) {
        return entityType.isAnnotationPresent(Entity.class);
    }

}
