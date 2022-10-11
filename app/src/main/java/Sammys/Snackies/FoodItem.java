package Sammys.Snackies;

enum Category {
    DRINK, CHOCOLATE, CHIPS, CANDY
}

public class FoodItem {

    private String name;
    private float price;
    private Category category;
    public FoodItem(String name, float price, Category category){
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName(){
        return this.name;
    }

    public float getPrice(){
        return this.price;
    }

    public String toString(){
        return this.name + " $" + String.format("%.2f", this.price);
    }

    public boolean isCategory(Category cat){
        return cat == this.category;
    }
}