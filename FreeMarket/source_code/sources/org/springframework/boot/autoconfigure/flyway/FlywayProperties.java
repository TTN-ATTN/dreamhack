package org.springframework.boot.autoconfigure.flyway;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "spring.flyway")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayProperties.class */
public class FlywayProperties {
    private boolean failOnMissingLocations;
    private int connectRetries;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectRetriesInterval;
    private Integer lockRetryCount;
    private String defaultSchema;
    private String tablespace;
    private String installedBy;
    private String placeholderSeparator;
    private String target;
    private String user;
    private String password;
    private String driverClassName;
    private String url;
    private boolean baselineOnMigrate;
    private boolean cleanDisabled;
    private boolean cleanOnValidationError;
    private boolean group;

    @Deprecated
    private boolean ignoreMissingMigrations;

    @Deprecated
    private boolean ignoreIgnoredMigrations;

    @Deprecated
    private boolean ignorePendingMigrations;
    private boolean mixed;
    private boolean outOfOrder;
    private boolean skipDefaultCallbacks;
    private boolean skipDefaultResolvers;
    private Boolean batch;
    private File dryRunOutput;
    private String[] errorOverrides;
    private String licenseKey;
    private Boolean oracleSqlplus;
    private Boolean oracleSqlplusWarn;
    private Boolean stream;
    private String undoSqlMigrationPrefix;
    private String[] cherryPick;
    private String kerberosConfigFile;
    private String oracleKerberosCacheFile;
    private String oracleWalletLocation;
    private Boolean outputQueryResults;
    private String sqlServerKerberosLoginFile;
    private Boolean skipExecutingMigrations;
    private List<String> ignoreMigrationPatterns;
    private Boolean detectEncoding;
    private String baselineMigrationPrefix;
    private String scriptPlaceholderPrefix;
    private String scriptPlaceholderSuffix;
    private boolean enabled = true;
    private List<String> locations = new ArrayList(Collections.singletonList("classpath:db/migration"));
    private Charset encoding = StandardCharsets.UTF_8;
    private List<String> schemas = new ArrayList();
    private boolean createSchemas = true;
    private String table = "flyway_schema_history";
    private String baselineDescription = "<< Flyway Baseline >>";
    private String baselineVersion = CustomBooleanEditor.VALUE_1;
    private Map<String, String> placeholders = new HashMap();
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";
    private boolean placeholderReplacement = true;
    private String sqlMigrationPrefix = "V";
    private List<String> sqlMigrationSuffixes = new ArrayList(Collections.singleton(".sql"));
    private String sqlMigrationSeparator = "__";
    private String repeatableSqlMigrationPrefix = "R";
    private List<String> initSqls = new ArrayList();

    @Deprecated
    private boolean ignoreFutureMigrations = true;
    private boolean validateMigrationNaming = false;
    private boolean validateOnMigrate = true;
    private Map<String, String> jdbcProperties = new HashMap();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFailOnMissingLocations() {
        return this.failOnMissingLocations;
    }

    public void setFailOnMissingLocations(boolean failOnMissingLocations) {
        this.failOnMissingLocations = failOnMissingLocations;
    }

