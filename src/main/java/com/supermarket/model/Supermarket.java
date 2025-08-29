package com.supermarket.model;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Supermarket {

    private final Map<String, Item> items;

    public Supermarket(String pricingTable) {
        this.items = this.parsePricingTable(pricingTable);
    }

    public Double checkout(List<String> scannedItems){
        Map<String, Long> itemsAndQuantitiesInBasket = scannedItems.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return itemsAndQuantitiesInBasket.entrySet().stream().map(this::getPrice).reduce(0.0, Double::sum);
    }

    public List<Item> getItems() {
        return List.copyOf(items.values());
    }

    private Map<String, Item> parsePricingTable(String pricingTable) {
        try {
            String[] splitLines = pricingTable.split("\n");
            if (isTableHeaderPresent(splitLines)) {
                return Arrays.stream(splitLines).skip(2)
                        .map(this::parsePricingTableLine)
                        .collect(Collectors.toUnmodifiableMap(item -> item.name().toLowerCase(), Function.identity()));
            }
            throw new RuntimeException("Table header does not match format.");
        } catch (Exception e) {
            throw new RuntimeException("Error parsing pricing table.", e);
        }
    }

    private boolean isTableHeaderPresent(String[] splitLines) {
        if (splitLines.length < 2) {
            return false;
        }
        String[] firstLine = splitLines[0].substring(1).split("[|]");
        if (firstLine.length != 3
                || !firstLine[0].toLowerCase().contains("item")
                || !firstLine[1].toLowerCase().contains("price")
                || !firstLine[2].toLowerCase().contains("offer")) {
            return false;
        }
        return !splitLines[1].matches("[^-| ]");
    }

    private Item parsePricingTableLine(String line) {
        String[] lineElements = line.substring(1).split("[|]");
        if(lineElements.length != 3) {
            throw new RuntimeException("The number of columns do not match the format.");
        }
        return new Item(lineElements[0].trim(), Double.parseDouble(lineElements[1]),
                lineElements[2].trim().equals("-") ? null : this.buildOffer(lineElements[2]));
    }

    private Offer buildOffer(String offerText) {
        String[] offerElements = offerText.trim().split(" ");
        return new Offer(Double.parseDouble(offerElements[2]), Integer.parseInt(offerElements[0]));
    }

    private Double getPrice(Map.Entry<String, Long> itemAndQuantity) {
        Item item = this.items.get(itemAndQuantity.getKey().trim().toLowerCase());
        return (item.offer() == null) ?
                itemAndQuantity.getValue() * item.price()
                : (itemAndQuantity.getValue() / item.offer().quantity()) * item.offer().price() +
                itemAndQuantity.getValue() % item.offer().quantity() * item.price();
    }

}
