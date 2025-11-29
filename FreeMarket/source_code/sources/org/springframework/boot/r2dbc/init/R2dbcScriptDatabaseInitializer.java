package org.springframework.boot.r2dbc.init;

import io.r2dbc.spi.ConnectionFactory;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.boot.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/r2dbc/init/R2dbcScriptDatabaseInitializer.class */
public class R2dbcScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {
    private final ConnectionFactory connectionFactory;

    public R2dbcScriptDatabaseInitializer(ConnectionFactory connectionFactory, DatabaseInitializationSettings settings) {
        super(settings);
        this.connectionFactory = connectionFactory;
    }

    @Override // org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer
    protected boolean isEmbeddedDatabase() {
        return EmbeddedDatabaseConnection.isEmbedded(this.connectionFactory);
    }

    @Override // org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer
    protected void runScripts(List<Resource> scripts, boolean continueOnError, String separator, Charset encoding) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(continueOnError);
        populator.setSeparator(separator);
        if (encoding != null) {
            populator.setSqlScriptEncoding(encoding.name());
        }
        for (Resource script : scripts) {
            populator.addScript(script);
        }
        populator.populate(this.connectionFactory).block();
    }
}
