package model;

import enums.RequestType;

import java.net.Proxy;
import java.util.Map;

/**
 * 页面载入参数
 * Created by wangyang on 2018/1/11.
 */
public class PageLoadConfig {

    private String url;
    private Map<String, String> paramMap;
    private Map<String, String> cookieMap;
    private Map<String, String> headerMap;
    private String userAgent;
    private String referrer;
    private RequestType requestType;
    private int timeoutMillis;
    private Proxy proxy;


    public PageLoadConfig() {
    }


    public PageLoadConfig(String url, Map<String, String> paramMap, Map<String, String> cookieMap, Map<String, String> headerMap, String userAgent, String referrer, RequestType requestType, int timeoutMillis, Proxy proxy) {
        this.url = url;
        this.paramMap = paramMap;
        this.cookieMap = cookieMap;
        this.headerMap = headerMap;
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.requestType = requestType;
        this.timeoutMillis = timeoutMillis;
        this.proxy = proxy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
