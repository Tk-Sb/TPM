package classes;

public class Item {
    public int id;
    public String name;
    public String category;
    public double price;
    public int stock;
    public int minimumStock;

    public Item () {}

    public Item (int id, String name, String category, double price, int stock, int minimumStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.minimumStock= minimumStock;
    }

    public int getId () {
        return id;
    }
}
