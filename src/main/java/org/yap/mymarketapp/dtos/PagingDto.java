package org.yap.mymarketapp.dtos;

public class PagingDto {

    public int pageSize;

    public int pageNumber;

    public boolean hasPrevious;

    public boolean hasNext;

    public PagingDto(int pageSize, int pageNumber, boolean hasPrevious, boolean hasNext) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    public int pageSize() {
        return pageSize;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
