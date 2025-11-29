package freemarker.ext.dom;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/AtAtKey.class */
enum AtAtKey {
    MARKUP("@@markup"),
    NESTED_MARKUP("@@nested_markup"),
    ATTRIBUTES_MARKUP("@@attributes_markup"),
    TEXT("@@text"),
    START_TAG("@@start_tag"),
    END_TAG("@@end_tag"),
    QNAME("@@qname"),
    NAMESPACE("@@namespace"),
    LOCAL_NAME("@@local_name"),
    ATTRIBUTES("@@"),
    PREVIOUS_SIBLING_ELEMENT("@@previous_sibling_element"),
    NEXT_SIBLING_ELEMENT("@@next_sibling_element");

    private final String key;

    public String getKey() {
        return this.key;
    }

    AtAtKey(String key) {
        this.key = key;
    }

    public static boolean containsKey(String key) {
        for (AtAtKey item : values()) {
            if (item.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }
}
