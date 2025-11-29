package org.springframework.jmx.export.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/annotation/AnnotationMBeanExporter.class */
public class AnnotationMBeanExporter extends MBeanExporter {
    private final AnnotationJmxAttributeSource annotationSource = new AnnotationJmxAttributeSource();
    private final MetadataNamingStrategy metadataNamingStrategy = new MetadataNamingStrategy(this.annotationSource);
    private final MetadataMBeanInfoAssembler metadataAssembler = new MetadataMBeanInfoAssembler(this.annotationSource);

    public AnnotationMBeanExporter() {
        setNamingStrategy(this.metadataNamingStrategy);
        setAssembler(this.metadataAssembler);
        setAutodetectMode(3);
    }

    public void setDefaultDomain(String defaultDomain) {
        this.metadataNamingStrategy.setDefaultDomain(defaultDomain);
    }

    @Override // org.springframework.jmx.export.MBeanExporter, org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.annotationSource.setBeanFactory(beanFactory);
    }
}
