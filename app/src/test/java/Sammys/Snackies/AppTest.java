/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

class AppTest {

    // generates array list from one string input
    ArrayList<String> generateInput(String input){
        String[] userInput = input.split(" ");
        ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
        return inputs;
    }

    // returns a premade stocked standard vending machine for testing
    VendingMachine getVendingMachine() {
        VendingMachine vm = new VendingMachine();
        vm.addSlot("A1", new FoodItem("water", 1.5, Category.DRINK), 10);
        return vm;
    }

    // ensures the verification of credit cards works for correctly formatted input
    @Test void checkCardVerificationValid() {
        assertTrue(App.verifyCard(1010101010101010L, "12/22", 333));
    }

    // positive credit card verification test with the largest number
    @Test void checkCardVerificationLarge() {
        assertTrue(App.verifyCard(9999999999999999L, "12/22", 333));
    }

    // negative credit card verification test, card number length is too short
    @Test void checkCardVerificationInvalidCardUnder() {
        assertFalse(App.verifyCard(999999999999999L, "12/22", 333));
    }

    // negative credit card verifictaion test, card number length is too long
    @Test void checkCardVerificationInvalidCardOver() {
        assertFalse(App.verifyCard(99999999999999999L, "12/22", 333));
    }

    // negative credit card verification test, cvc length too short
    @Test void checkCardVerificationInvalidCVCUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "12/22", 33));
    }

    // negative credit card verification test, cvc length too long
    @Test void checkCardVerificationInvalidCVCOver() {
        assertFalse(App.verifyCard(1010101010101010L, "12/22", 33123));
    }

    // positive credit card verification test, cvc length of 4
    @Test void checkCardVerificationValidCVC4() {
        assertTrue(App.verifyCard(1010101010101010L, "12/22", 3312));
    }

    // negative credit card verification test, card out of date
    @Test void checkCardVerificationInvalidDateUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "12/21", 332));
    }

    // negative credit card verification test, bad date format, negative month
    @Test void checkCardVerificationInvalidDateBadMonthUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "-2/21", 332));
    }

    // negative credit card verification test, bad date format, month > 12
    @Test void checkCardVerificationInvalidDateBadMonthOver() {
        assertFalse(App.verifyCard(1010101010101010L, "13/21", 332));
    }

    // negative credit card verification test, bad date format, negative year
    @Test void checkCardVerificationInvalidDateBadYearUnder() {
        assertFalse(App.verifyCard(1010101010101010L, "11/-1", 332));
    }

    // negative credit card verification test, bad date format, year too long
    @Test void checkCardVerificationInvalidDateBadYearOverLen() {
        assertFalse(App.verifyCard(1010101010101010L, "11/100", 332));
    }

    // negative credit card verification test, bad date format, month too long
    @Test void checkCardVerificationInvalidDateBadMonthOverLen() {
        assertFalse(App.verifyCard(1010101010101010L, "111/50", 332));
    }

    // positive credit card verification test, month length < 2
    @Test void checkCardVerificationValidDateMonthLen1() {
        assertTrue(App.verifyCard(1010101010101010L, "1/50", 332));
    }

    // big buy tests: 
    //  good input
    //  bad denomination
    //  buying more product than is stocked
    //  not giving cash with cash payment type
    //  purchasing negative amounts
    @Test void checkBuyCashTest(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("src/test/resources/testSaveFile.json");

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

    // positive and negative sign up test
    // ensure sign up ability
    // ensure no duplicate users
    @Test void checkWriteUser(){

        App.loadLogins("src/test/resources/testUserLoginWrite.json");

        String[] inputString = {"signup", "test", "password"};
        ArrayList<String> input = new ArrayList<>(Arrays.asList(inputString));
        assertTrue(App.signupUser(input), "could not sign up test user");

        assertFalse(App.signupUser(input), "Should not be able to sign up a duplicate");

        String[] removeString = {"removeUser", "test"};
        ArrayList<String> remove = new ArrayList<>(Arrays.asList(removeString));
        App.removeUser(remove);

    }

    // checks that cash check will output the cash in the vending machine
    @Test void checkCashCheck(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        try{
            App.cashCheck(vm);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        } 
    }
    
    // positive cash add test 
    @Test void checkCashaddValid(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCounts = vm.getCurrencyCounts().get("$5");
        String[] inputArr = {"Add", "3*$5"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCounts + 3, currencyCountsAfter.get("$5"));
    }

    // negative cash add test, negative add amount
    @Test void checkCashaddNeg(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCounts = vm.getCurrencyCounts().get("$5");
        String[] inputArr = {"Add", "-3*$5"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCounts, currencyCountsAfter.get("$5"));
    }

    // positive cash add test, tries a large number of notes to add
    @Test void checkCashaddbig(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCounts = vm.getCurrencyCounts().get("$5");
        String[] inputArr = {"Add", "500000*$5"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCounts + 500000, currencyCountsAfter.get("$5"));
    }

    // negative cash add test, bad format, no amount given
    @Test void checkCashaddinvalidSyntax(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCounts = vm.getCurrencyCounts().get("$5");
        String[] inputArr = {"Add", "$5"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCounts, currencyCountsAfter.get("$5"));
    }

    // positive cash add test, ensures multiple denominations at the same time
    @Test void checkCashaddValidMultiple(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCountsfives = vm.getCurrencyCounts().get("$5");
        int currencyCountstens = vm.getCurrencyCounts().get("$10");
        String[] inputArr = {"Add", "3*$5", "4*$10"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCountsfives + 3, currencyCountsAfter.get("$5"));
        assertEquals(currencyCountstens + 4, currencyCountsAfter.get("$10"));
    }

    // negative cash add test, no input given
    @Test void checkCashaddnoinput(){
        VendingMachine vm = new VendingMachine();
        vm.readFromFile("saveFile.json");
        int currencyCounts = vm.getCurrencyCounts().get("$5");
        String[] inputArr = {"Add"};
        ArrayList<String> inputString = new ArrayList<String>();
        for(String str : inputArr){
            inputString.add(str);
        }
        try{
            App.cashAdd(vm,inputString);
            assertTrue(true);
        }
        catch(Exception e){
            assertTrue(false);
        }
        HashMap<String, Integer> currencyCountsAfter = vm.getCurrencyCounts();
        
        assertEquals(currencyCounts, currencyCountsAfter.get("$5"));
    }

    

    // positive buying test case
    @Test void buyerValid() {
        VendingMachine vm = getVendingMachine();
        assertTrue(App.buyer(generateInput("buy cash water 1 1*$10"), vm, new Scanner(System.in)));
        assertEquals(9, vm.getContentCount("A1"));
    }

    // negative buying test case, product not in machine
    @Test void buyerInvalidProduct() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy card sprite 1"), vm, new Scanner(System.in)));
    }

    // negative buying test case:
    //  negative amount
    //  amount NaN
    //  no amount given
    //  amount > available
    @Test void buyerInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy card water -1 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water a 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water 1*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy card water 100 1*$10"), vm, new Scanner(System.in)));
    }

    // negative buying test case:
    //  bad denomination
    //  bad denomination
    //  denomination NaN
    @Test void buyerInvalidCashDenom() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy cash water 1 1*$110"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 1*1000"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 1*cat"), vm, new Scanner(System.in)));
    }

    // negative buyer test case:
    //  denomination amount NaN
    //  negative denomination amount
    //  denomination amount 0, denomination NaN
    @Test void buyerInvalidCashDenomAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.buyer(generateInput("buy cash water 1 a*$10"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 -1*1000"), vm, new Scanner(System.in)));
        assertFalse(App.buyer(generateInput("buy cash water 1 0*cat"), vm, new Scanner(System.in)));
    }

    // positive restock test
    @Test void restockProductValid() {
        VendingMachine vm = getVendingMachine();
        App.restockProduct(generateInput("restock A1 3"), vm);
        assertEquals(13, vm.getContentCount("A1"));
        App.restockProduct(generateInput("restock A1 10"), vm);
        assertEquals(13, vm.getContentCount("A1"));
    }

    // negative restock test, slot not in machine
    @Test void restockProductInvalidSLot() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.restockProduct(generateInput("restock A2 3"), vm));
    }

    // negative restock test:
    //  negative restock amount
    //  restock amount NaN
    //  restock amount more than the slot can hold
    @Test void restockProductInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.restockProduct(generateInput("restock A1 -1"), vm));
        assertFalse(App.restockProduct(generateInput("restock A1 a"), vm));
        assertFalse(App.restockProduct(generateInput("restock A1 20"), vm));
    }

    // positive add product test
    @Test void addProductValid() {
        VendingMachine vm = getVendingMachine();
        assertTrue(App.addProduct(generateInput("add A2 sprite $1.00 drink 11"), vm));
        assertEquals(11, vm.getContentCount("A2"));
        assertEquals("sprite", vm.getSlots().get("A2").getContents().getName());
        assertEquals(1, vm.getSlots().get("A2").getContents().getPrice());
    }

    // negative add product test, product name doesn't exist
    @Test void addProductInvalidName() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 $1.00 drink 11"), vm));
    }

    // negative add product test:
    //  add amount NaN
    //  negative add amount
    @Test void addProductInvalidPrice() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 Sprite cat drink 11"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite -1 drink 11"), vm));
    }

    // negative add product test:
    //  add amount is more than the slot can fit
    //  add amount NaN
    //  negative add amount
    @Test void addProductInvalidAmount() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink 100"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink cat"), vm));
        assertFalse(App.addProduct(generateInput("add A2 Sprite $1.00 drink -1"), vm));
    }

    // positive remove product test
    @Test void removeProductValid() {
        VendingMachine vm = getVendingMachine();
        assertTrue(App.removeProduct(generateInput("remove A1"), vm));
        assertEquals(0, vm.getSlots().keySet().size());
    }

    // negative remove product test, slot doesn't exist
    @Test void removeProductInvalidSlot() {
        VendingMachine vm = getVendingMachine();
        assertFalse(App.removeProduct(generateInput("remove A2"), vm));
    }

    // positive add user test, one for each user type
    @Test void addUserValid() {
        App.currentType = UserType.OWNER;
        assertTrue(App.addUser(generateInput("add wow user cashier")));
        assertTrue(App.addUser(generateInput("add new1 user owner")));
        assertTrue(App.addUser(generateInput("add new2 user seller")));
        assertTrue(App.addUser(generateInput("add new3 user buyer")));
    }

    // negative add user test:
    //  all ensure no duplicates
    @Test void addUserInvalidUserExists() {
        App.currentType = UserType.OWNER;
        assertTrue(App.addUser(generateInput("add new user buyer")));
        assertFalse(App.addUser(generateInput("add new user owner")));
        assertFalse(App.addUser(generateInput("add new user seller")));
        assertFalse(App.addUser(generateInput("add new user buyer")));
        assertFalse(App.addUser(generateInput("add new user cashier")));
    }

    // negative add user test, bad format:
    //  no user type given
    //  too many args given
    @Test void addUserInvalidFormat() {
        App.currentType = UserType.OWNER;
        assertFalse(App.addUser(generateInput("add new user")));
        assertFalse(App.addUser(generateInput("add new user owner wwwww")));
    }

    // positive sign up test
    @Test void signUpValid() {
        assertTrue(App.signupUser(generateInput("signup jack password")));
    }

    // negative sign up test, ensure no duplicate users
    @Test void signUpInvalidUserExists() {
        assertFalse(App.signupUser(generateInput("signup jack password")));
        assertFalse(App.signupUser(generateInput("signup jack amn")));
    }

    // negative sign up test, bad format:
    // no password given
    // no username or password given
    @Test void signUpInvalidUserName() {
        assertFalse(App.signupUser(generateInput("signup amn")));
        assertFalse(App.signupUser(generateInput("signup")));
    }

    @Test void removeUserValid() {}

    @Test void removeUserinvalid() {}

    @Test void setCategoryValid() {}

    @Test void setCategoryInvalid() {}
    
    @Test void modifySlotNameValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify A1 slot name to new"), vm));
        assertEquals("NEW", vm.getSlots().get("A1").getName());
    }

    @Test void modifySlotNameInvalid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.RED + "\nSlot name doesn't exist." + App.RESET, App.modify(generateInput("modify A2 slot name to new"), vm));
        assertEquals("water", vm.getSlots().get("A1").getContents().getName());
    }

    @Test void modifySlotProductNameValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify A1 product name to new"), vm));
        assertEquals("new", vm.getSlots().get("A1").getContents().getName());
    }

    @Test void modifySlotProductNameInvalid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.RED + "\nSlot name doesn't exist." + App.RESET, App.modify(generateInput("modify A2 product name to new"), vm));
        assertEquals("water", vm.getSlots().get("A1").getContents().getName());
    }

    @Test void modifySlotProductPriceValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify A1 product price to $100.55"), vm));
        assertEquals(100.55, vm.getSlots().get("A1").getContents().getPrice());
    }

    @Test void modifySlotProductPriceInvalid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.RED + "\nPlease ensure new price is a valid price." + App.RESET, App.modify(generateInput("modify A1 product price to -1"), vm));
        assertEquals(App.RED + "\nPlease ensure new price is a valid price." + App.RESET, App.modify(generateInput("modify A1 product price to cat"), vm));
        assertEquals(1.5, vm.getSlots().get("A1").getContents().getPrice());
    }

    @Test void modifySlotProductCategoryValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(Category.DRINK, vm.getSlots().get("A1").getContents().getCategory());
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify A1 product category to candy"), vm));
        assertEquals(Category.CANDY, vm.getSlots().get("A1").getContents().getCategory());
    }

    @Test void modifySlotProductCategoryInvalid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(Category.DRINK, vm.getSlots().get("A1").getContents().getCategory());
        assertEquals(App.RED + "\nPlease ensure the category is a valid category." + App.RESET, App.modify(generateInput("modify A1 product category to notACategory"), vm));
        assertEquals(Category.DRINK, vm.getSlots().get("A1").getContents().getCategory());
    }

    @Test void modifyProductNameValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify water name to notWater"), vm));
        assertEquals("notWater", vm.getSlots().get("A1").getContents().getName());
    }

    @Test void modifyProductPriceValid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.GREEN + "successfully modified product(s)" + App.RESET, App.modify(generateInput("modify water price to $100.55"), vm));
        assertEquals(100.55, vm.getSlots().get("A1").getContents().getPrice());
    }

    @Test void modifyProductPriceInvalid() {
        VendingMachine vm = getVendingMachine();
        assertEquals(App.RED + "\nPlease ensure new price is a valid price." + App.RESET, App.modify(generateInput("modify water price to -1"), vm));
        assertEquals(App.RED + "\nPlease ensure new price is a valid price." + App.RESET, App.modify(generateInput("modify water price to cat"), vm));
        assertEquals(1.5, vm.getSlots().get("A1").getContents().getPrice());
    }

    @Test void cashAddValid() {}

    @Test void cashAddInvalidAmount() {}

    @Test void cashAddInvalidDenom() {}

    @Test void cashremoveValid() {}

    // just run the help command, ensure it doesn't crash
    @Test void runHelpCommand() {
        App.helpCommand(null);
        String[] strs = {
            "help", "help buy", "help user", "help list", "help login", "help restock",
            "help signup", "help product", "help modify", "help products", "help help",
            "help cash", "help buyer", "help exit", "help quit", "help cashcheck","helpcashadd",
            "help cashremove", "help productadd", "help productremove","help userremove", "help useradd",
            "help userlist",
        };
        for (String s : strs) {
            App.helpCommand(generateInput(s));
        }
    }

    // negative cash remove tests:
    //  bad denomination
    //  no amount & bad denomination
    //  negative amount
    //  negative amount, with multiple denominations
    @Test void cashremoveInvalidAmount() {
        VendingMachine vm = new VendingMachine();
        vm.addCurrencyCount("$10", 20);
        assertFalse(App.cashRemove(vm, generateInput("remove 2*$30")));
        assertFalse(App.cashRemove(vm, generateInput("remove $40")));
        assertFalse(App.cashRemove(vm, generateInput("remove -4*$10")));
        assertFalse(App.cashRemove(vm, generateInput("remove 2*$2 -1*$1")));
    }

    // positive and negative cash remove test
    //  remove all of one denomination
    //  try and remove more, should fail
    @Test void cashremoveValidDenom() {
        VendingMachine vm = new VendingMachine();
        vm.addCurrencyCount("$10", 3);
        vm.addCurrencyCount("$20", 2);
        assertTrue(App.cashRemove(vm, generateInput("remove 2*$10")));
        assertFalse(App.cashRemove(vm, generateInput("remove 7*$10")));
    }
  
    // positive add product test, price has $
    @Test void addProductPositiveTest1() {
        String[] s = {"productadd", "Z1", "ZooperDooper", "$2.00", "candy", "5"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    // positive add product test, price does not have $, and is int
    @Test void addProductPositiveTest2() {
        String[] s = {"productadd", "W1", "WagonWheels", "1", "candy", "1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    // positive add product test, checks case insensitivity
    @Test void addProductTestCaseInsensitivity() {
        String[] s = {"ProductAdd", "X1", "XanderRoot", "1", "Candy", "1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertTrue(result);
    }

    // negative add product test, negative price
    @Test void addProductNegativeTest1() {
        String[] s = {"productadd", "Z1", "ZooperDooper", "-2.00", "candy", "5"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct((ArrayList<String>)inputs, vm);
        assertFalse(result);
    }
    
    // negative add product test, negative amount
    @Test void addProductNegativeTest2() {
        String[] s = {"productadd", "W1", "WagonWheels", "1", "candy", "-1"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertFalse(result);
    }

    // negative add product test, zero amount
    @Test void addProductNegativeTest3() {
        String[] s = {"productadd", "X1", "XanderRoot", "1", "candy", "0"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(s));
        VendingMachine vm = new VendingMachine();
        boolean result = App.addProduct(inputs, vm);
        assertFalse(result);
    }

    @Test void listTransactionsTest() {
        VendingMachine vm = new VendingMachine();
        vm.addTransaction("card", new FoodItem("apple", 0.4, Category.CANDY), 4, "test");
        try {
            App.listTransactions(vm, true);
            App.listTransactions(vm, false);
        } catch (Exception e){
            assertTrue(false);
        }
    }
}
