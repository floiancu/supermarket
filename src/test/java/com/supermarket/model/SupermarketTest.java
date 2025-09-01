package com.supermarket.model;

import com.supermarket.exception.CheckoutException;
import com.supermarket.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SupermarketTest {

    @Test
    void supermarket_whenTableIsEmpty() {
        Supermarket supermarket = new Supermarket("""
                | Item |Price for 1 item | Offer |
                |------|-----------------|-------|""");

        assertTrue(supermarket.getItems().isEmpty());
    }

    @Test
    void supermarket_whenOneItemGiven() {
        Supermarket supermarket = new Supermarket("""
                | Item | Price for 1 item | Offer |
                |--------|-----------------|---------------------|
                | Apple | 30 | - |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(30, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenHeaderIsLowercase() {
        Supermarket supermarket = new Supermarket("""
                | item | price for 1 item | offer |
                |--------|-----------------|---------------------|
                | Apple | 30 | - |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(30, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenNoSpaces() {
        Supermarket supermarket = new Supermarket("""
                |Item|Price for 1 item|Offer|
                |-|-|-|
                |Apple|30|-|""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(30, supermarket.getItems().getFirst().price());
        assertNull(supermarket.getItems().getFirst().offer());
    }

    @Test
    void supermarket_whenMultipleSpaces() {
        Supermarket supermarket = new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | 30               | -         |""");

        assertEquals(1, supermarket.getItems().size());
        assertEquals("Apple", supermarket.getItems().getFirst().name());
        assertEquals(30, supermarket.getItems().getFirst().price());
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
    void supermarket_whenMultipleItemsGiven() {
        Supermarket supermarket = this.buildSupermarket();

        assertEquals(4, supermarket.getItems().size());
        assertTrue(supermarket.getItems().contains(new Item("Apple", 30.0, new Offer(45.0, 2))));
        assertTrue(supermarket.getItems().contains(new Item("Banana", 50.0, new Offer(130.0, 3))));
        assertTrue(supermarket.getItems().contains(new Item("Peach", 60.0, null)));
        assertTrue(supermarket.getItems().contains(new Item("Kiwi", 20.0, null)));
    }

    @Test
    void supermarket_whenTableHasNoHeader() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("| Apple | 10 | - |"));
        assertEquals("Table header does not match format.", exception.getMessage());
    }

    @Test
    void supermarket_whenTableHasWrongFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Weight | Price | Offer     |
                |--------|--------|-------|-----------|
                | Apple  | 10     | 10    | 2 for 45  |"""));
        assertEquals("Table header does not match format.", exception.getMessage());
    }

    @Test
    void supermarket_whenTableLineHasWrongFormat() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price | Offer     |
                |--------|-------|-----------|
                | Apple  | 10    | 10    | 2 for 45  |"""));
        assertEquals("The number of columns does not match the format.", exception.getMessage());
    }

    @Test
    void supermarket_whenDuplicateItems() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | 30               | 2 for 45  |
                | apple  | 30               | -         |"""));
        assertEquals("Duplicate items in table.", exception.getMessage());
    }

    @Test
    void supermarket_whenPriceCanNotBeParsed() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item | Price for 1 item | Offer |
                |------|-----------------|-------|
                | Apple | x | - |""")
        );
        assertEquals("Cannot parse numeric value.", exception.getMessage());
    }

    @Test
    void supermarket_whenPriceIsNegative() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | -30              | -         |""")
        );
        assertEquals("The price must be a positive number.", exception.getMessage());
    }

    @Test
    void supermarket_whenOfferPriceIsNegative() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | 30               | 2 for -10 |""")
        );
        assertEquals("Offer price and quantity must be positive numbers.", exception.getMessage());
    }

    @Test
    void supermarket_whenOfferQuantityIsNegative() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | 30               | -1 for 10 |""")
        );
        assertEquals("Offer price and quantity must be positive numbers.", exception.getMessage());
    }

    @Test
    void supermarket_whenOfferQuantityIsZero() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item   | Price for 1 item | Offer    |
                |--------|------------------|----------|
                | Apple  | 30               | 0 for 10 |""")
        );
        assertEquals("Offer price and quantity must be positive numbers.", exception.getMessage());
    }

    @Test
    void supermarket_whenOfferCanNotBeParsed() {
        ParseException exception = assertThrows(ParseException.class, () -> new Supermarket("""
                | Item |Price for 1 item | Offer |
                |------|-----------------|-------|
                | Apple | 10 | buy one get one free |""")
        );
        assertEquals("Offer not formatted properly.", exception.getMessage());
    }

    @Test
    void checkout_whenOneItemGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(30, supermarket.checkout(List.of("Apple")));
    }

    @Test
    void checkout_whenIdenticalItemsGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(120, supermarket.checkout(List.of("Peach", "Peach")));
    }

    @Test
    void checkout_whenDifferentItemsGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(110, supermarket.checkout(List.of("Apple", "Peach", "Kiwi")));
    }

    @Test
    void checkout_whenItemsWithOfferGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(45, supermarket.checkout(List.of("Apple", "Apple")));
    }

    @Test
    void checkout_whenMoreItemsThanOfferGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(75, supermarket.checkout(List.of("Apple", "Apple", "Apple")));
    }

    @Test
    void checkout_whenMorFewerItemsThanOfferGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(100, supermarket.checkout(List.of("Banana", "Banana")));
    }

    @Test
    void checkout_whenItemsWithOfferGiven2(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(90, supermarket.checkout(List.of("Apple", "Apple", "Apple", "Apple")));
    }

    @Test
    void checkout_whenItemsWithOfferInRandomOrderGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(65, supermarket.checkout(List.of("Apple", "Kiwi", "Apple")));
    }

    @Test
    void checkout_whenItemsWithNonStandardNamesGiven(){
        Supermarket supermarket = this.buildSupermarket();
        assertEquals(160, supermarket.checkout(List.of("apple", "BANANA", "  kiwi   ", " pEaCh ")));
    }

    @Test
    void checkout_whenNonExistingItemGiven(){
        Supermarket supermarket = this.buildSupermarket();
        CheckoutException exception = assertThrows(CheckoutException.class, () -> supermarket.checkout(List.of("Potato")));
        assertEquals("Item Potato does not exist.", exception.getMessage());
    }

    private Supermarket buildSupermarket(){
        return new Supermarket("""
                | Item   | Price for 1 item | Offer     |
                |--------|------------------|-----------|
                | Apple  | 30               | 2 for 45  |
                | Banana | 50               | 3 for 130 |
                | Peach  | 60               | -         |
                | Kiwi   | 20               | -         |""");
    }
}