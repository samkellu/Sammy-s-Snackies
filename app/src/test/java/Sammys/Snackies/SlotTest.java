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

    @Test void restockContentsInBounds() {
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        Slot marsSlot = new Slot("A1", marsBar, 2);
        assertEquals(2, marsSlot.getCount());
        marsSlot.restockContents(10);
        assertEquals(12,  marsSlot.getCount());
    }

    @Test void restockContentsNegative() {
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        Slot marsSlot = new Slot("A1", marsBar, 2);
        assertEquals(2, marsSlot.getCount());
        marsSlot.restockContents(-1);
        assertEquals(2,  marsSlot.getCount());
    }

    @Test void restockContentsOverBounds() {
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        Slot marsSlot = new Slot("A1", marsBar, 2);
        assertEquals(2, marsSlot.getCount());
        assertThrows(IndexOutOfBoundsException.class, () -> {
            marsSlot.restockContents(200);
        });
        assertEquals(2,  marsSlot.getCount());
    }

    @Test void toStringCorrectness() {
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        Slot marsSlot = new Slot("A1", marsBar, 2);
        assertEquals("| A1   | Mars Bar    | 2    | $1.02     |", marsSlot.toString());
        assertEquals(2,  marsSlot.getCount());
    }
}
