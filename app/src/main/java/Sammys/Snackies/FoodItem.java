package Sammys.Snackies;

public class FoodItem {


    public String name;
    public float price;
    public FoodItem(String name, float price){
        this.name = name;
        this.price = price;
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
}