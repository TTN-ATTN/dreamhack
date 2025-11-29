package org.springframework.boot.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.boot.origin.TextResourceOrigin;
import org.springframework.core.io.Resource;
import org.springframework.util.ReflectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedYamlLoader.class */
class OriginTrackedYamlLoader extends YamlProcessor {
    private static final boolean HAS_RESOLVER_LIMIT;
    private final Resource resource;

    static {
        HAS_RESOLVER_LIMIT = ReflectionUtils.findMethod(Resolver.class, "addImplicitResolver", Tag.class, Pattern.class, String.class, Integer.TYPE) != null;
    }

    OriginTrackedYamlLoader(Resource resource) {
        this.resource = resource;
        setResources(resource);
    }

    @Override // org.springframework.beans.factory.config.YamlProcessor
    protected Yaml createYaml() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setAllowRecursiveKeys(true);
        return createYaml(loaderOptions);
    }

    private Yaml createYaml(LoaderOptions loaderOptions) {
        BaseConstructor constructor = new OriginTrackingConstructor(loaderOptions);
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        Resolver resolver = HAS_RESOLVER_LIMIT ? new NoTimestampResolverWithLimit() : new NoTimestampResolver();
        return new Yaml(constructor, representer, dumperOptions, loaderOptions, resolver);
    }

    List<Map<String, Object>> load() {
        List<Map<String, Object>> result = new ArrayList<>();
        process((properties, map) -> {
            result.add(getFlattenedMap(map));
        });
        return result;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedYamlLoader$OriginTrackingConstructor.class */
    private class OriginTrackingConstructor extends SafeConstructor {
        OriginTrackingConstructor(LoaderOptions loadingConfig) {
            super(loadingConfig);
        }

        @Override // org.yaml.snakeyaml.constructor.BaseConstructor
        public Object getData() throws NoSuchElementException {
            Object data = super.getData();
            if ((data instanceof CharSequence) && ((CharSequence) data).length() == 0) {
                return null;
            }
            return data;
        }

        @Override // org.yaml.snakeyaml.constructor.BaseConstructor
        protected Object constructObject(Node node) {
            if ((node instanceof CollectionNode) && ((CollectionNode) node).getValue().isEmpty()) {
                return constructTrackedObject(node, super.constructObject(node));
            }
            if ((node instanceof ScalarNode) && !(node instanceof KeyScalarNode)) {
                return constructTrackedObject(node, super.constructObject(node));
            }
            if (node instanceof MappingNode) {
                replaceMappingNodeKeys((MappingNode) node);
            }
            return super.constructObject(node);
        }

        private void replaceMappingNodeKeys(MappingNode node) {
            node.setValue((List) node.getValue().stream().map(KeyScalarNode::get).collect(Collectors.toList()));
        }

        private Object constructTrackedObject(Node node, Object value) {
            Origin origin = getOrigin(node);
            return OriginTrackedValue.of(getValue(value), origin);
        }

        private Object getValue(Object value) {
            return value != null ? value : "";
        }

        private Origin getOrigin(Node node) {
            Mark mark = node.getStartMark();
            TextResourceOrigin.Location location = new TextResourceOrigin.Location(mark.getLine(), mark.getColumn());
            return new TextResourceOrigin(OriginTrackedYamlLoader.this.resource, location);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedYamlLoader$KeyScalarNode.class */
    private static class KeyScalarNode extends ScalarNode {
        KeyScalarNode(ScalarNode node) {
            super(node.getTag(), node.getValue(), node.getStartMark(), node.getEndMark(), node.getScalarStyle());
        }

        static NodeTuple get(NodeTuple nodeTuple) {
            Node keyNode = nodeTuple.getKeyNode();
            Node valueNode = nodeTuple.getValueNode();
            return new NodeTuple(get(keyNode), valueNode);
        }

        private static Node get(Node node) {
            if (node instanceof ScalarNode) {
                return new KeyScalarNode((ScalarNode) node);
            }
            return node;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedYamlLoader$NoTimestampResolver.class */
    private static class NoTimestampResolver extends Resolver {
        private NoTimestampResolver() {
        }

        @Override // org.yaml.snakeyaml.resolver.Resolver
        public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
            if (tag == Tag.TIMESTAMP) {
                return;
            }
            super.addImplicitResolver(tag, regexp, first);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedYamlLoader$NoTimestampResolverWithLimit.class */
    private static class NoTimestampResolverWithLimit extends Resolver {
        private NoTimestampResolverWithLimit() {
        }

        public void addImplicitResolver(Tag tag, Pattern regexp, String first, int limit) {
            if (tag == Tag.TIMESTAMP) {
                return;
            }
            super.addImplicitResolver(tag, regexp, first, limit);
        }
    }
}
