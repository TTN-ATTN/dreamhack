package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/node/ObjectNode.class */
public class ObjectNode extends ContainerNode<ObjectNode> implements Serializable {
    private static final long serialVersionUID = 1;
    protected final Map<String, JsonNode> _children;

    public ObjectNode(JsonNodeFactory nc) {
        super(nc);
        this._children = new LinkedHashMap();
    }

    public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids) {
        super(nc);
        this._children = kids;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingProperty());
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode deepCopy() {
        ObjectNode ret = new ObjectNode(this._nodeFactory);
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return ret;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable.Base
    public boolean isEmpty(SerializerProvider serializers) {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final boolean isObject() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.START_OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public int size() {
        return this._children.size();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<JsonNode> elements() {
        return this._children.values().iterator();
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(int index) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(String propertyName) {
        return this._children.get(propertyName);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public Iterator<String> fieldNames() {
        return this._children.keySet().iterator();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            return n;
        }
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonNode
    public JsonNode required(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            return n;
        }
        return (JsonNode) _reportRequiredViolation("No value for property '%s' of `ObjectNode`", propertyName);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return this._children.entrySet().iterator();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode with(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            if (n instanceof ObjectNode) {
                return (ObjectNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n.getClass().getName() + ")");
        }
        ObjectNode result = objectNode();
        this._children.put(propertyName, result);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode withArray(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            if (n instanceof ArrayNode) {
                return (ArrayNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ArrayNode (but " + n.getClass().getName() + ")");
        }
        ArrayNode result = arrayNode();
        this._children.put(propertyName, result);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            return false;
        }
        ObjectNode other = (ObjectNode) o;
        Map<String, JsonNode> m1 = this._children;
        Map<String, JsonNode> m2 = other._children;
        int len = m1.size();
        if (m2.size() != len) {
            return false;
        }
        for (Map.Entry<String, JsonNode> entry : m1.entrySet()) {
            JsonNode v2 = m2.get(entry.getKey());
            if (v2 == null || !entry.getValue().equals(comparator, v2)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode findValue(String propertyName) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (propertyName.equals(entry.getKey())) {
                return entry.getValue();
            }
            JsonNode value = entry.getValue().findValue(propertyName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findValues(String propertyName, List<JsonNode> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (propertyName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList();
                }
                foundSoFar.add(entry.getValue());
            } else {
                foundSoFar = entry.getValue().findValues(propertyName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<String> findValuesAsText(String propertyName, List<String> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (propertyName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList();
                }
                foundSoFar.add(entry.getValue().asText());
            } else {
                foundSoFar = entry.getValue().findValuesAsText(propertyName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode findParent(String propertyName) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (propertyName.equals(entry.getKey())) {
                return this;
            }
            JsonNode value = entry.getValue().findParent(propertyName);
            if (value != null) {
                return (ObjectNode) value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findParents(String propertyName, List<JsonNode> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (propertyName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList();
                }
                foundSoFar.add(this);
            } else {
                foundSoFar = entry.getValue().findParents(propertyName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
        boolean trimEmptyArray = (provider == null || provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) ? false : true;
        g.writeStartObject(this);
        for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();
            if (!trimEmptyArray || !value.isArray() || !value.isEmpty(provider)) {
                g.writeFieldName(en.getKey());
                value.serialize(g, provider);
            }
        }
        g.writeEndObject();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        boolean trimEmptyArray = (provider == null || provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) ? false : true;
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_OBJECT));
        for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();
            if (!trimEmptyArray || !value.isArray() || !value.isEmpty(provider)) {
                g.writeFieldName(en.getKey());
                value.serialize(g, provider);
            }
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    public <T extends JsonNode> T set(String propertyName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        this._children.put(propertyName, value);
        return this;
    }

    public <T extends JsonNode> T setAll(Map<String, ? extends JsonNode> properties) {
        for (Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = en.getValue();
            if (n == null) {
                n = nullNode();
            }
            this._children.put(en.getKey(), n);
        }
        return this;
    }

    public <T extends JsonNode> T setAll(ObjectNode other) {
        this._children.putAll(other._children);
        return this;
    }

    public JsonNode replace(String propertyName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        return this._children.put(propertyName, value);
    }

    public <T extends JsonNode> T without(String propertyName) {
        this._children.remove(propertyName);
        return this;
    }

    public <T extends JsonNode> T without(Collection<String> propertyNames) {
        this._children.keySet().removeAll(propertyNames);
        return this;
    }

    @Deprecated
    public JsonNode put(String propertyName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        return this._children.put(propertyName, value);
    }

    public JsonNode putIfAbsent(String propertyName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        return this._children.putIfAbsent(propertyName, value);
    }

    public JsonNode remove(String propertyName) {
        return this._children.remove(propertyName);
    }

    public ObjectNode remove(Collection<String> propertyNames) {
        this._children.keySet().removeAll(propertyNames);
        return this;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode
    public ObjectNode removeAll() {
        this._children.clear();
        return this;
    }

    @Deprecated
    public JsonNode putAll(Map<String, ? extends JsonNode> properties) {
        return setAll(properties);
    }

    @Deprecated
    public JsonNode putAll(ObjectNode other) {
        return setAll(other);
    }

    public ObjectNode retain(Collection<String> propertyNames) {
        this._children.keySet().retainAll(propertyNames);
        return this;
    }

    public ObjectNode retain(String... propertyNames) {
        return retain(Arrays.asList(propertyNames));
    }

    public ArrayNode putArray(String propertyName) {
        ArrayNode n = arrayNode();
        _put(propertyName, n);
        return n;
    }

    public ObjectNode putObject(String propertyName) {
        ObjectNode n = objectNode();
        _put(propertyName, n);
        return n;
    }

    public ObjectNode putPOJO(String propertyName, Object pojo) {
        return _put(propertyName, pojoNode(pojo));
    }

    public ObjectNode putRawValue(String propertyName, RawValue raw) {
        return _put(propertyName, rawValueNode(raw));
    }

    public ObjectNode putNull(String propertyName) {
        this._children.put(propertyName, nullNode());
        return this;
    }

    public ObjectNode put(String propertyName, short v) {
        return _put(propertyName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Short v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.shortValue()));
    }

    public ObjectNode put(String fieldName, int v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Integer v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.intValue()));
    }

    public ObjectNode put(String fieldName, long v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Long v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.longValue()));
    }

    public ObjectNode put(String fieldName, float v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Float v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.floatValue()));
    }

    public ObjectNode put(String fieldName, double v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Double v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.doubleValue()));
    }

    public ObjectNode put(String fieldName, BigDecimal v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v));
    }

    public ObjectNode put(String fieldName, BigInteger v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v));
    }

    public ObjectNode put(String fieldName, String v) {
        return _put(fieldName, v == null ? nullNode() : textNode(v));
    }

    public ObjectNode put(String fieldName, boolean v) {
        return _put(fieldName, booleanNode(v));
    }

    public ObjectNode put(String fieldName, Boolean v) {
        return _put(fieldName, v == null ? nullNode() : booleanNode(v.booleanValue()));
    }

    public ObjectNode put(String fieldName, byte[] v) {
        return _put(fieldName, v == null ? nullNode() : binaryNode(v));
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof ObjectNode)) {
            return _childrenEqual((ObjectNode) o);
        }
        return false;
    }

    protected boolean _childrenEqual(ObjectNode other) {
        return this._children.equals(other._children);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        return this._children.hashCode();
    }

    protected ObjectNode _put(String fieldName, JsonNode value) {
        this._children.put(fieldName, value);
        return this;
    }
}
