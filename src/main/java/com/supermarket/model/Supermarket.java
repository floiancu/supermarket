package com.supermarket.model;


import com.supermarket.exception.CheckoutException;
import com.supermarket.exception.ParseException;

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
            throw new ParseException("Table header does not match format.");
        } catch (NumberFormatException e) {
            throw new ParseException("Cannot parse numeric value.");
        } catch (IllegalStateException e) {
            throw new ParseException("Duplicate items in table.");
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
            throw new ParseException("The number of columns does not match the format.");
        }
        double price = Double.parseDouble(lineElements[1]);
        if(price <= 0) {
            throw new ParseException("The price must be a positive number.");
        }
        return new Item(lineElements[0].trim(), price, lineElements[2].trim().equals("-") ? null : this.buildOffer(lineElements[2]));
    }

    private Offer buildOffer(String offerText) {
        String[] offerElements = offerText.trim().split(" ");
        if(offerElements.length != 3 || !offerElements[1].trim().equals("for")) {
            throw new ParseException("Offer not formatted properly.");
        }
        double offerPrice = Double.parseDouble(offerElements[2]);
        int offerQuantity = Integer.parseInt(offerElements[0]);
        if(offerPrice <= 0 || offerQuantity <= 0) {
            throw new ParseException("Offer price and quantity must be positive numbers.");
        }
        return new Offer(offerPrice, offerQuantity);
    }

    private Double getPrice(Map.Entry<String, Long> itemAndQuantity) {
        Item item = this.items.get(itemAndQuantity.getKey().trim().toLowerCase());
        if(item == null) {
            throw new CheckoutException(String.format("Item %s does not exist.", itemAndQuantity.getKey()));
        }
        return (item.offer() == null) ?
                itemAndQuantity.getValue() * item.price()
                : (itemAndQuantity.getValue() / item.offer().quantity()) * item.offer().price() +
                itemAndQuantity.getValue() % item.offer().quantity() * item.price();
    }

}
