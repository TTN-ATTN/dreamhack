package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.WireFeedOutput;
import java.io.OutputStreamWriter;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/feed/AbstractFeedView.class */
public abstract class AbstractFeedView<T extends WireFeed> extends AbstractView {
    /* renamed from: newFeed */
    protected abstract T mo2172newFeed();

    protected abstract void buildFeedEntries(Map<String, Object> model, T feed, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WireFeed wireFeedMo2172newFeed = mo2172newFeed();
        buildFeedMetadata(model, wireFeedMo2172newFeed, request);
        buildFeedEntries(model, wireFeedMo2172newFeed, request, response);
        setResponseContentType(request, response);
        if (!StringUtils.hasText(wireFeedMo2172newFeed.getEncoding())) {
            wireFeedMo2172newFeed.setEncoding("UTF-8");
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        ServletOutputStream out = response.getOutputStream();
        feedOutput.output(wireFeedMo2172newFeed, new OutputStreamWriter(out, wireFeedMo2172newFeed.getEncoding()));
        out.flush();
    }

    protected void buildFeedMetadata(Map<String, Object> model, T feed, HttpServletRequest request) {
    }
}
