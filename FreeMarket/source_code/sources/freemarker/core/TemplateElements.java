package freemarker.core;

import freemarker.template.utility.CollectionUtils;

/* compiled from: TemplateElementArrayBuilder.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateElements.class */
class TemplateElements {
    static final TemplateElements EMPTY = new TemplateElements(null, 0);
    private final TemplateElement[] buffer;
    private final int count;

    TemplateElements(TemplateElement[] buffer, int count) {
        this.buffer = buffer;
        this.count = count;
    }

    TemplateElement[] getBuffer() {
        return this.buffer;
    }

    int getCount() {
        return this.count;
    }

    TemplateElement getFirst() {
        if (this.buffer != null) {
            return this.buffer[0];
        }
        return null;
    }

    TemplateElement getLast() {
        if (this.buffer != null) {
            return this.buffer[this.count - 1];
        }
        return null;
    }

    TemplateElement asSingleElement() {
        if (this.count == 0) {
            return new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false);
        }
        TemplateElement first = this.buffer[0];
        if (this.count == 1) {
            return first;
        }
        MixedContent mixedContent = new MixedContent();
        mixedContent.setChildren(this);
        mixedContent.setLocation(first.getTemplate(), first, getLast());
        return mixedContent;
    }

    MixedContent asMixedContent() {
        MixedContent mixedContent = new MixedContent();
        if (this.count != 0) {
            TemplateElement first = this.buffer[0];
            mixedContent.setChildren(this);
            mixedContent.setLocation(first.getTemplate(), first, getLast());
        }
        return mixedContent;
    }
}
