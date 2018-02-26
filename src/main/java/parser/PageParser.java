package parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 页面解析
 * Created by wangyang on 2018/1/18.
 */
public abstract class PageParser<T> {

    /**
     * 将页面数据解析为对象
     *
     * @param html
     * @param pageVoElement
     * @param pageVo
     */
    public abstract void parse(Document html, Element pageVoElement, T pageVo);
}
