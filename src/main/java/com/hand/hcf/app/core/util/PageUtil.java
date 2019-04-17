package com.hand.hcf.app.core.util;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Created by Administrator on 2017-08-17.
 */
public final class PageUtil {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";

    private PageUtil(){

    };

    public static Page getPage(Pagination pagination, List records){
        Page page = new Page();
        page.setSize(pagination.getSize());
        page.setCurrent(pagination.getCurrent());
        page.setRecords(records);
        page.setTotal(pagination.getTotal());
        return page;
    }

    public static void startPage(int page,int size){
        PageHelper.startPage(page + 1, size);
    }

    public static Page getPage(int page, int size){
        return new Page(page + 1,size);
    }

    public static void remove(){
        PageHelper.remove();
    }

    public static Page getPage(Pageable pageable){
        return  new Page(pageable.getPageNumber()+1,pageable.getPageSize());
    }

    @Deprecated
    public static HttpHeaders generateHttpHeaders(Page<?> page, String baseURL)  {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", "" + page.getTotal());
        headers.add("Link", baseURL);
        return headers;
    }

    public static HttpHeaders getTotalHeader(Page<?> page)  {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", "" + page.getTotal());
        return headers;
    }

    /**
     * 获取page起始下标  仅用于容器分页，不能用于数据库分页
     * @param page
     * @return
     */
    public static Integer getPageStartIndex(Page<?> page){
        int start = (page.getCurrent() - 1) * page.getSize();
        return start < 0 ? 0 : start;
    }

    /**
     * 获取page结束下标  仅用于容器分页，不能用于数据库分页
     * @param page
     * @return
     */
    public static Integer getPageEndIndex(Page<?> page){
        int end = page.getCurrent() * page.getSize();
        return end;
    }

    /**
     * 手工处理分页信息，返回Page
     * @param page
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPage(Page<T> page, List<T> collection){
        page.setRecords(pageHandler(page,collection));
        return page;
    }

    /**
     * 手工处理分页信息，返回List，并设置page相关属性
     * @param page
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> List<T> pageHandler(Page<T> page, List<T> collection){
        int total = collection.size();
        Integer pageEndIndex = getPageEndIndex(page);
        List<T> subList = collection.subList(getPageStartIndex(page), pageEndIndex > total ? total : pageEndIndex);
        page.setTotal(total);
        return subList;
    }
}
