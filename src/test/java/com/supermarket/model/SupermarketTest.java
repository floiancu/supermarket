package com.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SupermarketTest {

    @Test
    void supermarket_whenTableIsEmpty() {
        Supermarket supermarket = new Supermarket("""
                | Item |Price for 1 item | Offer |
                |--------|-----------------|---------------------|""");

        assertTrue(supermarket.getItems().isEmpty());
    }

    @Test
    void supermarket_whenOneItemGiven() {
        Supermarket supermarket = new Supermarket("""
                | Item | Price for 1 item | Offer |
                |--------|-----------------|---------------------|
                | Apple | 10 | - |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(10, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenNoSpaces() {
        Supermarket supermarket = new Supermarket("""
                |Item|Price for 1 item|Offer|
                |-|-|-|
                |Apple|10|-|""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(10, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenOneItemHasTwoWordNameAndDecimalPrice() {
        Supermarket supermarket = new Supermarket("""
                | Item | Price for 1 item | Offer |
                |--------|-----------------|---------------------|
                | Russet Potato | 15.5 | - |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Russet Potato", supermarket.getItems().getFirst().name());
        assertEquals(15.5, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenItemWithOfferGiven() {
        Supermarket supermarket = new Supermarket("""
                | Item | Price for 1 item | Offer |
                |--------|-----------------|---------------------|
                | Apple | 10 | 3 for 24.9 |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(10, supermarket.getItems().getFirst().price());
        assertEquals(24.9, supermarket.getItems().getFirst().offer().price());
        assertEquals(3, supermarket.getItems().getFirst().offer().quantity());
    }

    @Test
    void supermarket_whenTableHasNoHeader() {
        assertThrows(RuntimeException.class, () -> new Supermarket("| Apple | 10 | - |"));
    }

    @Test
    void supermarket_whenPriceCanNotBeParsed() {
        assertThrows(RuntimeException.class, () -> new Supermarket("""
                | Item | Price for 1 item | Offer |
                |------|-----------------|-------|
                | Apple | x | - |""")
        );
    }

    @Test
    void supermarket_whenOfferCanNotBeParsed() {
        assertThrows(RuntimeException.class, () -> new Supermarket("""
                | Item |Price for 1 item | Offer |
                |------|-----------------|-------|
                | Apple | 10 | buy one get one free |""")
        );
    }

}