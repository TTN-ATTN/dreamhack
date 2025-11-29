package ch.qos.logback.core.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.joran.spi.SimpleRuleStore;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/joran/GenericConfigurator.class */
public abstract class GenericConfigurator extends ContextAwareBase {
    private BeanDescriptionCache beanDescriptionCache;
    protected Interpreter interpreter;

    protected abstract void addInstanceRules(RuleStore ruleStore);

    protected abstract void addImplicitRules(Interpreter interpreter);

    public final void doConfigure(URL url) throws JoranException, IOException {
        InputStream in = null;
        try {
            try {
                informContextOfURLUsedForConfiguration(getContext(), url);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setUseCaches(false);
                in = urlConnection.getInputStream();
                doConfigure(in, url.toExternalForm());
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ioe) {
                        addError("Could not close input stream", ioe);
                        throw new JoranException("Could not close input stream", ioe);
                    }
                }
            } catch (IOException ioe2) {
                String errMsg = "Could not open URL [" + url + "].";
                addError(errMsg, ioe2);
                throw new JoranException(errMsg, ioe2);
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe3) {
                    addError("Could not close input stream", ioe3);
                    throw new JoranException("Could not close input stream", ioe3);
                }
            }
            throw th;
        }
    }

    public final void doConfigure(String filename) throws JoranException, IOException {
        doConfigure(new File(filename));
    }

    public final void doConfigure(File file) throws JoranException, IOException {
        FileInputStream fis = null;
        try {
            try {
                URL url = file.toURI().toURL();
                informContextOfURLUsedForConfiguration(getContext(), url);
                fis = new FileInputStream(file);
                doConfigure(fis, url.toExternalForm());
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ioe) {
                        String errMsg = "Could not close [" + file.getName() + "].";
                        addError(errMsg, ioe);
                        throw new JoranException(errMsg, ioe);
                    }
                }
            } catch (IOException ioe2) {
                String errMsg2 = "Could not open [" + file.getPath() + "].";
                addError(errMsg2, ioe2);
                throw new JoranException(errMsg2, ioe2);
            }
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe3) {
                    String errMsg3 = "Could not close [" + file.getName() + "].";
                    addError(errMsg3, ioe3);
                    throw new JoranException(errMsg3, ioe3);
                }
            }
            throw th;
        }
    }

    public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
        ConfigurationWatchListUtil.setMainWatchURL(context, url);
    }

    public final void doConfigure(InputStream inputStream) throws ParserConfigurationException, SAXException, JoranException, IOException {
        doConfigure(new InputSource(inputStream));
    }

    public final void doConfigure(InputStream inputStream, String systemId) throws ParserConfigurationException, SAXException, JoranException, IOException {
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(systemId);
        doConfigure(inputSource);
    }

    protected BeanDescriptionCache getBeanDescriptionCache() {
        if (this.beanDescriptionCache == null) {
            this.beanDescriptionCache = new BeanDescriptionCache(getContext());
        }
        return this.beanDescriptionCache;
    }

    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
    }

    protected ElementPath initialElementPath() {
        return new ElementPath();
    }

    protected void buildInterpreter() {
        RuleStore rs = new SimpleRuleStore(this.context);
        addInstanceRules(rs);
        this.interpreter = new Interpreter(this.context, rs, initialElementPath());
        InterpretationContext interpretationContext = this.interpreter.getInterpretationContext();
        interpretationContext.setContext(this.context);
        addImplicitRules(this.interpreter);
        addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
    }

    public final void doConfigure(InputSource inputSource) throws ParserConfigurationException, SAXException, JoranException, IOException {
        long threshold = System.currentTimeMillis();
        SaxEventRecorder recorder = new SaxEventRecorder(this.context);
        recorder.recordEvents(inputSource);
        doConfigure(recorder.saxEventList);
        StatusUtil statusUtil = new StatusUtil(this.context);
        if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
            addInfo("Registering current configuration as safe fallback point");
            registerSafeConfiguration(recorder.saxEventList);
        }
    }

    public void doConfigure(List<SaxEvent> eventList) throws JoranException {
        buildInterpreter();
        synchronized (this.context.getConfigurationLock()) {
            this.interpreter.getEventPlayer().play(eventList);
        }
    }

    public void registerSafeConfiguration(List<SaxEvent> eventList) {
        this.context.putObject(CoreConstants.SAFE_JORAN_CONFIGURATION, eventList);
    }

    public List<SaxEvent> recallSafeConfiguration() {
        return (List) this.context.getObject(CoreConstants.SAFE_JORAN_CONFIGURATION);
    }
}
