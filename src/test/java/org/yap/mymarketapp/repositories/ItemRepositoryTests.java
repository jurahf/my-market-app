package org.yap.mymarketapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.yap.mymarketapp.model.ItemModel;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel item1;
    private ItemModel item2;
    private ItemModel item3;

    @BeforeEach
    void setUp() {
        item1 = new ItemModel();
        item1.setTitle("Java Programming");
        item1.setDescription("Learn Java from scratch");
        item1.setPrice(50L);
        itemRepository.save(item1);

        item2 = new ItemModel();
        item2.setTitle("Spring Boot Guide");
        item2.setDescription("Spring Boot for beginners");
        item2.setPrice(75L);
        itemRepository.save(item2);

        item3 = new ItemModel();
        item3.setTitle("Python Basics");
        item3.setDescription("Python programming language");
        item3.setPrice(40L);
        itemRepository.save(item3);
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_ShouldFindByTitle() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "java";

        // When
        Page<ItemModel> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("java");
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_ShouldFindByDescription() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "spring";

        // When
        Page<ItemModel> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).containsIgnoringCase("spring");
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_ShouldFindMultipleItems() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "programming";

        // When
        Page<ItemModel> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(ItemModel::getDescription)
                .anyMatch(desc -> desc.contains("programming"));
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_ShouldReturnEmpty_WhenNoMatch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "nonexistent";

        // When
        Page<ItemModel> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_ShouldSupportPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<ItemModel> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "programming", "programming", pageable
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isGreaterThan(1);
    }
}