package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @Test
    @DisplayName("specification-based: calculate total for multiple rules")
    void testAmazonCalculateSpec() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getItems()).thenReturn(List.of(
                new Item(ItemType.ELECTRONIC, "Phone", 1, 500)
        ));

        Amazon amazon = new Amazon(mockCart, List.of(
                new RegularCost(),
                new ExtraCostForElectronics()
        ));

        double total = amazon.calculate();
        assertEquals(500 + 7.5, total);
    }

    @Test
    @DisplayName("specification-based: calculate total for empty cart")
    void testEmptyCartSpec() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getItems()).thenReturn(List.of());

        Amazon amazon = new Amazon(mockCart, List.of(
                new RegularCost(),
                new ExtraCostForElectronics(),
                new DeliveryPrice()
        ));

        assertEquals(0, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based: RegularCost calculates correct sum")
    void testRegularCostStructure() {
        RegularCost regularCost = new RegularCost();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Book", 2, 10),
                new Item(ItemType.ELECTRONIC, "Headphones", 1, 50)
        );

        double total = regularCost.priceToAggregate(items);
        assertEquals(70, total);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice calculates according to item count")
    void testDeliveryPriceStructure() {
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        assertEquals(0, deliveryPrice.priceToAggregate(List.of()));
        assertEquals(5, deliveryPrice.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "Item1", 1, 10),
                new Item(ItemType.OTHER, "Item2", 1, 10)
        )));
        assertEquals(12.5, deliveryPrice.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "Item1", 1, 10),
                new Item(ItemType.OTHER, "Item2", 1, 10),
                new Item(ItemType.OTHER, "Item3", 1, 10),
                new Item(ItemType.OTHER, "Item4", 1, 10)
        )));
        assertEquals(20, deliveryPrice.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "Item1", 1, 10),
                new Item(ItemType.OTHER, "Item2", 1, 10),
                new Item(ItemType.OTHER, "Item3", 1, 10),
                new Item(ItemType.OTHER, "Item4", 1, 10),
                new Item(ItemType.OTHER, "Item5", 1, 10),
                new Item(ItemType.OTHER, "Item6", 1, 10),
                new Item(ItemType.OTHER, "Item7", 1, 10),
                new Item(ItemType.OTHER, "Item8", 1, 10),
                new Item(ItemType.OTHER, "Item9", 1, 10),
                new Item(ItemType.OTHER, "Item10", 1, 10),
                new Item(ItemType.OTHER, "Item11", 1, 10)
        )));
    }

    @Test
    @DisplayName("structural-based: ExtraCostForElectronics triggers correctly")
    void testExtraCostForElectronics() {
        ExtraCostForElectronics extra = new ExtraCostForElectronics();
        assertEquals(0, extra.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "Book", 1, 10)
        )));
        assertEquals(7.5, extra.priceToAggregate(List.of(
                new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000)
        )));
        assertEquals(7.5, extra.priceToAggregate(List.of(
                new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000),
                new Item(ItemType.ELECTRONIC, "Phone", 1, 500)
        )));
    }
}
