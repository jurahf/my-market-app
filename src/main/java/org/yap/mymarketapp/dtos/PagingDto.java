package org.yap.mymarketapp.dtos;

public record PagingDto(int pageSize, int pageNumber, boolean hasPrevious, boolean hasNext) {

}
