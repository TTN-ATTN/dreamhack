package org.springframework.util.xml;

import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/xml/AbstractXMLEventReader.class */
abstract class AbstractXMLEventReader implements XMLEventReader {
    private boolean closed;

    AbstractXMLEventReader() {
    }

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException ex) {
            throw new NoSuchElementException(ex.getMessage());
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported on " + ClassUtils.getShortName(getClass()));
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Property not supported: [" + name + "]");
    }

    public void close() {
        this.closed = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkIfClosed() throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("XMLEventReader has been closed");
        }
    }
}
