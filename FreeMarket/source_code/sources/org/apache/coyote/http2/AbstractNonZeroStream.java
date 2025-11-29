package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/AbstractNonZeroStream.class */
abstract class AbstractNonZeroStream extends AbstractStream {
    private static final Log log = LogFactory.getLog((Class<?>) AbstractNonZeroStream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) AbstractNonZeroStream.class);
    protected static final ByteBuffer ZERO_LENGTH_BYTEBUFFER = ByteBuffer.allocate(0);
    protected final StreamStateMachine state;
    private volatile int weight;

    abstract ByteBuffer getInputByteBuffer();

    abstract void receivedData(int i) throws Http2Exception;

    AbstractNonZeroStream(String connectionId, Integer identifier) {
        super(identifier);
        this.weight = 16;
        this.state = new StreamStateMachine(connectionId, getIdAsString());
    }

    AbstractNonZeroStream(Integer identifier, StreamStateMachine state) {
        super(identifier);
        this.weight = 16;
        this.state = state;
    }

    @Override // org.apache.coyote.http2.AbstractStream
    final int getWeight() {
        return this.weight;
    }

    final void rePrioritise(AbstractStream parent, boolean exclusive, int weight) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reprioritisation.debug", getConnectionId(), getIdAsString(), Boolean.toString(exclusive), parent.getIdAsString(), Integer.toString(weight)));
        }
        if (isDescendant(parent)) {
            parent.detachFromParent();
            getParentStream().addChild((AbstractNonZeroStream) parent);
        }
        if (exclusive) {
            Iterator<AbstractNonZeroStream> parentsChildren = parent.getChildStreams().iterator();
            while (parentsChildren.hasNext()) {
                AbstractNonZeroStream parentsChild = parentsChildren.next();
                parentsChildren.remove();
                addChild(parentsChild);
            }
        }
        detachFromParent();
        parent.addChild(this);
        this.weight = weight;
    }

    final void rePrioritise(AbstractStream parent, int weight) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reprioritisation.debug", getConnectionId(), getIdAsString(), Boolean.FALSE, parent.getIdAsString(), Integer.toString(weight)));
        }
        parent.addChild(this);
        this.weight = weight;
    }

    void replaceStream(AbstractNonZeroStream replacement) {
        getParentStream().addChild(replacement);
        detachFromParent();
        for (AbstractNonZeroStream child : getChildStreams()) {
            replacement.addChild(child);
        }
        getChildStreams().clear();
        replacement.weight = this.weight;
    }

    final boolean isClosedFinal() {
        return this.state.isClosedFinal();
    }

    final void checkState(FrameType frameType) throws Http2Exception {
        this.state.checkFrameType(frameType);
    }
}
