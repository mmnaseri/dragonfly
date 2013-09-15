<#-- @ftlvariable name="" type="com.agileapes.dragonfly.entity.impl.EntityFactoryModel" -->
package ${entityType.canonicalName?substring(0, entityType.canonicalName?last_index_of('.'))};

import com.agileapes.dragonfly.entity.EntityFactory;
import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "Entity factory for ${entityType.canonicalName}"
)
public class ${entityType.simpleName}EntityFactory implements EntityFactory<${entityType.simpleName}> {

    @Override
    public ${entityType.simpleName} getInstance() {
        return new ${name}();
    }

}
