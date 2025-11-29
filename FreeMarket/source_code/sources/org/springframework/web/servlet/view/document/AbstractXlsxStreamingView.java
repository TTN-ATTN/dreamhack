package org.springframework.web.servlet.view.document;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/document/AbstractXlsxStreamingView.class */
public abstract class AbstractXlsxStreamingView extends AbstractXlsxView {
    @Override // org.springframework.web.servlet.view.document.AbstractXlsxView, org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook, reason: collision with other method in class */
    protected /* bridge */ /* synthetic */ Workbook mo2171createWorkbook(Map model, HttpServletRequest request) {
        return createWorkbook((Map<String, Object>) model, request);
    }

    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new SXSSFWorkbook();
    }

    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        super.renderWorkbook(workbook, response);
        ((SXSSFWorkbook) workbook).dispose();
    }
}
