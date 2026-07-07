package org.yap.mymarketapp.dtos;

import java.util.List;

public class SearchResponse {

    public String search;

    public SortFieldEnum sort;

    public PagingDto paging;

    public List<List<ItemDto>> items;

    public SearchResponse(String search, SortFieldEnum sort, PagingDto paging, List<List<ItemDto>> items) {
        this.search = search;
        this.sort = sort;
        this.paging = paging;
        this.items = items;
    }

    public List<List<ItemDto>> getItems() {
        return items;
    }

    public void setItems(List<List<ItemDto>> items) {
        this.items = items;
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

    public PagingDto getPaging() {
        return paging;
    }

    public void setPaging(PagingDto paging) {
        paging = paging;
    }
}
