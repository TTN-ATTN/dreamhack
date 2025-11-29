package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForHashes.class */
class BuiltInsForHashes {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForHashes$keysBI.class */
    static class keysBI extends BuiltInForHashEx {
        keysBI() {
        }

        @Override // freemarker.core.BuiltInForHashEx
        TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
            TemplateCollectionModel keys = hashExModel.keys();
            if (keys == null) {
                throw newNullPropertyException("keys", hashExModel, env);
            }
            return keys instanceof TemplateSequenceModel ? keys : new CollectionAndSequence(keys);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForHashes$valuesBI.class */
    static class valuesBI extends BuiltInForHashEx {
        valuesBI() {
        }

        @Override // freemarker.core.BuiltInForHashEx
        TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
            TemplateCollectionModel values = hashExModel.values();
            if (values == null) {
                throw newNullPropertyException("values", hashExModel, env);
            }
            return values instanceof TemplateSequenceModel ? values : new CollectionAndSequence(values);
        }
    }

    private BuiltInsForHashes() {
    }
}
