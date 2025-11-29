package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.request.render.Renderer;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/tiles3/TilesViewResolver.class */
public class TilesViewResolver extends UrlBasedViewResolver {

    @Nullable
    private Renderer renderer;

    @Nullable
    private Boolean alwaysInclude;

    public TilesViewResolver() {
        setViewClass(requiredViewClass());
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setAlwaysInclude(Boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return TilesView.class;
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected AbstractUrlBasedView instantiateView() {
        return getViewClass() == TilesView.class ? new TilesView() : super.instantiateView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public TilesView buildView(String viewName) throws Exception {
        TilesView view = (TilesView) super.buildView(viewName);
        if (this.renderer != null) {
            view.setRenderer(this.renderer);
        }
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude.booleanValue());
        }
        return view;
    }
}
