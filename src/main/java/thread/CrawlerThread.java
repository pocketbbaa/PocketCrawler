package thread;

import annotation.PageFieldSelect;
import annotation.PageSelect;
import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.util.StringUtils;
import crawler.PocketCrawler;
import enums.SelectType;
import model.PageLoadConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.CommonUtil;
import utils.ElementUtil;
import utils.JsoupUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 爬虫线程
 * Created by wangyang on 2018/1/18.
 */
public class CrawlerThread implements Runnable {

    private PocketCrawler pocketCrawler;

    public CrawlerThread(PocketCrawler pocketCrawler) {
        this.pocketCrawler = pocketCrawler;
    }

    @Override
    public void run() {

        while (true) {
            String link = pocketCrawler.getUrl();
            if (!CommonUtil.isUrl(link)) {
                continue;
            }
            //解析页面
            boolean ret = false;
            try {
                ret = process(link);
                //TODO设置停顿时间
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            if (ret) {
//                break;
            }
        }

    }

    private boolean process(String link) throws IllegalAccessException, InstantiationException {

        PageLoadConfig pageLoadConfig = getPageLoadConfig(link);
        Document html = JsoupUtil.loadPage(pageLoadConfig);
        if (html == null) {
            return false;
        }

        //对爬取地址做正则校验，若不满足直接退出
        if (!pocketCrawler.validWhiteUrl(link)) {
            return false;
        }

        boolean isAllowSpread = pocketCrawler.isAllowSpread();
        if (isAllowSpread) {
            //获取页面所有超链接
            Set<String> links = ElementUtil.getLinks(html);
            if (links != null && links.size() > 0) {
                for (String url : links) {
                    //进行url正则过滤,若用过放到带采集池
                    if (pocketCrawler.validWhiteUrl(url)) {
                        pocketCrawler.getUnVisitedUrlQueue().add(url);
                    }
                }
            }
        }

        /**
         * getGenericSuperclass()方法可以获取当前对象的直接超类的 Type
         * getActualTypeArguments()返回表示此类型实际类型参数的 Type 对象的数组
         */
        Type[] pageVoClassTypes = ((ParameterizedType) pocketCrawler.getPageParser().getClass().getGenericSuperclass()).getActualTypeArguments();
        //获取需要被转换对象class
        Class pageVoClassType = (Class) pageVoClassTypes[0];
        //获取类上的css选择器
        PageSelect pageVoSelect = (PageSelect) pageVoClassType.getAnnotation(PageSelect.class);
        String pageVoCssQuery = (pageVoSelect != null && pageVoSelect.cssQuery() != null && pageVoSelect.cssQuery().trim().length() > 0) ? pageVoSelect.cssQuery() : "html";
        Elements pageVoElements = html.select(pageVoCssQuery);
        if (pageVoElements != null && pageVoElements.hasText()) {
            for (Element element : pageVoElements) {
                //获取对象属性
                Object pageVo = pageVoClassType.newInstance();
                Field[] fields = pageVoClassType.getDeclaredFields();
                if (fields != null) {
                    for (Field field : fields) {
                        //过滤静态属性
                        if (Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }

                        //获取属性上的注解
                        PageFieldSelect fieldSelect = field.getAnnotation(PageFieldSelect.class);
                        String cssQuery = null;
                        SelectType selectType = null;
                        String selectVal = null;
                        if (fieldSelect != null) {
                            cssQuery = fieldSelect.cssQuery();
                            selectType = fieldSelect.selectType();
                            selectVal = fieldSelect.selectVal();
                        }

                        //如果没有css选择器设置则不做处理
                        if (cssQuery == null || cssQuery.trim().length() == 0) {
                            continue;
                        }

                        //该属性用于存放解析后的参数值
                        Object fieldValue = null;
                        /**
                         * field.getGenericType()返回属性声的Type类型
                         * fieldGenericType.getRawType()返回 Type 对象，表示声明此类型的类或接口
                         * ParameterizedType可以获取泛型参数Class类型,这里主要判断如果属性是一个对象
                         */
                        if (field.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
                            System.out.println(JSONObject.toJSONString(fieldGenericType));
                            //如果属性是集合
                            if (fieldGenericType.getRawType().equals(List.class)) {
                                //css选择器获取多个数据
                                Elements fieldElementList = element.select(cssQuery);
                                if (fieldElementList != null && fieldElementList.size() > 0) {
                                    //用于存放解析出来的参数的值
                                    List<Object> fieldValueTmp = new ArrayList<>();
                                    for (Element fieldElement : fieldElementList) {
                                        //获取单个的值
                                        String fieldElementOrigin = JsoupUtil.parseElement(fieldElement, selectType, selectVal);
                                        if (fieldElementOrigin == null || fieldElementOrigin.length() == 0) {
                                            System.out.println("没有获取到属性的值");
                                            continue;
                                        }
                                        try {
                                            fieldValueTmp.add(CommonUtil.parseValue(field, fieldElementOrigin));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (fieldValueTmp.size() > 0) {
                                        fieldValue = fieldValueTmp;
                                    }
                                }
                            }
                        } else {
                            //如果属性不是对象，直接获取值将页面获取到的值赋值给fieldValue
                            Elements fieldElements = element.select(cssQuery);
                            String fieldValueOrigin = null;
                            if (fieldElements != null && fieldElements.size() > 0) {
                                fieldValueOrigin = JsoupUtil.parseElement(fieldElements.get(0), selectType, selectVal);
                            }
                            if (fieldValueOrigin == null || fieldValueOrigin.length() == 0) {
                                continue;
                            }
                            try {
                                fieldValue = CommonUtil.parseValue(field, fieldValueOrigin);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //给属性赋值
                        if (fieldValue != null) {
                            field.setAccessible(true);
                            field.set(pageVo, fieldValue);
                        }
                    }
                }
                //将赋值后的pageVo输出
                pocketCrawler.getPageParser().parse(html, element, pageVo);
            }
        }
        return true;
    }

    public PageLoadConfig getPageLoadConfig(String link) {

        PageLoadConfig pageLoadConfig = new PageLoadConfig();
        String userAgent = pocketCrawler.getUserAgentList().size() > 1
                ? pocketCrawler.getUserAgentList().get(new Random().nextInt(pocketCrawler.getUserAgentList().size()))
                : pocketCrawler.getUserAgentList().size() == 1 ? pocketCrawler.getUserAgentList().get(0) : null;

        pageLoadConfig.setUrl(link);
        if (userAgent != null) {
            pageLoadConfig.setUserAgent(userAgent);
        }
        pageLoadConfig.setRequestType(pocketCrawler.getRequestType());
        pageLoadConfig.setTimeoutMillis(pocketCrawler.getTimeoutMillis());
        pageLoadConfig.setParamMap(pocketCrawler.getParamMap());
        pageLoadConfig.setHeaderMap(pocketCrawler.getHeaderMap());
        pageLoadConfig.setCookieMap(pocketCrawler.getCookieMap());
        return pageLoadConfig;
    }
}
