package org.springframework.boot.diagnostics;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.boot.util.Instantiator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/FailureAnalyzers.class */
final class FailureAnalyzers implements SpringBootExceptionReporter {
    private static final Log logger = LogFactory.getLog((Class<?>) FailureAnalyzers.class);
    private final ClassLoader classLoader;
    private final List<FailureAnalyzer> analyzers;

    FailureAnalyzers(ConfigurableApplicationContext context) {
        this(context, SpringFactoriesLoader.loadFactoryNames(FailureAnalyzer.class, getClassLoader(context)));
    }

    FailureAnalyzers(ConfigurableApplicationContext context, List<String> classNames) {
        this.classLoader = getClassLoader(context);
        this.analyzers = loadFailureAnalyzers(classNames, context);
    }

    private static ClassLoader getClassLoader(ConfigurableApplicationContext context) {
        if (context != null) {
            return context.getClassLoader();
        }
        return null;
    }

    private List<FailureAnalyzer> loadFailureAnalyzers(List<String> classNames, ConfigurableApplicationContext context) {
        Instantiator<FailureAnalyzer> instantiator = new Instantiator<>(FailureAnalyzer.class, availableParameters -> {
            if (context != null) {
                availableParameters.add(BeanFactory.class, context.getBeanFactory());
                availableParameters.add(Environment.class, context.getEnvironment());
            }
        }, new LoggingInstantiationFailureHandler());
        List<FailureAnalyzer> analyzers = instantiator.instantiate(this.classLoader, classNames);
        return handleAwareAnalyzers(analyzers, context);
    }

    private List<FailureAnalyzer> handleAwareAnalyzers(List<FailureAnalyzer> analyzers, ConfigurableApplicationContext context) {
        List<FailureAnalyzer> awareAnalyzers = (List) analyzers.stream().filter(analyzer -> {
            return (analyzer instanceof BeanFactoryAware) || (analyzer instanceof EnvironmentAware);
        }).collect(Collectors.toList());
        if (!awareAnalyzers.isEmpty()) {
            String awareAnalyzerNames = StringUtils.collectionToCommaDelimitedString((Collection) awareAnalyzers.stream().map(analyzer2 -> {
                return analyzer2.getClass().getName();
            }).collect(Collectors.toList()));
            logger.warn(LogMessage.format("FailureAnalyzers [%s] implement BeanFactoryAware or EnvironmentAware. Support for these interfaces on FailureAnalyzers is deprecated, and will be removed in a future release. Instead provide a constructor that accepts BeanFactory or Environment parameters.", awareAnalyzerNames));
            if (context == null) {
                logger.trace(LogMessage.format("Skipping [%s] due to missing context", awareAnalyzerNames));
                return (List) analyzers.stream().filter(analyzer3 -> {
                    return !awareAnalyzers.contains(analyzer3);
                }).collect(Collectors.toList());
            }
            awareAnalyzers.forEach(analyzer4 -> {
                if (analyzer4 instanceof BeanFactoryAware) {
                    ((BeanFactoryAware) analyzer4).setBeanFactory(context.getBeanFactory());
                }
                if (analyzer4 instanceof EnvironmentAware) {
                    ((EnvironmentAware) analyzer4).setEnvironment(context.getEnvironment());
                }
            });
        }
        return analyzers;
    }

    @Override // org.springframework.boot.SpringBootExceptionReporter
    public boolean reportException(Throwable failure) {
        FailureAnalysis analysis = analyze(failure, this.analyzers);
        return report(analysis, this.classLoader);
    }

    private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
        FailureAnalysis analysis;
        for (FailureAnalyzer analyzer : analyzers) {
            try {
                analysis = analyzer.analyze(failure);
            } catch (Throwable ex) {
                logger.trace(LogMessage.format("FailureAnalyzer %s failed", analyzer), ex);
            }
            if (analysis != null) {
                return analysis;
            }
        }
        return null;
    }

    private boolean report(FailureAnalysis analysis, ClassLoader classLoader) {
        List<FailureAnalysisReporter> reporters = SpringFactoriesLoader.loadFactories(FailureAnalysisReporter.class, classLoader);
        if (analysis == null || reporters.isEmpty()) {
            return false;
        }
        for (FailureAnalysisReporter reporter : reporters) {
            reporter.report(analysis);
        }
        return true;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/FailureAnalyzers$LoggingInstantiationFailureHandler.class */
    static class LoggingInstantiationFailureHandler implements Instantiator.FailureHandler {
        LoggingInstantiationFailureHandler() {
        }

        @Override // org.springframework.boot.util.Instantiator.FailureHandler
        public void handleFailure(Class<?> type, String implementationName, Throwable failure) {
            FailureAnalyzers.logger.trace(LogMessage.format("Skipping %s: %s", implementationName, failure.getMessage()));
        }
    }
}
