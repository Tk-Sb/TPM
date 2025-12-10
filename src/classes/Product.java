package classes;

import java.util.HashMap;

public class Product {
    public int id;
    public String name;
    public HashMap<Item, Integer> resquiredItems = new HashMap<>();
}
