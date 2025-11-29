package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.springframework.beans.PropertyAccessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/pattern/parser/Compiler.class */
class Compiler<E> extends ContextAwareBase {
    Converter<E> head;
    Converter<E> tail;
    final Node top;
    final Map converterMap;

    Compiler(Node top, Map converterMap) {
        this.top = top;
        this.converterMap = converterMap;
    }

    Converter<E> compile() {
        this.tail = null;
        this.head = null;
        Node node = this.top;
        while (true) {
            Node n = node;
            if (n != null) {
                switch (n.type) {
                    case 0:
                        addToList(new LiteralConverter((String) n.getValue()));
                        break;
                    case 1:
                        SimpleKeywordNode kn = (SimpleKeywordNode) n;
                        DynamicConverter<E> dynaConverter = createConverter(kn);
                        if (dynaConverter != null) {
                            dynaConverter.setFormattingInfo(kn.getFormatInfo());
                            dynaConverter.setOptionList(kn.getOptions());
                            addToList(dynaConverter);
                            break;
                        } else {
                            Converter<E> errConveter = new LiteralConverter<>("%PARSER_ERROR[" + kn.getValue() + "]");
                            addStatus(new ErrorStatus(PropertyAccessor.PROPERTY_KEY_PREFIX + kn.getValue() + "] is not a valid conversion word", this));
                            addToList(errConveter);
                            break;
                        }
                    case 2:
                        CompositeNode cn = (CompositeNode) n;
                        CompositeConverter<E> compositeConverter = createCompositeConverter(cn);
                        if (compositeConverter == null) {
                            addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
                            addToList(new LiteralConverter("%PARSER_ERROR[" + cn.getValue() + "]"));
                            break;
                        } else {
                            compositeConverter.setFormattingInfo(cn.getFormatInfo());
                            compositeConverter.setOptionList(cn.getOptions());
                            Compiler<E> childCompiler = new Compiler<>(cn.getChildNode(), this.converterMap);
                            childCompiler.setContext(this.context);
                            Converter<E> childConverter = childCompiler.compile();
                            compositeConverter.setChildConverter(childConverter);
                            addToList(compositeConverter);
                            break;
                        }
                }
                node = n.next;
            } else {
                return this.head;
            }
        }
    }

    private void addToList(Converter<E> c) {
        if (this.head == null) {
            this.tail = c;
            this.head = c;
        } else {
            this.tail.setNext(c);
            this.tail = c;
        }
    }

    DynamicConverter<E> createConverter(SimpleKeywordNode kn) {
        String keyword = (String) kn.getValue();
        String converterClassStr = (String) this.converterMap.get(keyword);
        if (converterClassStr != null) {
            try {
                return (DynamicConverter) OptionHelper.instantiateByClassName(converterClassStr, (Class<?>) DynamicConverter.class, this.context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
                return null;
            }
        }
        addError("There is no conversion class registered for conversion word [" + keyword + "]");
        return null;
    }

    CompositeConverter<E> createCompositeConverter(CompositeNode cn) {
        String keyword = (String) cn.getValue();
        String converterClassStr = (String) this.converterMap.get(keyword);
        if (converterClassStr != null) {
            try {
                return (CompositeConverter) OptionHelper.instantiateByClassName(converterClassStr, (Class<?>) CompositeConverter.class, this.context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
                return null;
            }
        }
        addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
        return null;
    }
}
