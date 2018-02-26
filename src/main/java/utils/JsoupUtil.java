package utils;

import enums.RequestType;
import enums.SelectType;
import model.PageLoadConfig;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Jsoup
 * Created by wangyang on 2018/1/11.
 */
public class JsoupUtil {


    /**
     * 从一个URL加载一个Document
     * 这个方法只支持Web URLs (http和https 协议);
     *
     * @param pageLoadConfig
     * @return
     */
    public static Document loadPage(PageLoadConfig pageLoadConfig) {
        String targetUrl = pageLoadConfig.getUrl();
        if (!CommonUtil.isUrl(targetUrl)) {
            return null;
        }
        try {
            Connection conn = Jsoup.connect(pageLoadConfig.getUrl());
            if (CommonUtil.mapIsNotEmpty(pageLoadConfig.getParamMap())) {
                conn.data(pageLoadConfig.getParamMap());
            }
            if (CommonUtil.mapIsNotEmpty(pageLoadConfig.getCookieMap())) {
                conn.cookies(pageLoadConfig.getCookieMap());
            }
            if (CommonUtil.mapIsNotEmpty(pageLoadConfig.getHeaderMap())) {
                conn.headers(pageLoadConfig.getHeaderMap());
            }
            if (pageLoadConfig.getUserAgent() != null) {
                conn.userAgent(pageLoadConfig.getUserAgent());
            }
            if (pageLoadConfig.getReferrer() != null) {
                conn.referrer(pageLoadConfig.getReferrer());
            }
            if (pageLoadConfig.getTimeoutMillis() > 0) {
                conn.timeout(pageLoadConfig.getTimeoutMillis());
            }
            if (pageLoadConfig.getProxy() != null) {
                conn.proxy(pageLoadConfig.getProxy());
            }
            return pageLoadConfig.getRequestType() != null && pageLoadConfig.getRequestType().equals(RequestType.POST) ? conn.post() : conn.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 抽取元素数据
     *
     * @param fieldElement
     * @param selectType
     * @param selectVal
     * @return
     */
    public static String parseElement(Element fieldElement, SelectType selectType, String selectVal) {
        if (SelectType.HTML == selectType) {
            return fieldElement.html();
        }
        if (SelectType.VAL == selectType) {
            return fieldElement.val();
        }
        if (SelectType.TEXT == selectType) {
            return fieldElement.text();
        }
        return fieldElement.toString();
    }


    public static void main(String[] args) {
        PageLoadConfig pageLoadConfig = new PageLoadConfig();
        pageLoadConfig.setUrl("https://www.baidu.com/");
        pageLoadConfig.setRequestType(RequestType.POST);
        Document doc = loadPage(pageLoadConfig);
    }
}
