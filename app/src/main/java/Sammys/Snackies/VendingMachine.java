package Sammys.Snackies;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class VendingMachine {
    private HashMap<String, Slot> allSlots;

    public VendingMachine(){
        this.allSlots = new HashMap<String, Slot>();
    }


    public void addSlot(String slotName, FoodItem slotContents, int contentCount){
        this.allSlots.put(slotName, new Slot(slotName, slotContents, contentCount));
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        for (Slot currentSlot : this.allSlots.values()){
            output.append(currentSlot.getName() + currentSlot.getContents());
        }
        return output.toString();
    }

    public int getContentCount(String slotName) throws NoSuchElementException{
        if (this.allSlots.containsKey(slotName)){
            return this.allSlots.get(slotName).getCount();
        }

        throw new NoSuchElementException("Can not find a slot " + slotName);
    }

}
