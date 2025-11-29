package org.apache.coyote.http11.upgrade;

import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.modeler.BaseModelMBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http11/upgrade/UpgradeGroupInfo.class */
public class UpgradeGroupInfo extends BaseModelMBean {
    private final List<UpgradeInfo> upgradeInfos = new ArrayList();
    private long deadBytesReceived = 0;
    private long deadBytesSent = 0;
    private long deadMsgsReceived = 0;
    private long deadMsgsSent = 0;

    public synchronized void addUpgradeInfo(UpgradeInfo ui) {
        this.upgradeInfos.add(ui);
    }

    public synchronized void removeUpgradeInfo(UpgradeInfo ui) {
        if (ui != null) {
            this.deadBytesReceived += ui.getBytesReceived();
            this.deadBytesSent += ui.getBytesSent();
            this.deadMsgsReceived += ui.getMsgsReceived();
            this.deadMsgsSent += ui.getMsgsSent();
            this.upgradeInfos.remove(ui);
        }
    }

    public synchronized long getBytesReceived() {
        long bytes = this.deadBytesReceived;
        for (UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesReceived();
        }
        return bytes;
    }

    public synchronized void setBytesReceived(long bytesReceived) {
        this.deadBytesReceived = bytesReceived;
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesReceived(bytesReceived);
        }
    }

    public synchronized long getBytesSent() {
        long bytes = this.deadBytesSent;
        for (UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesSent();
        }
        return bytes;
    }

    public synchronized void setBytesSent(long bytesSent) {
        this.deadBytesSent = bytesSent;
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesSent(bytesSent);
        }
    }

    public synchronized long getMsgsReceived() {
        long msgs = this.deadMsgsReceived;
        for (UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsReceived();
        }
        return msgs;
    }

    public synchronized void setMsgsReceived(long msgsReceived) {
        this.deadMsgsReceived = msgsReceived;
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsReceived(msgsReceived);
        }
    }

    public synchronized long getMsgsSent() {
        long msgs = this.deadMsgsSent;
        for (UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsSent();
        }
        return msgs;
    }

    public synchronized void setMsgsSent(long msgsSent) {
        this.deadMsgsSent = msgsSent;
        for (UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsSent(msgsSent);
        }
    }

    public void resetCounters() {
        setBytesReceived(0L);
        setBytesSent(0L);
        setMsgsReceived(0L);
        setMsgsSent(0L);
    }
}
