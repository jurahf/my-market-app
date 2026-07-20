package org.yap.mymarketapp.dtos;

import java.util.List;

public record SearchResponse(String search, SortFieldEnum sort, PagingDto paging, List<List<ItemDto>> items) {

}
