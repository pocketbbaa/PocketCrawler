import com.alibaba.fastjson.JSONObject;
import conf.CrawlerConf;
import crawler.PocketCrawler;
import enums.RequestType;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import parser.PageParser;
import vo.DFClassDataVO;
import vo.PocketTestVO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wangyang on 2018/1/18.
 */
public class PocketTest {


    public static void main(String[] args) {


        PocketCrawler pocketCrawler = new PocketCrawler.Builde()
                .setUrls("http://www.dianfuketang.com")
                .setWhiteUrlRegexs("^http://www.dianfuketang.com.*")
                .setThreadCount(5)
                .setAllowSpread(true)
                .setPageParser(new PageParser<DFClassDataVO>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, DFClassDataVO pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        if (!StringUtil.isBlank(pageVo.getTitle()) && !StringUtil.isBlank(pageVo.getTeacher())
                                && !StringUtil.isBlank(pageVo.getWatchNum()) && !StringUtil.isBlank(pageVo.getBuyNum())) {
                            String teacherC = pageVo.getTeacher();
                            String teacher = teacherC.substring(5);
                            String watchNumC = pageVo.getWatchNum();
                            String watchNum = watchNumC.substring(5, watchNumC.length() - 1);
                            String buyNumC = pageVo.getBuyNum();
                            String buyNum = buyNumC.substring(6, watchNumC.length() - 1);
                            System.out.println(pageUrl);
                            pageVo.setTeacher(teacher);
                            pageVo.setWatchNum(watchNum);
                            pageVo.setBuyNum(buyNum);
                            System.out.println(pageVo);
                        }
                    }
                }).build();

        pocketCrawler.start();

    }

}
