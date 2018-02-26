package crawler;

import conf.CrawlerConf;
import enums.RequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.PageParser;
import thread.CrawlerThread;
import utils.CommonUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 主类
 * Created by wangyang on 2018/1/17.
 */
public class PocketCrawler {

    private static Logger log = LoggerFactory.getLogger(PocketCrawler.class);

    // 待采集URL池(阻塞队列，先进先出)
    private volatile LinkedBlockingQueue<String> unVisitedUrlQueue = new LinkedBlockingQueue<>();

    // URL白名单正则，非空时进行URL白名单过滤页面
    private Set<String> whiteUrlRegexs = Collections.synchronizedSet(new HashSet<String>());

    //并发线程数
    private volatile int threadCount = 1;

    // 页面解析器
    private volatile PageParser pageParser;

    //请求方式
    private volatile RequestType requestType;

    //用户代理
    private volatile List<String> userAgentList = Collections.synchronizedList(new ArrayList<>(
            Arrays.asList(CrawlerConf.USER_AGENT_CHROME, CrawlerConf.USER_AGENT_FIREFOX_45, CrawlerConf.USER_AGENT_IE)));

    //请求参数
    private volatile Map<String, String> paramMap;

    //cookie
    private volatile Map<String, String> cookieMap;

    //请求头
    private volatile Map<String, String> headerMap;

    //超时时间
    private volatile int timeoutMillis;

    //停顿时间
    private volatile int pauseMillis = 0;

    // 允许扩散爬取，将会以现有URL为起点扩散爬取整站
    private volatile boolean allowSpread = true;

    // 已采集URL池
    private volatile Set<String> visitedUrlSet = Collections.synchronizedSet(new HashSet<String>());

    // 线程引用镜像
    private List<CrawlerThread> crawlerThreads = new CopyOnWriteArrayList<>();

    // 爬虫线程池
    private ExecutorService crawlers = Executors.newCachedThreadPool();


    public static class Builde {
        PocketCrawler crawler = new PocketCrawler();

        /**
         * 设置采集url
         *
         * @param urls
         * @return
         */
        public Builde setUrls(String... urls) {
            if (urls != null && urls.length > 0) {
                for (String url : urls) {
                    crawler.addUrl(url);
                }
            }
            return this;
        }

        /**
         * 设置白名单
         *
         * @param whiteUrlRegexs
         * @return
         */
        public Builde setWhiteUrlRegexs(String... whiteUrlRegexs) {
            if (whiteUrlRegexs != null && whiteUrlRegexs.length > 0) {
                for (String whiteUrlRegex : whiteUrlRegexs) {
                    crawler.getWhiteUrlRegexs().add(whiteUrlRegex);
                }
            }
            return this;
        }

        /**
         * 设置并发线程数
         *
         * @param threadCount
         * @return
         */
        public Builde setThreadCount(int threadCount) {
            crawler.threadCount = threadCount;
            return this;
        }

        /**
         * 页面解析器
         *
         * @param pageParser
         * @return
         */
        public Builde setPageParser(PageParser pageParser) {
            crawler.setPageParser(pageParser);
            return this;
        }

        /**
         * 请求方式
         *
         * @param requesttype
         * @return
         */
        public Builde setRequesttype(RequestType requesttype) {
            crawler.setRequestType(requesttype);
            return this;
        }

        /**
         * 设置用户请求代理，若不设置默认为谷歌，火狐，IE
         *
         * @return
         */
        public Builde setUserAgent(String... userAgents) {
            if (userAgents != null && userAgents.length > 0) {
                for (String userAgent : userAgents) {
                    if (crawler.userAgentList.contains(userAgent)) {
                        continue;
                    }
                    crawler.userAgentList.add(userAgent);
                }
            }
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param paramMap
         * @return
         */
        public Builde setParamMap(Map<String, String> paramMap) {
            if (CommonUtil.mapIsNotEmpty(paramMap)) {
                crawler.setParamMap(paramMap);
            }
            return this;
        }

        /**
         * 设置请求cookie
         *
         * @param cookieMap
         * @return
         */
        public Builde setCookie(Map<String, String> cookieMap) {
            if (CommonUtil.mapIsNotEmpty(cookieMap)) {
                crawler.setCookieMap(cookieMap);
            }
            return this;
        }

        /**
         * 设置请求头信息
         *
         * @param headerMap
         * @return
         */
        public Builde setHeader(Map<String, String> headerMap) {
            if (CommonUtil.mapIsNotEmpty(headerMap)) {
                crawler.setHeaderMap(headerMap);
            }
            return this;
        }

        /**
         * 设置超时时间
         *
         * @param timeoutMillis
         * @return
         */
        public Builde setTimeoutMillis(int timeoutMillis) {
            if (timeoutMillis > 0) {
                crawler.setTimeoutMillis(timeoutMillis);
            }
            return this;
        }

        /**
         * 设置是否允许扩散爬取，若为false则只爬取单个页面
         *
         * @param allowSpread
         * @return
         */
        public Builde setAllowSpread(boolean allowSpread) {
            crawler.setAllowSpread(allowSpread);
            return this;
        }

        /**
         * 设置停顿时间
         *
         * @param pauseMillis
         * @return
         */
        public Builde setPauseMillis(int pauseMillis) {
            if (pauseMillis > 0) {
                crawler.setPauseMillis(pauseMillis);
            }
            return this;
        }

        public PocketCrawler build() {
            return crawler;
        }
    }


