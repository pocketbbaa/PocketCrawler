package utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Page Element
 * Created by wangyang on 2018/1/17.
 */
public class ElementUtil {

    /**
     * 获取页面所有超链接地址
     *
     * @param doc
     * @return
     */
    public static Set<String> getLinks(Document doc) {

        if (doc == null) {
            return null;
        }
        Elements hrefElements = doc.select("a[href]");
        Set<String> links = new HashSet<String>();
        if (hrefElements != null && hrefElements.size() > 0) {
            for (Element item : hrefElements) {
                String href = item.attr("abs:href");
                if (CommonUtil.isUrl(href)) {
                    links.add(href);
                }
            }
        }
        return links;
    }




}
