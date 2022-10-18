package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

class slotTest {
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
}
