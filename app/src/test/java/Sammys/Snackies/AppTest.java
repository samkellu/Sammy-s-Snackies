/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class AppTest {

    ArrayList<String> generateInput(String input){
        String[] userInput = input.split(" ");
        ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
        return inputs;
    }

    VendingMachine getVendingMachine() {
        VendingMachine vm = new VendingMachine();
        vm.addSlot("A1", new FoodItem("water", 1.5, Category.DRINK), 10);
        return vm;
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
        vm.readFromFile("saveFile.json");

        String[] inputString1 = {"buy", "cash", "water", "1", "1*$20"};
        ArrayList<String> input1 = new ArrayList<>(Arrays.asList(inputString1));
        assertTrue(App.buyer(input1, vm, new Scanner(System.in)));

        String[] inputString2 = {"buy", "cash", "water", "1", "1*$2031"};
        ArrayList<String> input2 = new ArrayList<>(Arrays.asList(inputString2));
        assertFalse(App.buyer(input2, vm, new Scanner(System.in)));

        String[] inputString3 = {"buy", "cash", "water", "5", "1*20"};
        ArrayList<String> input3 = new ArrayList<>(Arrays.asList(inputString3));
        assertFalse(App.buyer(input3, vm, new Scanner(System.in)), "Should be insufficient waters remaining");


        String[] inputString4 = {"buy", "cash", "water", "5"};
        ArrayList<String> input4 = new ArrayList<>(Arrays.asList(inputString4));
        assertFalse(App.buyer(input4, vm, new Scanner(System.in)), "Not enough inputs");


        String[] inputString5 = {"buy", "cash", "water", "-5", "1*$2"};
        ArrayList<String> input5= new ArrayList<>(Arrays.asList(inputString5));
        assertFalse(App.buyer(input5, vm, new Scanner(System.in)), "Negative product count");

    }

    @Test void checkWriteUser(){

        App.loadLogins("testUserLoginWrite.json");

        String[] inputString = {"signup", "test", "password"};
        ArrayList<String> input = new ArrayList<>(Arrays.asList(inputString));
        assertTrue(App.signupUser(input), "could not sign up test user");

        assertFalse(App.signupUser(input), "Should not be able to sign up a duplicate");

        String[] removeString = {"removeUser", "test"};
        ArrayList<String> remove = new ArrayList<>(Arrays.asList(removeString));
        App.removeUser(remove);

    }

    // Check cash/product stock
    @Test void buyerValid() {
        VendingMachine vm = getVendingMachine();
        assertTrue(App.buyer(generateInput("buy cash water 1 1*$10"), vm, new Scanner(System.in)));
        assertEquals(9, vm.getContentCount("A1"));
    }

    @Test void buyerInvalidProduct() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy card sprite 1"), vm, new Scanner(System.in)));
    }

    @Test void buyerInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy card water -1 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water a 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water 100 1*$10"), vm, new Scanner(System.in)));
    }

    @Test void buyerInvalidCashDenom() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy cash water 1 1*$110"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 1*1000"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 1*cat"), vm, new Scanner(System.in)));
    }

    @Test void buyerInvalidCashDenomAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy cash water 1 a*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 -1*1000"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 0*cat"), vm, new Scanner(System.in)));
    }

    @Test void restockProductValid() {
        VendingMachine vm = getVendingMachine();
        App.restockProduct(generateInput("restock A1 3"), vm);
        assertEquals(13, vm.getContentCount("A1"));
        App.restockProduct(generateInput("restock A1 10"), vm);
        assertEquals(13, vm.getContentCount("A1"));
    }

    @Test void restockProductInvalidSLot() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.restockProduct(generateInput("restock A2 3"), vm));
    }

    @Test void restockProductInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.restockProduct(generateInput("restock A1 -1"), vm));
        assertFalse(App.restockProduct(generateInput("restock A1 a"), vm));
        assertFalse(App.restockProduct(generateInput("restock A1 20"), vm));
    }

    @Test void addProductValid() {
        VendingMachine vm = getVendingMachine();
        assertTrue(App.addProduct(generateInput("add A2 sprite $1.00 drink 11"), vm));
        assertEquals(11, vm.getContentCount("A2"));
        assertEquals("sprite", vm.getSlots().get("A2").getContents().getName());
        assertEquals(1, vm.getSlots().get("A2").getContents().getPrice());
    }

    @Test void addProductInvalidName() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 $1.00 drink 11"), vm));
    }

    @Test void addProductInvalidPrice() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 Sprite cat drink 11"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite -1 drink 11"), vm));
    }

    @Test void addProductInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink 100"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink cat"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink -1"), vm));
    }

    @Test void removeProductValid() {}

    @Test void removeProductInvalidProduct() {}

    @Test void addUserValid() {}

    @Test void addUserInvalidUserExists() {}

    @Test void addUserInvalidUserName() {}

    @Test void signUpValid() {}

    @Test void signUpInvalidUserExists() {}

    @Test void signUpInvalidUserName() {}

    @Test void removeUserValid() {}

    @Test void removeUserinvalid() {}

    @Test void setCategoryValid() {}

    @Test void setCategoryInvalid() {}
    
    @Test void modify() {} // fill this in as reasonable

    @Test void cashAddValid() {}

    @Test void cashAddInvalidAmount() {}

    @Test void cashAddInvalidDenom() {}

    @Test void cashremoveValid() {}

    @Test void cashremoveInvalidAmount() {}

    @Test void cashremoveValidDenom() {}
  
    @Test void addProductPositiveTest1() {
        String[] s = {"productadd", "Z1", "ZooperDooper", "$2.00", "candy", "5"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    @Test void addProductPositiveTest2() {
        String[] s = {"productadd", "W1", "WagonWheels", "1", "candy", "1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    @Test void addProductTestCaseInsensitivity() {
        String[] s = {"ProductAdd", "X1", "XanderRoot", "1", "Candy", "1"};
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
