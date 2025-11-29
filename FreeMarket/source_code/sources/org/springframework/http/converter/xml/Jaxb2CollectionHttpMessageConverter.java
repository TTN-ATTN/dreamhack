package org.springframework.http.converter.xml;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.xml.StaxUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/converter/xml/Jaxb2CollectionHttpMessageConverter.class */
public class Jaxb2CollectionHttpMessageConverter<T extends Collection> extends AbstractJaxb2HttpMessageConverter<T> implements GenericHttpMessageConverter<T> {
    private final XMLInputFactory inputFactory = createXmlInputFactory();

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public /* bridge */ /* synthetic */ Object read(Type type, @Nullable Class contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return read(type, (Class<?>) contextClass, inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (!(parameterizedType.getRawType() instanceof Class)) {
            return false;
        }
        Class<?> rawType = (Class) parameterizedType.getRawType();
        if (!Collection.class.isAssignableFrom(rawType) || parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class<?> typeArgumentClass = (Class) typeArgument;
        return (typeArgumentClass.isAnnotationPresent(XmlRootElement.class) || typeArgumentClass.isAnnotationPresent(XmlType.class)) && canRead(mediaType);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    public T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public T read(Type type, @Nullable Class<?> cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        T t = (T) createCollection((Class) parameterizedType.getRawType());
        Class<?> cls2 = (Class) parameterizedType.getActualTypeArguments()[0];
        try {
            Unmarshaller unmarshallerCreateUnmarshaller = createUnmarshaller(cls2);
            XMLStreamReader xMLStreamReaderCreateXMLStreamReader = this.inputFactory.createXMLStreamReader(httpInputMessage.getBody());
            int iMoveToFirstChildOfRootElement = moveToFirstChildOfRootElement(xMLStreamReaderCreateXMLStreamReader);
            while (iMoveToFirstChildOfRootElement != 8) {
                if (cls2.isAnnotationPresent(XmlRootElement.class)) {
                    t.add(unmarshallerCreateUnmarshaller.unmarshal(xMLStreamReaderCreateXMLStreamReader));
                } else if (cls2.isAnnotationPresent(XmlType.class)) {
                    t.add(unmarshallerCreateUnmarshaller.unmarshal(xMLStreamReaderCreateXMLStreamReader, cls2).getValue());
                } else {
                    throw new HttpMessageNotReadableException("Cannot unmarshal to [" + cls2 + "]", httpInputMessage);
                }
                iMoveToFirstChildOfRootElement = moveToNextElement(xMLStreamReaderCreateXMLStreamReader);
            }
            return t;
        } catch (JAXBException e) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + e.getMessage(), e);
        } catch (XMLStreamException e2) {
            throw new HttpMessageNotReadableException("Failed to read XML stream: " + e2.getMessage(), e2, httpInputMessage);
        } catch (UnmarshalException e3) {
            throw new HttpMessageNotReadableException("Could not unmarshal to [" + cls2 + "]: " + e3, e3, httpInputMessage);
        }
    }

    protected T createCollection(Class<?> collectionClass) {
        if (!collectionClass.isInterface()) {
            try {
                return (T) ReflectionUtils.accessibleConstructor(collectionClass, new Class[0]).newInstance(new Object[0]);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate collection class: " + collectionClass.getName(), ex);
            }
        }
        if (List.class == collectionClass) {
            return new ArrayList();
        }
        if (SortedSet.class == collectionClass) {
            return new TreeSet();
        }
        return new LinkedHashSet();
    }

    private int moveToFirstChildOfRootElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event;
        int event2 = streamReader.next();
        while (event2 != 1) {
            event2 = streamReader.next();
        }
        int next = streamReader.next();
        while (true) {
            event = next;
            if (event == 1 || event == 8) {
                break;
            }
            next = streamReader.next();
        }
        return event;
    }

    private int moveToNextElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event;
        int eventType = streamReader.getEventType();
        while (true) {
            event = eventType;
            if (event == 1 || event == 8) {
                break;
            }
            eventType = streamReader.next();
        }
        return event;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    public void writeToResult(T t, HttpHeaders headers, Result result) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected XMLInputFactory createXmlInputFactory() {
        return StaxUtils.createDefensiveInputFactory();
    }
}
