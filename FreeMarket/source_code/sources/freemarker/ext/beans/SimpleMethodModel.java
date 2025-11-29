package freemarker.ext.beans;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/SimpleMethodModel.class */
public final class SimpleMethodModel extends SimpleMethod implements TemplateMethodModelEx, TemplateSequenceModel, _UnexpectedTypeErrorExplainerTemplateModel {
    private final Object object;
    private final BeansWrapper wrapper;

    SimpleMethodModel(Object object, Method method, Class[] argTypes, BeansWrapper wrapper) {
        super(method, argTypes);
        this.object = object;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        try {
            return this.wrapper.invokeMethod(this.object, (Method) getMember(), unwrapArguments(arguments, this.wrapper));
        } catch (TemplateModelException e) {
            throw e;
        } catch (Exception e2) {
            throw _MethodUtil.newInvocationTemplateModelException(this.object, getMember(), e2);
        }
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        return (TemplateModel) exec(Collections.singletonList(new SimpleNumber(Integer.valueOf(index))));
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        throw new _TemplateModelException(new _ErrorDescriptionBuilder("Getting the number of items or listing the items is not supported on this ", new _DelayedFTLTypeDescription(this), " value, because this value wraps the following Java method, not a real listable value: ", new _DelayedToString(getMember())).tips("Maybe you should to call this method first and then do something with its return value.", "obj.someMethod(i) and obj.someMethod[i] does the same for this method, hence it's a \"+sequence\"."));
    }

    public String toString() {
        return getMember().toString();
    }

    @Override // freemarker.core._UnexpectedTypeErrorExplainerTemplateModel
    public Object[] explainTypeError(Class[] expectedClasses) {
        Method m;
        Class returnType;
        Member member = getMember();
        if (!(member instanceof Method) || (returnType = (m = (Method) member).getReturnType()) == null || returnType == Void.TYPE || returnType == Void.class) {
            return null;
        }
        String mName = m.getName();
        if (mName.startsWith(BeanUtil.PREFIX_GETTER_GET) && mName.length() > 3 && Character.isUpperCase(mName.charAt(3)) && m.getParameterTypes().length == 0) {
            return new Object[]{"Maybe using obj.something instead of obj.getSomething will yield the desired value."};
        }
        if (mName.startsWith(BeanUtil.PREFIX_GETTER_IS) && mName.length() > 2 && Character.isUpperCase(mName.charAt(2)) && m.getParameterTypes().length == 0) {
            return new Object[]{"Maybe using obj.something instead of obj.isSomething will yield the desired value."};
        }
        Object[] objArr = new Object[3];
        objArr[0] = "Maybe using obj.something(";
        objArr[1] = m.getParameterTypes().length != 0 ? "params" : "";
        objArr[2] = ") instead of obj.something will yield the desired value";
        return objArr;
    }
}
