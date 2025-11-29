package freemarker.template;

import freemarker.template.TemplateHashModelEx2;
import freemarker.template.utility.Constants;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/GeneralPurposeNothing.class */
final class GeneralPurposeNothing implements TemplateBooleanModel, TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2, TemplateMethodModelEx {
    private static final TemplateModel instance = new GeneralPurposeNothing();

    private GeneralPurposeNothing() {
    }

    static TemplateModel getInstance() {
        return instance;
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return "";
    }

    @Override // freemarker.template.TemplateBooleanModel
    public boolean getAsBoolean() {
        return false;
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return true;
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() {
        return 0;
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int i) throws TemplateModelException {
        throw new TemplateModelException("Can't get item from an empty sequence.");
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) {
        return null;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List args) {
        return null;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() {
        return Constants.EMPTY_COLLECTION;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() {
        return Constants.EMPTY_COLLECTION;
    }

    @Override // freemarker.template.TemplateHashModelEx2
    public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
        return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
    }
}