    public List<String> getLocations() {
        return this.locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public int getConnectRetries() {
        return this.connectRetries;
    }

    public void setConnectRetries(int connectRetries) {
        this.connectRetries = connectRetries;
    }

    public Duration getConnectRetriesInterval() {
        return this.connectRetriesInterval;
    }

    public void setConnectRetriesInterval(Duration connectRetriesInterval) {
        this.connectRetriesInterval = connectRetriesInterval;
    }

    public Integer getLockRetryCount() {
        return this.lockRetryCount;
    }

    public void setLockRetryCount(Integer lockRetryCount) {
        this.lockRetryCount = lockRetryCount;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public List<String> getSchemas() {
        return this.schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public boolean isCreateSchemas() {
        return this.createSchemas;
    }

    public void setCreateSchemas(boolean createSchemas) {
        this.createSchemas = createSchemas;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTablespace() {
        return this.tablespace;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    public String getBaselineDescription() {
        return this.baselineDescription;
    }

    public void setBaselineDescription(String baselineDescription) {
        this.baselineDescription = baselineDescription;
    }

    public String getBaselineVersion() {
        return this.baselineVersion;
    }

    public void setBaselineVersion(String baselineVersion) {
        this.baselineVersion = baselineVersion;
    }

    public String getInstalledBy() {
        return this.installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public Map<String, String> getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getPlaceholderPrefix() {
        return this.placeholderPrefix;
    }

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public String getPlaceholderSuffix() {
        return this.placeholderSuffix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    public String getPlaceholderSeparator() {
        return this.placeholderSeparator;
    }

    public void setPlaceholderSeparator(String placeholderSeparator) {
        this.placeholderSeparator = placeholderSeparator;
    }

    public boolean isPlaceholderReplacement() {
        return this.placeholderReplacement;
    }

    public void setPlaceholderReplacement(boolean placeholderReplacement) {
        this.placeholderReplacement = placeholderReplacement;
    }

    public String getSqlMigrationPrefix() {
        return this.sqlMigrationPrefix;
    }

    public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
        this.sqlMigrationPrefix = sqlMigrationPrefix;
    }

    public List<String> getSqlMigrationSuffixes() {
        return this.sqlMigrationSuffixes;
    }

    public void setSqlMigrationSuffixes(List<String> sqlMigrationSuffixes) {
        this.sqlMigrationSuffixes = sqlMigrationSuffixes;
    }

    public String getSqlMigrationSeparator() {
        return this.sqlMigrationSeparator;
    }

    public void setSqlMigrationSeparator(String sqlMigrationSeparator) {
        this.sqlMigrationSeparator = sqlMigrationSeparator;
    }

    public String getRepeatableSqlMigrationPrefix() {
        return this.repeatableSqlMigrationPrefix;
    }

    public void setRepeatableSqlMigrationPrefix(String repeatableSqlMigrationPrefix) {
        this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getInitSqls() {
        return this.initSqls;
    }

    public void setInitSqls(List<String> initSqls) {
        this.initSqls = initSqls;
    }

    public boolean isBaselineOnMigrate() {
        return this.baselineOnMigrate;
    }

    public void setBaselineOnMigrate(boolean baselineOnMigrate) {
        this.baselineOnMigrate = baselineOnMigrate;
    }

    public boolean isCleanDisabled() {
        return this.cleanDisabled;
    }

    public void setCleanDisabled(boolean cleanDisabled) {
        this.cleanDisabled = cleanDisabled;
    }

    public boolean isCleanOnValidationError() {
        return this.cleanOnValidationError;
    }

    public void setCleanOnValidationError(boolean cleanOnValidationError) {
        this.cleanOnValidationError = cleanOnValidationError;
    }

    public boolean isGroup() {
        return this.group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.flyway.ignore-migration-patterns")
    @Deprecated
    public boolean isIgnoreMissingMigrations() {
        return this.ignoreMissingMigrations;
    }

    @Deprecated
    public void setIgnoreMissingMigrations(boolean ignoreMissingMigrations) {
        this.ignoreMissingMigrations = ignoreMissingMigrations;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.flyway.ignore-migration-patterns")
    @Deprecated
    public boolean isIgnoreIgnoredMigrations() {
        return this.ignoreIgnoredMigrations;
    }

    @Deprecated
    public void setIgnoreIgnoredMigrations(boolean ignoreIgnoredMigrations) {
        this.ignoreIgnoredMigrations = ignoreIgnoredMigrations;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.flyway.ignore-migration-patterns")
    @Deprecated
    public boolean isIgnorePendingMigrations() {
        return this.ignorePendingMigrations;
    }

    @Deprecated
    public void setIgnorePendingMigrations(boolean ignorePendingMigrations) {
        this.ignorePendingMigrations = ignorePendingMigrations;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.flyway.ignore-migration-patterns")
    @Deprecated
    public boolean isIgnoreFutureMigrations() {
        return this.ignoreFutureMigrations;
    }

    @Deprecated
    public void setIgnoreFutureMigrations(boolean ignoreFutureMigrations) {
        this.ignoreFutureMigrations = ignoreFutureMigrations;
    }

    public boolean isMixed() {
        return this.mixed;
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public boolean isOutOfOrder() {
        return this.outOfOrder;
    }

    public void setOutOfOrder(boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public boolean isSkipDefaultCallbacks() {
        return this.skipDefaultCallbacks;
    }

    public void setSkipDefaultCallbacks(boolean skipDefaultCallbacks) {
        this.skipDefaultCallbacks = skipDefaultCallbacks;
    }

    public boolean isSkipDefaultResolvers() {
        return this.skipDefaultResolvers;
    }

    public void setSkipDefaultResolvers(boolean skipDefaultResolvers) {
        this.skipDefaultResolvers = skipDefaultResolvers;
    }

    public boolean isValidateMigrationNaming() {
        return this.validateMigrationNaming;
    }

    public void setValidateMigrationNaming(boolean validateMigrationNaming) {
        this.validateMigrationNaming = validateMigrationNaming;
    }

    public boolean isValidateOnMigrate() {
        return this.validateOnMigrate;
    }

    public void setValidateOnMigrate(boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
    }

    public Boolean getBatch() {
        return this.batch;
    }

    public void setBatch(Boolean batch) {
        this.batch = batch;
    }

    public File getDryRunOutput() {
        return this.dryRunOutput;
    }

    public void setDryRunOutput(File dryRunOutput) {
        this.dryRunOutput = dryRunOutput;
    }

    public String[] getErrorOverrides() {
        return this.errorOverrides;
    }

    public void setErrorOverrides(String[] errorOverrides) {
        this.errorOverrides = errorOverrides;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public Boolean getOracleSqlplus() {
        return this.oracleSqlplus;
    }

    public void setOracleSqlplus(Boolean oracleSqlplus) {
        this.oracleSqlplus = oracleSqlplus;
    }

    public Boolean getOracleSqlplusWarn() {
        return this.oracleSqlplusWarn;
    }

    public void setOracleSqlplusWarn(Boolean oracleSqlplusWarn) {
        this.oracleSqlplusWarn = oracleSqlplusWarn;
    }

    public String getOracleWalletLocation() {
        return this.oracleWalletLocation;
    }

    public void setOracleWalletLocation(String oracleWalletLocation) {
        this.oracleWalletLocation = oracleWalletLocation;
    }

    public Boolean getStream() {
        return this.stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public String getUndoSqlMigrationPrefix() {
        return this.undoSqlMigrationPrefix;
    }

    public void setUndoSqlMigrationPrefix(String undoSqlMigrationPrefix) {
        this.undoSqlMigrationPrefix = undoSqlMigrationPrefix;
    }

    public String[] getCherryPick() {
        return this.cherryPick;
    }

    public void setCherryPick(String[] cherryPick) {
        this.cherryPick = cherryPick;
    }

    public Map<String, String> getJdbcProperties() {
        return this.jdbcProperties;
    }

    public void setJdbcProperties(Map<String, String> jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public String getKerberosConfigFile() {
        return this.kerberosConfigFile;
    }

    public void setKerberosConfigFile(String kerberosConfigFile) {
        this.kerberosConfigFile = kerberosConfigFile;
    }

    public String getOracleKerberosCacheFile() {
        return this.oracleKerberosCacheFile;
    }

    public void setOracleKerberosCacheFile(String oracleKerberosCacheFile) {
        this.oracleKerberosCacheFile = oracleKerberosCacheFile;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.flyway.kerberos-config-file")
    @Deprecated
    public String getOracleKerberosConfigFile() {
        return getKerberosConfigFile();
    }

    @Deprecated
    public void setOracleKerberosConfigFile(String oracleKerberosConfigFile) {
        setKerberosConfigFile(oracleKerberosConfigFile);
    }

    public Boolean getOutputQueryResults() {
        return this.outputQueryResults;
    }

    public void setOutputQueryResults(Boolean outputQueryResults) {
        this.outputQueryResults = outputQueryResults;
    }

    public String getSqlServerKerberosLoginFile() {
        return this.sqlServerKerberosLoginFile;
    }

    public void setSqlServerKerberosLoginFile(String sqlServerKerberosLoginFile) {
        this.sqlServerKerberosLoginFile = sqlServerKerberosLoginFile;
    }

    public Boolean getSkipExecutingMigrations() {
        return this.skipExecutingMigrations;
    }

    public void setSkipExecutingMigrations(Boolean skipExecutingMigrations) {
        this.skipExecutingMigrations = skipExecutingMigrations;
    }

    public List<String> getIgnoreMigrationPatterns() {
        return this.ignoreMigrationPatterns;
    }

    public void setIgnoreMigrationPatterns(List<String> ignoreMigrationPatterns) {
        this.ignoreMigrationPatterns = ignoreMigrationPatterns;
    }

    public Boolean getDetectEncoding() {
        return this.detectEncoding;
    }

    public void setDetectEncoding(final Boolean detectEncoding) {
        this.detectEncoding = detectEncoding;
    }

    public String getBaselineMigrationPrefix() {
        return this.baselineMigrationPrefix;
    }

    public void setBaselineMigrationPrefix(String baselineMigrationPrefix) {
        this.baselineMigrationPrefix = baselineMigrationPrefix;
    }

    public String getScriptPlaceholderPrefix() {
        return this.scriptPlaceholderPrefix;
    }

    public void setScriptPlaceholderPrefix(String scriptPlaceholderPrefix) {
        this.scriptPlaceholderPrefix = scriptPlaceholderPrefix;
    }

    public String getScriptPlaceholderSuffix() {
        return this.scriptPlaceholderSuffix;
    }

    public void setScriptPlaceholderSuffix(String scriptPlaceholderSuffix) {
        this.scriptPlaceholderSuffix = scriptPlaceholderSuffix;
    }
}
