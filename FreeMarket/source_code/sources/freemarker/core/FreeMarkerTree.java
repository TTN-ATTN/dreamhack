package freemarker.core;

import freemarker.template.Template;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FreeMarkerTree.class */
public class FreeMarkerTree extends JTree {
    public FreeMarkerTree(Template template) {
        super(template.getRootTreeNode());
    }

    public void setTemplate(Template template) {
        setModel(new DefaultTreeModel(template.getRootTreeNode()));
        invalidate();
    }

    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof TemplateElement) {
            return ((TemplateElement) value).getDescription();
        }
        return value.toString();
    }
}
