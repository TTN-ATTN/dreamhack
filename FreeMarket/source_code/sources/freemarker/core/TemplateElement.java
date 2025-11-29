package freemarker.core;

import freemarker.core.TemplateProcessingTracer;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateSequenceModel;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateElement.class */
public abstract class TemplateElement extends TemplateObject implements TreeNode, TemplateProcessingTracer.TracedElement {
    private static final int INITIAL_REGULATED_CHILD_BUFFER_CAPACITY = 6;
    private TemplateElement parent;
    private TemplateElement[] childBuffer;
    private int childCount;
    private int index;

    abstract TemplateElement[] accept(Environment environment) throws TemplateException, IOException;

    abstract boolean isNestedBlockRepeater();

    protected abstract String dump(boolean z);

    @Override // freemarker.core.TemplateProcessingTracer.TracedElement
    public final String getDescription() {
        return dump(false);
    }

    @Override // freemarker.core.TemplateObject
    public final String getCanonicalForm() {
        return dump(true);
    }

    final String getChildrenCanonicalForm() {
        return getChildrenCanonicalForm(this.childBuffer);
    }

    static String getChildrenCanonicalForm(TemplateElement[] children) {
        TemplateElement child;
        if (children == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int length = children.length;
        for (int i = 0; i < length && (child = children[i]) != null; i++) {
            sb.append(child.getCanonicalForm());
        }
        return sb.toString();
    }

    boolean isShownInStackTrace() {
        return false;
    }

    public TemplateNodeModel getParentNode() {
        return null;
    }

    public String getNodeNamespace() {
        return null;
    }

    public String getNodeType() {
        return "element";
    }

    public TemplateSequenceModel getChildNodes() {
        if (this.childBuffer != null) {
            SimpleSequence seq = new SimpleSequence(this.childCount);
            for (int i = 0; i < this.childCount; i++) {
                seq.add(this.childBuffer[i]);
            }
            return seq;
        }
        return new SimpleSequence(0);
    }

    public String getNodeName() {
        String classname = getClass().getName();
        int shortNameOffset = classname.lastIndexOf(46) + 1;
        return classname.substring(shortNameOffset);
    }

    @Override // freemarker.core.TemplateProcessingTracer.TracedElement
    public boolean isLeaf() {
        return this.childCount == 0;
    }

    @Deprecated
    public boolean getAllowsChildren() {
        return !isLeaf();
    }

    @Deprecated
    public int getIndex(TreeNode node) {
        for (int i = 0; i < this.childCount; i++) {
            if (this.childBuffer[i].equals(node)) {
                return i;
            }
        }
        return -1;
    }

    public int getChildCount() {
        return this.childCount;
    }

    public Enumeration children() {
        return this.childBuffer != null ? new _ArrayEnumeration(this.childBuffer, this.childCount) : Collections.enumeration(Collections.EMPTY_LIST);
    }

    @Deprecated
    public TreeNode getChildAt(int index) {
        if (this.childCount == 0) {
            throw new IndexOutOfBoundsException("Template element has no children");
        }
        try {
            return this.childBuffer[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
        }
    }

    public void setChildAt(int index, TemplateElement element) {
        if (index < this.childCount && index >= 0) {
            this.childBuffer[index] = element;
            element.index = index;
            element.parent = this;
            return;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
    }

    @Deprecated
    public TreeNode getParent() {
        return this.parent;
    }

    final TemplateElement getParentElement() {
        return this.parent;
    }

    final void setChildBufferCapacity(int capacity) {
        int ln = this.childCount;
        TemplateElement[] newChildBuffer = new TemplateElement[capacity];
        for (int i = 0; i < ln; i++) {
            newChildBuffer[i] = this.childBuffer[i];
        }
        this.childBuffer = newChildBuffer;
    }

    final void addChild(TemplateElement nestedElement) {
        addChild(this.childCount, nestedElement);
    }

    final void addChild(int index, TemplateElement nestedElement) {
        int childCount = this.childCount;
        TemplateElement[] childBuffer = this.childBuffer;
        if (childBuffer == null) {
            childBuffer = new TemplateElement[6];
            this.childBuffer = childBuffer;
        } else if (childCount == childBuffer.length) {
            setChildBufferCapacity(childCount != 0 ? childCount * 2 : 1);
            childBuffer = this.childBuffer;
        }
        for (int i = childCount; i > index; i--) {
            TemplateElement movedElement = childBuffer[i - 1];
            movedElement.index = i;
            childBuffer[i] = movedElement;
        }
        nestedElement.index = index;
        nestedElement.parent = this;
        childBuffer[index] = nestedElement;
        this.childCount = childCount + 1;
    }

    final TemplateElement getChild(int index) {
        return this.childBuffer[index];
    }

    final TemplateElement[] getChildBuffer() {
        return this.childBuffer;
    }

    final void setChildren(TemplateElements buffWithCnt) {
        TemplateElement[] childBuffer = buffWithCnt.getBuffer();
        int childCount = buffWithCnt.getCount();
        for (int i = 0; i < childCount; i++) {
            TemplateElement child = childBuffer[i];
            child.index = i;
            child.parent = this;
        }
        this.childBuffer = childBuffer;
        this.childCount = childCount;
    }

    final void copyFieldsFrom(TemplateElement that) {
        super.copyFieldsFrom((TemplateObject) that);
        this.parent = that.parent;
        this.index = that.index;
        this.childBuffer = that.childBuffer;
        this.childCount = that.childCount;
    }

    final int getIndex() {
        return this.index;
    }

    final void setFieldsForRootElement() {
        this.index = 0;
        this.parent = null;
    }

    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        int childCount = this.childCount;
        if (childCount != 0) {
            for (int i = 0; i < childCount; i++) {
                TemplateElement te = this.childBuffer[i];
                TemplateElement te2 = te.postParseCleanup(stripWhitespace);
                this.childBuffer[i] = te2;
                te2.parent = this;
                te2.index = i;
            }
            int i2 = 0;
            while (i2 < childCount) {
                TemplateElement te3 = this.childBuffer[i2];
                if (te3.isIgnorable(stripWhitespace)) {
                    childCount--;
                    for (int j = i2; j < childCount; j++) {
                        TemplateElement te22 = this.childBuffer[j + 1];
                        this.childBuffer[j] = te22;
                        te22.index = j;
                    }
                    this.childBuffer[childCount] = null;
                    this.childCount = childCount;
                    i2--;
                }
                i2++;
            }
            if (childCount == 0) {
                this.childBuffer = null;
            } else if (childCount < this.childBuffer.length && childCount <= (this.childBuffer.length * 3) / 4) {
                TemplateElement[] trimmedChildBuffer = new TemplateElement[childCount];
                for (int i3 = 0; i3 < childCount; i3++) {
                    trimmedChildBuffer[i3] = this.childBuffer[i3];
                }
                this.childBuffer = trimmedChildBuffer;
            }
        }
        return this;
    }

    boolean isIgnorable(boolean stripWhitespace) {
        return false;
    }

    TemplateElement prevTerminalNode() {
        TemplateElement prev = previousSibling();
        if (prev != null) {
            return prev.getLastLeaf();
        }
        if (this.parent != null) {
            return this.parent.prevTerminalNode();
        }
        return null;
    }

    TemplateElement nextTerminalNode() {
        TemplateElement next = nextSibling();
        if (next != null) {
            return next.getFirstLeaf();
        }
        if (this.parent != null) {
            return this.parent.nextTerminalNode();
        }
        return null;
    }

    TemplateElement previousSibling() {
        if (this.parent != null && this.index > 0) {
            return this.parent.childBuffer[this.index - 1];
        }
        return null;
    }

    TemplateElement nextSibling() {
        if (this.parent != null && this.index + 1 < this.parent.childCount) {
            return this.parent.childBuffer[this.index + 1];
        }
        return null;
    }

    private TemplateElement getFirstChild() {
        if (this.childCount == 0) {
            return null;
        }
        return this.childBuffer[0];
    }

    private TemplateElement getLastChild() {
        int childCount = this.childCount;
        if (childCount == 0) {
            return null;
        }
        return this.childBuffer[childCount - 1];
    }

    private TemplateElement getFirstLeaf() {
        TemplateElement te;
        TemplateElement firstChild = this;
        while (true) {
            te = firstChild;
            if (te.isLeaf() || (te instanceof Macro) || (te instanceof BlockAssignment)) {
                break;
            }
            firstChild = te.getFirstChild();
        }
        return te;
    }

    private TemplateElement getLastLeaf() {
        TemplateElement te;
        TemplateElement lastChild = this;
        while (true) {
            te = lastChild;
            if (te.isLeaf() || (te instanceof Macro) || (te instanceof BlockAssignment)) {
                break;
            }
            lastChild = te.getLastChild();
        }
        return te;
    }

    boolean isOutputCacheable() {
        return false;
    }

    boolean isChildrenOutputCacheable() {
        int ln = this.childCount;
        for (int i = 0; i < ln; i++) {
            if (!this.childBuffer[i].isOutputCacheable()) {
                return false;
            }
        }
        return true;
    }

    boolean heedsOpeningWhitespace() {
        return false;
    }

    boolean heedsTrailingWhitespace() {
        return false;
    }
}
