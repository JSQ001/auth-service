

package com.hand.hcf.app.core.util;

import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Deprecated
public final class PaginationUtil {

    public static void setPaginationHttpHeaders(Page<?> page, String baseUrl, HttpServletResponse response) throws URISyntaxException {
        HttpHeaders httpHeaders = generatePaginationHttpHeaders(page, baseUrl);
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String name = entry.getKey();
            for (String value : entry.getValue()) {
                response.addHeader(name, value);
            }
        }
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page<?> page, String baseURL) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", "" + page.getTotal());
        String urlConnector = "?";
        if (baseURL.contains("?")) {
            urlConnector = "&";
        }
        String link = "";
        if (page.getCurrent() < page.getPages()) {
            link = "<" + (new URI(baseURL + urlConnector + "page=" + (page.getCurrent() + 1) + "&size=" + page.getSize())).toString() + ">; rel=\"next\",";
        }
        if (page.getCurrent() > 1) {
            link += "<" + (new URI(baseURL + urlConnector + "page=" + (page.getCurrent() - 1) + "&size=" + page.getSize())).toString() + ">; rel=\"prev\",";
        }
        if (page.getCurrent() != 1 && page.getTotal() > 0) {
            link += "<" + (new URI(baseURL + urlConnector + "page=1&size=" + page.getSize())).toString() + ">; rel=\"first\",";
        }
        if (page.getCurrent() != page.getPages()) {
            link += "<" + (new URI(baseURL + urlConnector + "page=" + page.getPages() + "&size=" + page.getSize())).toString() + ">; rel=\"last\",";
        }
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }
}
