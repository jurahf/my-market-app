package org.yap.mymarketapp.dtos;

import org.springframework.web.bind.annotation.RequestParam;

public class SearchRequest {
    public String search;
    public SortFieldEnum sort;
    public int pageNumber;
    public int pageSize;

    public SearchRequest(String search, SortFieldEnum sort, int pageNumber, int pageSize) {
        this.search = search;
        this.sort = sort;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public SortFieldEnum getSort() {
        return sort;
    }

    public void setSort(SortFieldEnum sort) {
        this.sort = sort;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
