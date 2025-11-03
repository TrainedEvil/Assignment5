package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BarnesAndNobleTest {

    // ----- SPECIFICATION-BASED TESTS -----

    @Test
    @DisplayName("specification-based: returns null when order map is null")
    void testNullOrderReturnsNull() {
        BarnesAndNoble bn = new BarnesAndNoble(isbn -> null, (b, q) -> {});
        assertNull(bn.getPriceForCart(null));
    }

    @Test
    @DisplayName("specification-based: calculates total price correctly for available stock")
    void testTotalPriceCalculatedCorrectly() {
        Book bookA = new Book("111", 10, 5);
        BookDatabase db = isbn -> bookA;
        BuyBookProcess process = (book, qty) -> {};

        BarnesAndNoble bn = new BarnesAndNoble(db, process);
        Map<String, Integer> order = new HashMap<>();
        order.put("111", 3);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(30, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("specification-based: records unavailable quantity when request exceeds stock")
    void testUnavailableBooksRecorded() {
        Book bookA = new Book("222", 20, 2);
        BookDatabase db = isbn -> bookA;
        BuyBookProcess process = (book, qty) -> {};

        BarnesAndNoble bn = new BarnesAndNoble(db, process);
        Map<String, Integer> order = new HashMap<>();
        order.put("222", 5);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(40, summary.getTotalPrice());
        assertEquals(3, summary.getUnavailable().get(bookA));
    }

    @Test
    @DisplayName("specification-based: getPrice and getQuantity report constructor values")
    void testBookGetters() {
        Book b = new Book("ISBN-100", 42, 7);
        assertEquals(42, b.getPrice());
        assertEquals(7, b.getQuantity());
    }

    @Test
    @DisplayName("specification-based: equals returns true for different instances with same ISBN")
    void testBookEqualsSameISBN() {
        Book a = new Book("ISBN-XYZ", 10, 1);
        Book b = new Book("ISBN-XYZ", 999, 99);
        assertNotSame(a, b);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
    }


    // ----- STRUCTURAL-BASED TESTS -----

    @Test
    @DisplayName("structural-based: retrieveBook handles insufficient quantity branch")
    void testRetrieveBookBranchInsufficientQuantity() {
        Book book = new Book("333", 15, 1);
        final boolean[] called = {false};

        BookDatabase db = isbn -> book;
        BuyBookProcess process = (b, q) -> called[0] = true;

        BarnesAndNoble bn = new BarnesAndNoble(db, process);
        Map<String, Integer> order = new HashMap<>();
        order.put("333", 4);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertTrue(called[0]);
        assertEquals(15, summary.getTotalPrice());
        assertEquals(3, summary.getUnavailable().get(book));
    }

    @Test
    @DisplayName("structural-based: retrieveBook handles sufficient quantity branch")
    void testRetrieveBookBranchSufficientQuantity() {
        Book book = new Book("444", 25, 10);
        final boolean[] called = {false};

        BookDatabase db = isbn -> book;
        BuyBookProcess process = (b, q) -> {
            assertEquals(5, q);
            called[0] = true;
        };

        BarnesAndNoble bn = new BarnesAndNoble(db, process);
        Map<String, Integer> order = new HashMap<>();
        order.put("444", 5);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertTrue(called[0]);
        assertEquals(125, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("structural-based: loop through multiple items in order map")
    void testMultipleBooksLoop() {
        Book book1 = new Book("AAA", 10, 2);
        Book book2 = new Book("BBB", 5, 1);

        BookDatabase db = isbn -> isbn.equals("AAA") ? book1 : book2;
        final int[] totalBought = {0};
        BuyBookProcess process = (b, q) -> totalBought[0] += q;

        BarnesAndNoble bn = new BarnesAndNoble(db, process);
        Map<String, Integer> order = new HashMap<>();
        order.put("AAA", 2);
        order.put("BBB", 1);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(25, summary.getTotalPrice());
        assertEquals(3, totalBought[0]);
    }

    @Test
    @DisplayName("structural-based: Book equals this == o branch")
    void testBookEqualsSameReference() {
        Book a = new Book("REF-1", 5, 2);
        assertTrue(a.equals(a));
    }

    @Test
    @DisplayName("structural-based: Book equals null branch")
    void testBookEqualsNull() {
        Book a = new Book("NULL-TEST", 1, 1);
        assertFalse(a.equals(null));
    }

    @Test
    @DisplayName("structural-based: Book equals different class branch")
    void testBookEqualsDifferentClass() {
        Book a = new Book("CLASS-TEST", 1, 1);
        String other = "CLASS-TEST";
        assertFalse(a.equals(other));
    }

    @Test
    @DisplayName("structural-based: Book equals different ISBN")
    void testBookEqualsDifferentISBN() {
        Book a = new Book("ISBN-A", 10, 2);
        Book b = new Book("ISBN-B", 10, 2);
        assertFalse(a.equals(b));
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("structural-based: Book hashCode consistent with equals")
    void testBookHashCodeContract() {
        Book a = new Book("HASH-1", 3, 3);
        Book b = new Book("HASH-1", 4, 5);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }
}
