package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AmazonIntegrationTest {

    private Database db;
    private ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeAll
    void setup() {
        db = new Database();
        cart = new ShoppingCartAdaptor(db);
    }

    @BeforeEach
    void resetDb() {
        db.resetDatabase();
        cart.getItems().clear();
    }

    @Test
    @DisplayName("specification-based: calculate total for multiple items including delivery and electronics fee")
    void testCalculateTotalSpec() {
        cart.add(new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000));
        cart.add(new Item(ItemType.OTHER, "Book", 2, 20));

        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));

        double total = amazon.calculate();
        assertEquals(1040 + 5 + 7.5, total);
    }

    @Test
    @DisplayName("specification-based: calculate total for empty cart")
    void testEmptyCartSpec() {
        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));
        assertEquals(0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based: calculate delivery price for >10 items")
    void testDeliveryOverTenItems() {
        for (int i = 0; i < 11; i++) {
            cart.add(new Item(ItemType.OTHER, "Item" + i, 1, 10));
        }

        amazon = new Amazon(cart, List.of(
                new DeliveryPrice()
        ));

        assertEquals(20, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based: database stores and retrieves items correctly")
    void testDatabasePersistence() {
        cart.add(new Item(ItemType.OTHER, "Book", 3, 15));
        var itemsFromDb = cart.getItems();

        assertEquals(1, itemsFromDb.size());
        assertEquals("Book", itemsFromDb.get(0).getName());
        assertEquals(3, itemsFromDb.get(0).getQuantity());
    }

    @Test
    @DisplayName("structural-based: database handles multiple electronics")
    void testDatabaseMultipleElectronics() {
        cart.add(new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000));
        cart.add(new Item(ItemType.ELECTRONIC, "Phone", 2, 500));

        var itemsFromDb = cart.getItems();
        assertEquals(2, itemsFromDb.size());
        assertEquals("Laptop", itemsFromDb.get(0).getName());
        assertEquals("Phone", itemsFromDb.get(1).getName());
    }

    @Test
    @DisplayName("structural-based: numberOfItems returns correct count")
    void testNumberOfItems() {
        assertEquals(0, cart.numberOfItems());

        cart.add(new Item(ItemType.OTHER, "Book", 1, 10));
        assertEquals(1, cart.numberOfItems());

        cart.add(new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000));
        assertEquals(2, cart.numberOfItems());
    }

    @Test
    @DisplayName("specification-based: numberOfItems works with multiple same items")
    void testNumberOfItemsWithDuplicates() {
        cart.add(new Item(ItemType.OTHER, "Book", 2, 15));
        cart.add(new Item(ItemType.OTHER, "Book", 3, 15));
        assertEquals(2, cart.numberOfItems());
    }
}
