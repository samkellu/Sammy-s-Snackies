package Sammys.Snackies;

enum Category {
    DRINK, CHOCOLATE, CHIPS, CANDY
}

public class FoodItem {

    private String name;
    private double price;
    private Category category;

    public FoodItem(String name, double price, Category category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isCategory(Category cat) {
        return cat == this.category;
    }

    public String toString() {
        return this.name + " $" + String.format("%.2f", this.price);
    }
}