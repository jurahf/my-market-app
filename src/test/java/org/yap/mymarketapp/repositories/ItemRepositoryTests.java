package org.yap.mymarketapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yap.mymarketapp.model.ItemModel;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel item1;
    private ItemModel item2;
    private ItemModel item3;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll().block();

        item1 = new ItemModel();
        item1.setTitle("Java Programming");
        item1.setDescription("Learn Java from scratch");
        item1.setPrice(50L);
        itemRepository.save(item1).block();

        item2 = new ItemModel();
        item2.setTitle("Spring Boot Guide");
        item2.setDescription("Spring Boot for beginners");
        item2.setPrice(75L);
        itemRepository.save(item2).block();

        item3 = new ItemModel();
        item3.setTitle("Python Basics");
        item3.setDescription("Python programming language");
        item3.setPrice(40L);
        itemRepository.save(item3).block();
    }

    @Test
    void search_ShouldFindByTitle() {
        itemRepository.search("java")
                .as(StepVerifier::create)
                .assertNext(item -> assertThat(item.getTitle()).containsIgnoringCase("java"))
                .verifyComplete();
    }

    @Test
    void search_ShouldFindByDescription() {
        itemRepository.search("spring")
                .as(StepVerifier::create)
                .assertNext(item -> assertThat(item.getDescription()).containsIgnoringCase("spring"))
                .verifyComplete();
    }

    @Test
    void search_ShouldFindMultipleItems() {
        itemRepository.search("programming")
                .collectList()
                .as(StepVerifier::create)
                .assertNext(items -> assertThat(items).hasSize(2))
                .verifyComplete();
    }

    @Test
    void search_ShouldReturnEmpty_WhenNoMatch() {
        itemRepository.search("nonexistent")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void countByKeyword_ShouldReturnCorrectCount() {
        itemRepository.countByKeyword("programming")
                .as(StepVerifier::create)
                .assertNext(count -> assertThat(count).isEqualTo(2))
                .verifyComplete();
    }

    @Test
    void count_ShouldReturnTotalItems() {
        itemRepository.count()
                .as(StepVerifier::create)
                .assertNext(count -> assertThat(count).isEqualTo(3))
                .verifyComplete();
    }
}
