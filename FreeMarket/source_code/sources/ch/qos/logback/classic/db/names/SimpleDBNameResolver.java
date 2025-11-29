package ch.qos.logback.classic.db.names;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/db/names/SimpleDBNameResolver.class */
public class SimpleDBNameResolver implements DBNameResolver {
    private String tableNamePrefix = "";
    private String tableNameSuffix = "";
    private String columnNamePrefix = "";
    private String columnNameSuffix = "";

    @Override // ch.qos.logback.classic.db.names.DBNameResolver
    public <N extends Enum<?>> String getTableName(N tableName) {
        return this.tableNamePrefix + tableName.name().toLowerCase() + this.tableNameSuffix;
    }

    @Override // ch.qos.logback.classic.db.names.DBNameResolver
    public <N extends Enum<?>> String getColumnName(N columnName) {
        return this.columnNamePrefix + columnName.name().toLowerCase() + this.columnNameSuffix;
    }

    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix != null ? tableNamePrefix : "";
    }

    public void setTableNameSuffix(String tableNameSuffix) {
        this.tableNameSuffix = tableNameSuffix != null ? tableNameSuffix : "";
    }

    public void setColumnNamePrefix(String columnNamePrefix) {
        this.columnNamePrefix = columnNamePrefix != null ? columnNamePrefix : "";
    }

    public void setColumnNameSuffix(String columnNameSuffix) {
        this.columnNameSuffix = columnNameSuffix != null ? columnNameSuffix : "";
    }
}
