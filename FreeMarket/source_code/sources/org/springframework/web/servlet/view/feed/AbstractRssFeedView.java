package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/feed/AbstractRssFeedView.class */
public abstract class AbstractRssFeedView extends AbstractFeedView<Channel> {
    protected abstract List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    protected /* bridge */ /* synthetic */ void buildFeedEntries(Map model, WireFeed channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        buildFeedEntries((Map<String, Object>) model, (Channel) channel, request, response);
    }

    public AbstractRssFeedView() {
        setContentType(MediaType.APPLICATION_RSS_XML_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    /* renamed from: newFeed, reason: merged with bridge method [inline-methods] */
    public Channel mo2172newFeed() {
        return new Channel("rss_2.0");
    }

    protected final void buildFeedEntries(Map<String, Object> model, Channel channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Item> items = buildFeedItems(model, request, response);
        channel.setItems(items);
    }
}
