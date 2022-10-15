package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

class SlotTest {
    @Test void removeSnack() {
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);

        Slot marsSlot = new Slot("A1", marsBar, 2);
        assertEquals(marsBar, marsSlot.removeItem(), "Failed to remove 1st item");
        assertEquals(marsBar, marsSlot.removeItem(), "Failed to remove 2nd item");
        try {
            marsSlot.removeItem();
            assertEquals(false, true, "Did not throw error when removing item despite no item remaining");
        } catch (NoSuchElementException e){
            
        } catch (Exception e){
            assertEquals(false, true, "Incorrect exception was thrown");
        }
    }

    @Test void writeToFile() {

        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        FoodItem sprite = new FoodItem("Sprite", 2.0f, Category.DRINK);
        FoodItem doritos = new FoodItem("Doritos", 221.0f, Category.CHIPS);

        VendingMachine vm = new VendingMachine();

        vm.addSlot("A1", marsBar, 9);
        vm.addSlot("A2", sprite, 200);
        vm.addSlot("A3", doritos, 55);

        try {

            vm.writeToFile("testWrite.json");
        } catch (Exception e) {
            assertEquals(false, true, "Failed to write");
        }
        assertEquals(true, true, "Written successfully");
    }

    @Test void readFromFile() {

        VendingMachine vm = new VendingMachine();
        assertEquals("", vm.toString());

        vm.readFromFile("testRead.json");
        assertEquals("A1 Mars Bar $1.02\nA2 Sprite $2.00\nA3 Doritos $221.00\n", vm.toString());
    }
}
