package org.yap.mymarketapp.dtos;

public class ParamsDto {

    public String search;

    public SortFieldEnum sort;

    public PagingDto Paging;


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

    public PagingDto getPaging() {
        return Paging;
    }

    public void setPaging(PagingDto paging) {
        Paging = paging;
    }
}
