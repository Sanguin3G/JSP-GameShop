/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Cart {

    private List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public Cart(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Get item by ID using Java 11 streams and Optional
    private Optional<Item> findItemByID(int id) {
        return items.stream()
                   .filter(item -> item.getProduct().getId() == id)
                   .findFirst();
    }

    // Get item by ID
    private Item getItemByID(int id) {
        return findItemByID(id).orElse(null);
    }

    // Get quantity by ID
    public int getQuantityByID(int id) {
        return findItemByID(id)
                .map(Item::getQuantity)
                .orElse(0);
    }

    public void addItem(Item t) {
        findItemByID(t.getProduct().getId())
                .ifPresentOrElse(
                    // If item exists, increase quantity
                    existingItem -> existingItem.setQuantity(existingItem.getQuantity() + t.getQuantity()),
                    // If item doesn't exist, add to list
                    () -> items.add(t)
                );
    }

    public void removeItem(int id) {
        findItemByID(id).ifPresent(item -> items.remove(item));
    }

    // Calculate total money using streams
    public double getTotalMoney() {
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
    }
}
