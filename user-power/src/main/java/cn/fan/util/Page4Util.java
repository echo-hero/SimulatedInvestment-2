package cn.fan.util;

import java.util.List;

public class Page4Util<T> {

    public Page4Util() {
    }

    public Page4Util(int number, int pageSize, int navigatePages){
        this.number=number;
        this.pageSize=pageSize;
        this.navigatePages=navigatePages;
    }

    //进行分页
    public void page(List<T> tList){
        totalElements=tList.size();

        if(totalElements==0)
            return;

        totalPages=totalElements/pageSize;
        if(totalElements%pageSize>0)
            totalPages++;

        number = number<0?0:number;
        number = number>=totalPages?(totalPages-1):number;

        first = number==0?true:false;
        last = number==(totalPages-1)?true:false;

        int start=number*pageSize;
        int end=start+pageSize;
        end = end>totalElements?totalElements:end;
        content=tList.subList(start,end);
        calcNavigatepageNums();
    }

    private void calcNavigatepageNums(){
        if(totalPages<=navigatePages){//当总页数小于或等于导航页码数时,展示全部
            navigatepageNums=new int[totalPages];
            for(int i=0;i<totalPages;i++){
                navigatepageNums[i]=i+1;
            }
        }else{//当总页数小于或等于导航页码数时
            int p=navigatePages%2;
            int startNum=number-navigatePages/2+p;
            int endNum=startNum+navigatePages;

            if(startNum<1){
                startNum=1;
            }else if(endNum>totalPages){
                startNum=totalPages-navigatePages;
            }

            for(int i=0;i<navigatePages;i++){
                navigatepageNums[i]=startNum++;
            }

        }
    }

    private int totalElements;//总数据量
    private int numberOfElements;//当前页面数据量 暂时没用到
    private int totalPages;//总页数(基1)
    private int number;//第几页（基0）
    private int pageSize;//每页显示的条数（最后一页除外）
    private boolean first;//是否是首页
    private boolean last;//是否是末页
    private List<T> content;//分页后的集合
    private int navigatePages;//前端显示的页数数量
    private int[] navigatepageNums;//前端显示的页数集合

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

}
