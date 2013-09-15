package ${name?substring(0, name?last_index_of('.'))};

public class ${name?substring(name?last_index_of('.') + 1)} extends ${entityType.simpleName} {
}
