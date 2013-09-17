<#-- @ftlvariable name="" type="com.agileapes.dragonfly.entity.impl.EntityFactoryModel" -->
package ${entityType.canonicalName?substring(0, entityType.canonicalName?last_index_of('.'))};

import com.agileapes.dragonfly.entity.EntityFactory;
import com.agileapes.couteau.enhancer.api.Interceptible;
import com.agileapes.dragonfly.entity.impl.EntityProxy;

import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "Entity factory for ${entityType.canonicalName}"
)
public class ${entityType.simpleName}EntityFactory implements EntityFactory<${entityType.simpleName}> {

    @Override
    public ${entityType.simpleName} getInstance(EntityProxy<${entityType.canonicalName}> proxy) {
        final ${entityType.simpleName} entity = new ${name}();
        ((Interceptible) entity).setInterceptor(proxy);
        return entity;
    }

}
