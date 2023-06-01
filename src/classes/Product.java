package classes;

public class Product {

    int id, quantity;
    double price;
    String name, category, description;

    public Product(String category) {
        this.category = category;
    }

    public Product(int id, int quantity, double price, String name, String category, String description) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public Product(int id, String name, int quantity) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
