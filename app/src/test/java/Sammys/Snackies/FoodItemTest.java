package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class FoodItemTest {
    @Test void canGetName() {
        FoodItem apple = new FoodItem("Apple", 4.0f, Category.CANDY);
        assertEquals("Apple", apple.getName());
    }

    @Test void canGetProperRepresentation(){
        FoodItem orange = new FoodItem("Orange", 1.3829f, Category.CANDY);
        assertEquals(orange.toString(), "Orange $1.38");
    }
}
