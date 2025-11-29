package freemarker.template;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/ResourceBundleLocalizedString.class */
public class ResourceBundleLocalizedString extends LocalizedString {
    private String resourceKey;
    private String resourceBundleLookupKey;

    public ResourceBundleLocalizedString(String resourceBundleLookupKey, String resourceKey) {
        this.resourceBundleLookupKey = resourceBundleLookupKey;
        this.resourceKey = resourceKey;
    }

    @Override // freemarker.template.LocalizedString
    public String getLocalizedString(Locale locale) throws TemplateModelException {
        try {
            ResourceBundle rb = ResourceBundle.getBundle(this.resourceBundleLookupKey, locale);
            return rb.getString(this.resourceKey);
        } catch (MissingResourceException mre) {
            throw new TemplateModelException("missing resource", (Exception) mre);
        }
    }
}