    public void start() {
        if (unVisitedUrlQueue == null || unVisitedUrlQueue.size() <= 0) {
            throw new RuntimeException("待采集URL池里没有数据!!!");
        }
        if (threadCount < 1 || threadCount > 1000) {
            throw new RuntimeException("并发线程数异常!!!");
        }
        if (pageParser == null) {
            throw new RuntimeException("页面解析器异常!!!");
        }
        for (int i = 0; i < threadCount; i++) {
            CrawlerThread crawlerThread = new CrawlerThread(this);
            crawlerThreads.add(crawlerThread);
        }
        for (CrawlerThread crawlerThread : crawlerThreads) {
            crawlers.execute(crawlerThread);
        }
        crawlers.shutdown();

    }

    /**
     * 添加地址到待采集池,去重过滤
     *
     * @param url
     * @return
     */
    private boolean addUrl(String url) {
        if (!CommonUtil.isUrl(url)) {
            log.debug("【pocketCrawler】 url 不是一个正常的地址 ...:{}", url);
            return false;
        }
        if (unVisitedUrlQueue.contains(url)) {
            log.debug("【pocketCrawler】 该url已在待采集池中 ...:{}", url);
            return false;
        }
        if (visitedUrlSet.contains(url)) {
            log.debug("【pocketCrawler】 该url已被采集 ...:{}", url);
            return false;
        }
        log.info(url + "[已添加到采集池]");
        unVisitedUrlQueue.add(url);
        return true;
    }

    /**
     * url正则验证
     *
     * @param link
     * @return
     */
    public boolean validWhiteUrl(String link) {
        if (!CommonUtil.isUrl(link)) {
            return false;
        }

        if (whiteUrlRegexs != null && whiteUrlRegexs.size() > 0) {
            boolean underWhiteUrl = false;
            for (String whiteRegex : whiteUrlRegexs) {
                if (CommonUtil.matches(whiteRegex, link)) {
                    underWhiteUrl = true;
                }
            }
            if (!underWhiteUrl) {
                return false;
            }
        }
        return true;
    }


    public Set<String> getWhiteUrlRegexs() {
        return whiteUrlRegexs;
    }

    public void setPageParser(PageParser pageParser) {
        this.pageParser = pageParser;
    }

    public PageParser getPageParser() {
        return pageParser;
    }

    /**
     * 在带采集池获取url返回，并将其放入已采集池中
     *
     * @return
     */
    public String getUrl() {
        String link = null;
        try {
            link = unVisitedUrlQueue.take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (link != null) {
            visitedUrlSet.add(link);
        }
        return link;
    }

    public List<String> getUserAgentList() {
        return userAgentList;
    }

    public void setUserAgentList(List<String> userAgentList) {
        this.userAgentList = userAgentList;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public void setCookieMap(Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public boolean isAllowSpread() {
        return allowSpread;
    }

    public void setAllowSpread(boolean allowSpread) {
        this.allowSpread = allowSpread;
    }

    public LinkedBlockingQueue<String> getUnVisitedUrlQueue() {
        return unVisitedUrlQueue;
    }

    public void setUnVisitedUrlQueue(LinkedBlockingQueue<String> unVisitedUrlQueue) {
        this.unVisitedUrlQueue = unVisitedUrlQueue;
    }

    public int getPauseMillis() {
        return pauseMillis;
    }

    public void setPauseMillis(int pauseMillis) {
        this.pauseMillis = pauseMillis;
    }
}
