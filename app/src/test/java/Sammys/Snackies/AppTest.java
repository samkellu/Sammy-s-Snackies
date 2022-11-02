/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

class AppTest {
    @Test void appHasAGreeting() {
        // App classUnderTest = new App();
        assertNotNull(1,"nice");
    }

    @Test void checkCardVerificationValid() {
        assertTrue(App.verifyCard(1010101010101010L, "12/22", 333));
    }

    @Test void checkCardVerificationLarge() {
        assertTrue(App.verifyCard(9999999999999999L, "12/22", 333));
    }

    @Test void checkCardVerificationInvalidCardUnder() {
        assertFalse(App.verifyCard(999999999999999L, "12/22", 333));
    }

    @Test void checkCardVerificationInvalidCardOver() {
        assertFalse(App.verifyCard(99999999999999999L, "12/22", 333));
    }

    @Test void checkCardVerificationInvalidCVCUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "12/22", 33));
    }

    @Test void checkCardVerificationInvalidCVCOver() {
        assertFalse(App.verifyCard(1010101010101010L, "12/22", 33123));
    }

    @Test void checkCardVerificationInvalidCVC4() {
        assertTrue(App.verifyCard(1010101010101010L, "12/22", 3312));
    }

    @Test void checkCardVerificationInvalidDateUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "12/21", 332));
    }

    @Test void checkCardVerificationInvalidDateBadMonthUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "-2/21", 332));
    }

    @Test void checkCardVerificationInvalidDateBadMonthOver() {
        assertFalse(App.verifyCard(1010101010101010L, "13/21", 332));
    }

    @Test void checkCardVerificationInvalidDateBadYearUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "11/-1", 332));
    }

    @Test void checkCardVerificationInvalidDateBadYearOverLen() {
        assertFalse(App.verifyCard(1010101010101010L, "11/100", 332));
    }

    @Test void checkCardVerificationInvalidDateBadMonthOverLen() {
        assertFalse(App.verifyCard(1010101010101010L, "111/50", 332));
    }

    @Test void checkCardVerificationValidDateMonthLen1() {
        assertTrue(App.verifyCard(1010101010101010L, "1/50", 332));
    }

    @Test void checkBuyCashTest(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("testRead.json");

        String[] inputString1 = {"buy", "cash", "water", "1", "1*$20"};
        ArrayList<String> input1 = new ArrayList<>(Arrays.asList(inputString1));
        assertTrue(App.buyer(input1, vm));

        String[] inputString2 = {"buy", "cash", "water", "1", "1*$2031"};
        ArrayList<String> input2 = new ArrayList<>(Arrays.asList(inputString2));
        assertFalse(App.buyer(input2, vm));

        String[] inputString3 = {"buy", "cash", "water", "5", "1*20"};
        ArrayList<String> input3 = new ArrayList<>(Arrays.asList(inputString3));
        assertFalse(App.buyer(input3, vm), "Should be insufficient waters remaining");


        String[] inputString4 = {"buy", "cash", "water", "5"};
        ArrayList<String> input4 = new ArrayList<>(Arrays.asList(inputString4));
        assertFalse(App.buyer(input4, vm), "Not enough inputs");


        String[] inputString5 = {"buy", "cash", "water", "-5", "1*$2"};
        ArrayList<String> input5= new ArrayList<>(Arrays.asList(inputString5));
        assertFalse(App.buyer(input5, vm), "Negative product count");

    }

    @Test void addProductPositiveTest1() {
        String[] s = {"productadd", "Z1", "ZooperDooper", "$2.00", "candy", "5"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    @Test void addProductPositiveTest2() {
        String[] s = {"productadd", "W1", "WagonWheels", "$1.00", "candy", "1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    @Test void addProductNegativeTest1() {
        String[] s = {"productadd", "Z1", "ZooperDooper", "-2.00", "candy", "5"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct((ArrayList<String>)inputs, vm);
        assertFalse(result);
    }
    
    @Test void addProductNegativeTest2() {
        String[] s = {"productadd", "W1", "WagonWheels", "1", "candy", "-1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertFalse(result);
    }

    @Test void addProductNegativeTest3() {
        String[] s = {"productadd", "X1", "XanderRoot", "1", "candy", "0"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertFalse(result);
    }
}
