package com.supermarket.model;


import java.util.Arrays;
import java.util.List;

public class Supermarket {

    private final List<Item> items;

    public Supermarket(String pricingTable) {
        this.items = this.parsePricingTable(pricingTable);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    private List<Item> parsePricingTable(String pricingTable) {
        try {
            String[] splitLines = pricingTable.split("\n");
            if (isTableHeaderPresent(splitLines)) {
                return Arrays.stream(splitLines).skip(2).map(this::parsePricingTableLine).toList();
            }
            throw new RuntimeException("Table header not present.");
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
        return new Item(lineElements[0].trim(), Double.parseDouble(lineElements[1]),
                lineElements[2].trim().equals("-") ? null : this.buildOffer(lineElements[2]));
    }

    private Offer buildOffer(String offerText) {
        String[] offerElements = offerText.trim().split(" ");
        return new Offer(Double.parseDouble(offerElements[2]), Integer.parseInt(offerElements[0]));
    }

}
