package vo;


import annotation.PageFieldSelect;
import annotation.PageSelect;

/**
 * 课程数据组装
 * Created by wangyang on 2017/12/27.
 */
@PageSelect(cssQuery = "body")
public class DFClassDataVO {

    //课堂名称
    @PageFieldSelect(cssQuery = ".right_referral .Issue .wrap")
    private String title;

    //课程价格
    @PageFieldSelect(cssQuery = ".right_referral .Issue .mb10 .f18 .f30")
    private String price;

    //主讲老师
    @PageFieldSelect(cssQuery = ".Detail > .fl:nth-child(3) > li")
    private String teacher;

    //观看人数
    @PageFieldSelect(cssQuery = ".Detail > .ml30 > li:nth-child(3)")
    private String watchNum;

    //购买人数
    @PageFieldSelect(cssQuery = ".Detail > .fl:nth-child(3) > li:nth-child(3)")
    private String buyNum;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(String watchNum) {
        this.watchNum = watchNum;
    }

    public String getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(String buyNum) {
        this.buyNum = buyNum;
    }

    @Override
    public String toString() {
        return "DFClassDataVO{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", teacher='" + teacher + '\'' +
                ", watchNum='" + watchNum + '\'' +
                ", buyNum='" + buyNum + '\'' +
                '}';
    }

    public static void main(String[] args) {

        String s = "主讲老师：杨顺坤";
        System.out.println(s.substring(5));

    }
}
