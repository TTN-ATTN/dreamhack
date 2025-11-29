package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({MongoDatabaseFactory.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class */
class MongoDatabaseFactoryDependentConfiguration {
    private final MongoProperties properties;

    MongoDatabaseFactoryDependentConfiguration(MongoProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({MongoOperations.class})
    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    @ConditionalOnMissingBean({MongoConverter.class})
    @Bean
    MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @ConditionalOnMissingBean({GridFsOperations.class})
    @Bean
    GridFsTemplate gridFsTemplate(MongoDatabaseFactory factory, MongoTemplate mongoTemplate) {
        return new GridFsTemplate(new GridFsMongoDatabaseFactory(factory, this.properties), mongoTemplate.getConverter(), this.properties.getGridfs().getBucket());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration$GridFsMongoDatabaseFactory.class */
    static class GridFsMongoDatabaseFactory implements MongoDatabaseFactory {
        private final MongoDatabaseFactory mongoDatabaseFactory;
        private final MongoProperties properties;

        GridFsMongoDatabaseFactory(MongoDatabaseFactory mongoDatabaseFactory, MongoProperties properties) {
            Assert.notNull(mongoDatabaseFactory, "MongoDatabaseFactory must not be null");
            Assert.notNull(properties, "Properties must not be null");
            this.mongoDatabaseFactory = mongoDatabaseFactory;
            this.properties = properties;
        }

        public MongoDatabase getMongoDatabase() throws DataAccessException {
            String gridFsDatabase = this.properties.getGridfs().getDatabase();
            if (StringUtils.hasText(gridFsDatabase)) {
                return this.mongoDatabaseFactory.getMongoDatabase(gridFsDatabase);
            }
            return this.mongoDatabaseFactory.getMongoDatabase();
        }

        public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
            return this.mongoDatabaseFactory.getMongoDatabase(dbName);
        }

        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.mongoDatabaseFactory.getExceptionTranslator();
        }

        public ClientSession getSession(ClientSessionOptions options) {
            return this.mongoDatabaseFactory.getSession(options);
        }

        public MongoDatabaseFactory withSession(ClientSession session) {
            return this.mongoDatabaseFactory.withSession(session);
        }
    }
}
