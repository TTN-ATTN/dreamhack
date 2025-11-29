package org.springframework.http.codec.xml;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/xml/XmlEventDecoder.class */
public class XmlEventDecoder extends AbstractDecoder<XMLEvent> {
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private static final boolean aaltoPresent = ClassUtils.isPresent("com.fasterxml.aalto.AsyncXMLStreamReader", XmlEventDecoder.class.getClassLoader());
    boolean useAalto;
    private int maxInMemorySize;

    public XmlEventDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, new MediaType("application", "*+xml"));
        this.useAalto = aaltoPresent;
        this.maxInMemorySize = 262144;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<XMLEvent> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.useAalto) {
            AaltoDataBufferToXmlEvent mapper = new AaltoDataBufferToXmlEvent(this.maxInMemorySize);
            return Flux.from(input).flatMapIterable(mapper).doFinally(signalType -> {
                mapper.endOfInput();
            });
        }
        return DataBufferUtils.join(input, this.maxInMemorySize).flatMapIterable(buffer -> {
            try {
                try {
                    InputStream is = buffer.asInputStream();
                    XMLEventReader xMLEventReaderCreateXMLEventReader = inputFactory.createXMLEventReader(is);
                    List<XMLEvent> result = new ArrayList<>();
                    xMLEventReaderCreateXMLEventReader.forEachRemaining(event -> {
                        result.add((XMLEvent) event);
                    });
                    DataBufferUtils.release(buffer);
                    return result;
                } catch (XMLStreamException ex) {
                    throw new DecodingException(ex.getMessage(), ex);
                }
            } catch (Throwable th) {
                DataBufferUtils.release(buffer);
                throw th;
            }
        });
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/xml/XmlEventDecoder$AaltoDataBufferToXmlEvent.class */
    private static class AaltoDataBufferToXmlEvent implements Function<DataBuffer, List<? extends XMLEvent>> {
        private static final AsyncXMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory(InputFactoryImpl::new);
        private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader = inputFactory.createAsyncForByteBuffer();
        private final XMLEventAllocator eventAllocator = EventAllocatorImpl.getDefaultInstance();
        private final int maxInMemorySize;
        private int byteCount;
        private int elementDepth;

        public AaltoDataBufferToXmlEvent(int maxInMemorySize) {
            this.maxInMemorySize = maxInMemorySize;
        }

        @Override // java.util.function.Function
        public List<? extends XMLEvent> apply(DataBuffer dataBuffer) {
            try {
                try {
                    increaseByteCount(dataBuffer);
                    this.streamReader.getInputFeeder().feedInput(dataBuffer.asByteBuffer());
                    List<XMLEvent> events = new ArrayList<>();
                    while (this.streamReader.next() != 257) {
                        XMLEvent event = this.eventAllocator.allocate(this.streamReader);
                        events.add(event);
                        if (event.isEndDocument()) {
                            break;
                        }
                        checkDepthAndResetByteCount(event);
                    }
                    if (this.maxInMemorySize > 0 && this.byteCount > this.maxInMemorySize) {
                        raiseLimitException();
                    }
                    return events;
                } catch (XMLStreamException ex) {
                    throw new DecodingException(ex.getMessage(), ex);
                }
            } finally {
                DataBufferUtils.release(dataBuffer);
            }
        }

        private void increaseByteCount(DataBuffer dataBuffer) {
            if (this.maxInMemorySize > 0) {
                if (dataBuffer.readableByteCount() > Integer.MAX_VALUE - this.byteCount) {
                    raiseLimitException();
                } else {
                    this.byteCount += dataBuffer.readableByteCount();
                }
            }
        }

        private void checkDepthAndResetByteCount(XMLEvent event) {
            if (this.maxInMemorySize > 0) {
                if (event.isStartElement()) {
                    this.byteCount = this.elementDepth == 1 ? 0 : this.byteCount;
                    this.elementDepth++;
                } else if (event.isEndElement()) {
                    this.elementDepth--;
                    this.byteCount = this.elementDepth == 1 ? 0 : this.byteCount;
                }
            }
        }

        private void raiseLimitException() {
            throw new DataBufferLimitException("Exceeded limit on max bytes per XML top-level node: " + this.maxInMemorySize);
        }

        public void endOfInput() {
            this.streamReader.getInputFeeder().endOfInput();
        }
    }
}
