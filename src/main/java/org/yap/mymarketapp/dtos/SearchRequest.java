package org.yap.mymarketapp.dtos;


public record SearchRequest(String search, SortFieldEnum sort, int pageNumber, int pageSize) {
}
