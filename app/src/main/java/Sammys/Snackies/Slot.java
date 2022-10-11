package Sammys.Snackies;

import java.util.NoSuchElementException;

public class Slot {
    
    private String name;
    private FoodItem contents;
    private int count;

    public Slot(String name, FoodItem contents, int count){
        this.name = name;
        this.contents = contents;
        this.count = count;
    }

    public FoodItem getContents(){
        return this.contents;
    }

    public FoodItem removeItem() throws NoSuchElementException {
        if (this.count == 0){
            throw new NoSuchElementException("No " + this.contents.getName() + " remaining");
        }
        this.count--;
        return this.contents;
    }

    public void restockContents(int newCount) throws IndexOutOfBoundsException {
        if (this.count + newCount > 15){ // If too many items, do not allow restocking
            throw new IndexOutOfBoundsException("This slot can only hold " + Integer.toString(15-this.count) + " more items");
        }

        this.count += newCount;
    }


    public String getName(){

        return this.name;
    }
}
