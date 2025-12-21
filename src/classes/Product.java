package classes;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public int id;
    public String name;
    public List<RequiredItem> requiredItems;

    public Product() {
        this.requiredItems = new ArrayList<>();
    }

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
        this.requiredItems = new ArrayList<>();
    }

    public static class RequiredItem {
        public String item;
        public int quantity;

        public RequiredItem() {}

        public RequiredItem(String item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    public int getId () {
        return id;
    }
}