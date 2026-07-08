package org.yap.mymarketapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ItemService {

    @Autowired
    public ItemRepository repository;

    public ItemDto getById(long id) {
        Optional<ItemModel> itemOpt = repository.findById(id);

        if (itemOpt.isEmpty())
            throw new IllegalArgumentException();

        return convertFromDB(itemOpt.get());
    }

    public SearchResponse getAll(SearchRequest request) {

        Pageable pageable = PageRequest.of(
                request.pageNumber < 1 ? 0 : request.pageNumber - 1,
                request.pageSize < 0 ? 5 : request.pageSize,
                getSort(request.sort));

        Page<ItemModel> page;
        if (request.search != null && !request.search.isEmpty()) {
             page = repository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    request.search, request.search, pageable
            );
        }
        else {
            page = repository.findAll(pageable);
        }

        List<ItemDto> dtos = page.getContent().stream()
                .map(x -> convertFromDB(x)
                )
                .toList();

        var items = splitIntoChunks(dtos, 3);

        return new SearchResponse(
                request.search,
                request.sort,
                new PagingDto(
                        request.pageSize,
                        request.pageNumber,
                        page.hasPrevious(),
                        page.hasNext()),
                items);
    }

    private ItemDto convertFromDB(ItemModel x) {
        return new ItemDto(
                x.id,
                x.title,
                x.description,
                x.imgPath,
                x.price,
                x.getCart().map(c -> c.getCount()).orElse(0)
        );
    }

    private List<List<ItemDto>> splitIntoChunks(List<ItemDto> list, int chunkSize) {
        return IntStream.range(0, (list.size() + chunkSize - 1) / chunkSize)
                .mapToObj(i -> {
                    int start = i * chunkSize;
                    int end = Math.min(start + chunkSize, list.size());
                    List<ItemDto> chunk = new ArrayList<>(list.subList(start, end));

                    while (chunk.size() < chunkSize) {
                        chunk.add(new ItemDto(-1, "", "", "", 0, 0));
                    }

                    return chunk;
                })
                .collect(Collectors.toList());

    }

    private Sort getSort(SortFieldEnum sort) {
        if (sort == null || sort == SortFieldEnum.NO) {
            return Sort.unsorted();
        }

        return switch (sort) {
            case ALPHA -> Sort.by("title").ascending();
            case PRICE -> Sort.by("description").ascending();
            default -> Sort.unsorted();
        };
    }


}
