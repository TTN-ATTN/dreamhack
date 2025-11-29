package org.springframework.http.codec.xml;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/xml/Jaxb2XmlDecoder.class */
public class Jaxb2XmlDecoder extends AbstractDecoder<Object> {
    private static final String JAXB_DEFAULT_ANNOTATION_VALUE = "##default";
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private final XmlEventDecoder xmlEventDecoder;
    private final JaxbContextContainer jaxbContexts;
    private Function<Unmarshaller, Unmarshaller> unmarshallerProcessor;
    private int maxInMemorySize;

    public Jaxb2XmlDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, new MediaType("application", "*+xml"));
        this.xmlEventDecoder = new XmlEventDecoder();
        this.jaxbContexts = new JaxbContextContainer();
        this.unmarshallerProcessor = Function.identity();
        this.maxInMemorySize = 262144;
    }

    public Jaxb2XmlDecoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
        this.xmlEventDecoder = new XmlEventDecoder();
        this.jaxbContexts = new JaxbContextContainer();
        this.unmarshallerProcessor = Function.identity();
        this.maxInMemorySize = 262144;
    }

    public void setUnmarshallerProcessor(Function<Unmarshaller, Unmarshaller> processor) {
        this.unmarshallerProcessor = this.unmarshallerProcessor.andThen(processor);
    }

    public Function<Unmarshaller, Unmarshaller> getUnmarshallerProcessor() {
        return this.unmarshallerProcessor;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
        this.xmlEventDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> outputClass = elementType.toClass();
        return (outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class)) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<XMLEvent> xmlEventFlux = this.xmlEventDecoder.decode(inputStream, ResolvableType.forClass(XMLEvent.class), mimeType, hints);
        Class<?> outputClass = elementType.toClass();
        QName typeName = toQName(outputClass);
        Flux<List<XMLEvent>> splitEvents = split(xmlEventFlux, typeName);
        return splitEvents.map(events -> {
            Object value = unmarshal(events, outputClass);
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
                return Hints.getLogPrefix(hints) + "Decoded [" + formatted + "]";
            });
            return value;
        });
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(input, this.maxInMemorySize).map(dataBuffer -> {
            return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    @Override // org.springframework.core.codec.Decoder
    public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        try {
            try {
                try {
                    XMLEventReader xMLEventReaderCreateXMLEventReader = inputFactory.createXMLEventReader(dataBuffer.asInputStream(), encoding(mimeType));
                    List<XMLEvent> events = new ArrayList<>();
                    xMLEventReaderCreateXMLEventReader.forEachRemaining(event -> {
                        events.add((XMLEvent) event);
                    });
                    Object objUnmarshal = unmarshal(events, targetType.toClass());
                    DataBufferUtils.release(dataBuffer);
                    return objUnmarshal;
                } catch (Throwable ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof XMLStreamException) {
                        throw new DecodingException(cause.getMessage(), cause);
                    }
                    throw Exceptions.propagate(ex);
                }
            } catch (XMLStreamException ex2) {
                throw new DecodingException(ex2.getMessage(), ex2);
            }
        } catch (Throwable th) {
            DataBufferUtils.release(dataBuffer);
            throw th;
        }
    }

    @Nullable
    private static String encoding(@Nullable MimeType mimeType) {
        Charset charset;
        if (mimeType == null || (charset = mimeType.getCharset()) == null) {
            return null;
        }
        return charset.name();
    }

    private Object unmarshal(List<XMLEvent> events, Class<?> outputClass) throws CodecException {
        try {
            Unmarshaller unmarshaller = initUnmarshaller(outputClass);
            XMLEventReader eventReader = StaxUtils.createXMLEventReader(events);
            if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
                return unmarshaller.unmarshal(eventReader);
            }
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, outputClass);
            return jaxbElement.getValue();
        } catch (UnmarshalException ex) {
            throw new DecodingException("Could not unmarshal XML to " + outputClass, ex);
        } catch (JAXBException ex2) {
            throw new CodecException("Invalid JAXB configuration", ex2);
        }
    }

    private Unmarshaller initUnmarshaller(Class<?> outputClass) throws JAXBException, CodecException {
        Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
        return this.unmarshallerProcessor.apply(unmarshaller);
    }

    QName toQName(Class<?> outputClass) {
        String localPart;
        String namespaceUri;
        if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
            XmlRootElement annotation = outputClass.getAnnotation(XmlRootElement.class);
            localPart = annotation.name();
            namespaceUri = annotation.namespace();
        } else if (outputClass.isAnnotationPresent(XmlType.class)) {
            XmlType annotation2 = outputClass.getAnnotation(XmlType.class);
            localPart = annotation2.name();
            namespaceUri = annotation2.namespace();
        } else {
            throw new IllegalArgumentException("Output class [" + outputClass.getName() + "] is neither annotated with @XmlRootElement nor @XmlType");
        }
        if ("##default".equals(localPart)) {
            localPart = ClassUtils.getShortNameAsProperty(outputClass);
        }
        if ("##default".equals(namespaceUri)) {
            Package outputClassPackage = outputClass.getPackage();
            if (outputClassPackage != null && outputClassPackage.isAnnotationPresent(XmlSchema.class)) {
                namespaceUri = outputClassPackage.getAnnotation(XmlSchema.class).namespace();
            } else {
                namespaceUri = "";
            }
        }
        return new QName(namespaceUri, localPart);
    }

    Flux<List<XMLEvent>> split(Flux<XMLEvent> xmlEventFlux, QName desiredName) {
        return xmlEventFlux.handle(new SplitHandler(desiredName));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/xml/Jaxb2XmlDecoder$SplitHandler.class */
    private static class SplitHandler implements BiConsumer<XMLEvent, SynchronousSink<List<XMLEvent>>> {
        private final QName desiredName;

        @Nullable
        private List<XMLEvent> events;
        private int elementDepth = 0;
        private int barrier = Integer.MAX_VALUE;

        public SplitHandler(QName desiredName) {
            this.desiredName = desiredName;
        }

        @Override // java.util.function.BiConsumer
        public void accept(XMLEvent event, SynchronousSink<List<XMLEvent>> sink) {
            if (event.isStartElement()) {
                if (this.barrier == Integer.MAX_VALUE) {
                    QName startElementName = event.asStartElement().getName();
                    if (this.desiredName.equals(startElementName)) {
                        this.events = new ArrayList();
                        this.barrier = this.elementDepth;
                    }
                }
                this.elementDepth++;
            }
            if (this.elementDepth > this.barrier) {
                Assert.state(this.events != null, "No XMLEvent List");
                this.events.add(event);
            }
            if (event.isEndElement()) {
                this.elementDepth--;
                if (this.elementDepth == this.barrier) {
                    this.barrier = Integer.MAX_VALUE;
                    Assert.state(this.events != null, "No XMLEvent List");
                    sink.next(this.events);
                }
            }
        }
    }
}
