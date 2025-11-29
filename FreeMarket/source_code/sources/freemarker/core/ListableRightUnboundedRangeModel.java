package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.math.BigInteger;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ListableRightUnboundedRangeModel.class */
final class ListableRightUnboundedRangeModel extends RightUnboundedRangeModel implements TemplateCollectionModel {
    ListableRightUnboundedRangeModel(int begin) {
        super(begin);
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return Integer.MAX_VALUE;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new TemplateModelIterator() { // from class: freemarker.core.ListableRightUnboundedRangeModel.1
            boolean needInc;
            int nextType = 1;
            int nextInt;
            long nextLong;
            BigInteger nextBigInteger;

            {
                this.nextInt = ListableRightUnboundedRangeModel.this.getBegining();
            }

            @Override // freemarker.template.TemplateModelIterator
            public TemplateModel next() throws TemplateModelException {
                if (this.needInc) {
                    switch (this.nextType) {
                        case 1:
                            if (this.nextInt < Integer.MAX_VALUE) {
                                this.nextInt++;
                                break;
                            } else {
                                this.nextType = 2;
                                this.nextLong = this.nextInt + 1;
                                break;
                            }
                        case 2:
                            if (this.nextLong < Long.MAX_VALUE) {
                                this.nextLong++;
                                break;
                            } else {
                                this.nextType = 3;
                                this.nextBigInteger = BigInteger.valueOf(this.nextLong);
                                this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
                                break;
                            }
                        default:
                            this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
                            break;
                    }
                }
                this.needInc = true;
                return this.nextType == 1 ? new SimpleNumber(this.nextInt) : this.nextType == 2 ? new SimpleNumber(this.nextLong) : new SimpleNumber(this.nextBigInteger);
            }

            @Override // freemarker.template.TemplateModelIterator
            public boolean hasNext() throws TemplateModelException {
                return true;
            }
        };
    }
}
