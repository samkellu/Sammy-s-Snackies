package Sammys.Snackies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.time.*;
import java.util.NoSuchElementException;

public class App {

    // Defining colours for text output
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String MAGENTABG = "\u001B[45;30m";
    public static final String RESET = "\u001B[0m";
    
    // File paths for databases
    private static final String saveFilePath = "saveFile.json";
    private static final String userLoginFilepath = "userLogins.json";
    private static UserType currentType = UserType.BUYER;
    private static ArrayList<UserLogin> userLogins;

    // Prints a given message in a given colour
    private static void printColour(String colour, String str) {
        System.out.println(colour + str + RESET);
    }
    
    // Converts a string representation of a denomination into its double value
    private static double parseDenom(String s) {

        switch (s) {
            case "5c":
                return 0.05;
            case "10c":
                return 0.10;
            case "20c":
                return 0.20;
            case "50c":
                return 0.50;
            case "$1":
                return 1.00;
            case "$2":
                return 2.00;
            case "$5":
                return 5.00;
            case "$10":
                return 10.00;
            case "$20":
                return 20.00;
            case "$50":
                return 50.00;
            case "$100":
                return 100.00;
            default:
                printColour(RED, "Please enter a valid monetary denomination.");
                return -1;
        }
    }

    public static void products(VendingMachine v) {

        int maxSlot = 5;
        int maxName = 5;
        int maxQty = 4;
        int maxPrice = 6;

        // Retrieving the maximum width for each column of the table
        boolean empty = false;
        for (String key : v.getSlots().keySet()) {

            if (!empty) {
                empty = (v.getSlots().get(key).getCount() == 0) ? true : false;
            }
                
            String name = v.getSlots().get(key).getName();
            String qty = String.valueOf(v.getSlots().get(key).getCount());
            String price = String.format("$%.2f", v.getSlots().get(key).getContents().getPrice());

            maxSlot = (key.length() > maxSlot) ? key.length() : maxSlot;
            maxName = (name.length() > maxName) ? name.length() : maxName;
            maxQty = (qty.length() > maxQty) ? qty.length() : maxQty;
            maxPrice = (price.length() > maxPrice) ? price.length() : maxPrice;
        }
        // Custom error message for when the machine is empty
        if (empty) {
            printColour(RED, "Sorry, there are no products available in this machine.");
            return;
        }

        // Initialising and printing substrings for the label row of the table
        String slotSub = String.format(RESET + YELLOW + "%-" + maxSlot + "s" + RESET + GREEN, "SLOT");
        String nameSub = String.format(RESET + YELLOW + "%-" + maxName + "s" + RESET + GREEN, "NAME");
        String qtySub = String.format(RESET + YELLOW + "%-" + maxQty + "s" + RESET + GREEN, "QTY");
        String priceSub = String.format(RESET + YELLOW + "%-" + maxPrice + "s" + RESET + GREEN, "PRICE");

        printColour(YELLOW, "Products available:\n");
        printColour(GREEN, String.format("    | %s | %s | %s | %s |", slotSub, nameSub, qtySub, priceSub));

        // Initialising and printing spacing row of the table
        String slotSpace = String.format("%-" + maxSlot + "s", "").replace(' ', '-');
        String nameSpace = String.format("%-" + maxName + "s", "").replace(' ', '-');
        String qtySpace = String.format("%-" + maxQty + "s", "").replace(' ', '-');
        String priceSpace = String.format("%-" + maxPrice + "s", "").replace(' ', '-');
        printColour(GREEN, "    |-" + slotSpace + "-+-" + nameSpace + "-+-" + qtySpace + "-+-" + priceSpace + "-|");

        // Prints all formatted rows of the table
        for (String key : v.getSlots().keySet()) {

            String name = v.getSlots().get(key).getName();
            String qty = String.valueOf(v.getSlots().get(key).getCount());
            String price = String.format("$%.2f", v.getSlots().get(key).getContents().getPrice());
            printColour(GREEN, String.format("    | %-" + maxSlot + "s | %-" + maxName + "s | %-" + maxQty + "s | %-" + maxPrice + "s |", key, name, qty, price));
        }
    }

    // Outputs a standard card parsing error
    public static void printCardError() {
        printColour(RED, "Invalid card details entered, please try again or type \"quit\" to cancel this transaction.\nCard details must be of the form:\n");
        printColour(GREEN, "  CARD NUMBER (16) MM/YY CVC\n  **************** **/** ***");
        System.out.print(MAGENTABG + currentType.toString().toUpperCase() + RESET + " > ");
    }

    // Processes the sale of items
    public static boolean buyer(ArrayList<String> inputs, VendingMachine vm) {

        // Ensure enough arguments
        if (inputs.size() < 4) {
            printColour(RED, "Not enough arguments.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    buy <cash/card> <product> <amount> [denominations...]");
            return false;
        }
        // Check cash or card
        boolean cash = false;
        if (inputs.get(1).toLowerCase().equals("cash")) {
            cash = true;

            // Ensure enough arguments for cash payment
            if (inputs.size() < 5) {
                printColour(RED, "Not enough arguments.");
                printColour(YELLOW, "Usage:");
                printColour(GREEN, "    buy <cash/card> <product> <amount> [denominations...]");
                return false;
            }
        // Gives error if the payment method is neither card nor cash
        } else if (!inputs.get(1).toLowerCase().equals("card")) {
            printColour(RED, "Please specify payment type (cash or card).");
            return false;
        }

        Slot slot = null;
        // Finds the slot in the machine that holds the desired item
        for (Slot s : vm.getSlots().values()) {
            if (inputs.get(2).toLowerCase().equals(s.getContents().getName().toLowerCase())) {
                slot = s;
                break;
            }
        }

        // Checks that the required slot exists
        if (slot == null) {
            printColour(RED, "Please enter a valid product code. This machine contains no item with code: " + inputs.get(2));
            return false;
        }

        int productAmt = -1;
        // Check that the inputted amount is a valid integer
        try {
            productAmt = Integer.parseInt(inputs.get(3));
        } catch (NumberFormatException e) {
            printColour(RED, "\nPlease ensure the product amount is a positive integer.");
            return false;
        }
        
        // Check the machine has sufficient quantity of the item
        if (productAmt <= 0) {
            printColour(RED, "\nPlease ensure the product amount is a positive integer.");
            return false;
        }

        // Check that the machine has enough of the item to satisfy the order
        if (slot.getCount() < productAmt) {
            if (slot.getCount() == 0){
                printColour(RED, "Unfortunately, this machine is all out of stock of " + slot.getContents().getName());
            } else {
                printColour(RED, "Unfortunately, this machine only has " + slot.getCount() + "x " + slot.getContents().toString() + " available.");
            }
            return false;
        }

        // Calculates the cost of the order
        double price = -1;
        for (String s : vm.getSlots().keySet()) {
            if (vm.getSlots().get(s).getContents().getName().equals(inputs.get(2))) {
                price = vm.getSlots().get(s).getContents().getPrice();
            }
        }
        if (price == -1) {
            printColour(RED, "\nSorry an internal error occured, please try again.");
            return false;
        }
        
        double totalGiven = 0.00;
        String changeString = "";
        double totalCost = productAmt*price;

        // Handle a cash transaction
        if (cash) {

            ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(4, inputs.size()));
            HashMap<String, Integer> givenDenominations = new HashMap<>();
            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};

            // Check all inputted denominations for validity
            for (String s : inputDenoms) {

                String[] values = s.split("\\*");
                ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
                
                if (values.length != 2 || !(denomSet.contains(values[1]))) {
                    printColour(RED, "Unrecognisable denomination.\nPlease use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                    return false;
                }
                
                String v = values[1];                
                String amount = values[0];
                int amt = -1;
                
                try {
                    amt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    printColour(RED, "\nPlease ensure the cash amount is a positive integer.");
                    return false;
                } 
                
                if (amt <= 0) {
                    printColour(RED, "\nPlease ensure the amount is a positive integer.");
                    return false;
                }
                

                double value = parseDenom(v);
                if (value == -1) return false;
                
                // Calculate the total cash given
                givenDenominations.put(v, amt);
                totalGiven += value*amt;
            }

            HashMap<String, Integer> changeToGive;
            // Checks if there is enough change in the machine to satisfy the transaction
            try {
                changeToGive = vm.getChangeFromCash(totalGiven-totalCost);
            } catch (IndexOutOfBoundsException e) {
                printColour(RED, "\nSorry, we don't have the change to give you.\nReturning money...");
                return false;
            }

            // Gives change
            boolean givingChange = false;
            changeString = "Change given: ";
            for (String value : currencyValues) {
                if (changeToGive.containsKey(value))
                if (changeToGive.get(value) > 0) {
                    givingChange = true;
                    changeString += String.format("%dx%s, ", changeToGive.get(value), value);
                }
            }
            changeString += "\n";

            if (!givingChange) {
                changeString = "Correct change given, no change to give\n\n";
            }

        // Processes a card transaction
        } else {

            boolean attempting = true;
            Scanner s = new Scanner(System.in);
            String input = new String();

            // Gives card details prompt
            printColour(YELLOW, "Please enter your card details, or type \"quit\" to cancel this transaction.\nCard details must be of the form:\n");
            printColour(GREEN, "  CARD NUMBER (16) MM/YY CVC\n  **************** **/** ***");
            System.out.print(MAGENTABG + currentType.toString().toUpperCase() + RESET + " > ");

            // Repeatedly gets card details from the user until a card is accepted, or the user quits
            while (attempting) {
                input = s.nextLine();
                System.out.println();
                // Cancels a transaction
                if (input.toLowerCase().equals("quit")) {
                    printColour(GREEN, "Transaction cancelled.");
                    s.close();
                    return false;
                }

                String sepInput[] = input.split(" ");

                if (sepInput.length != 3) {
                    printCardError();
                    continue;
                }

                // Verifies the card
                try {
                    if (!verifyCard(Long.parseLong(sepInput[0]), sepInput[1], Integer.parseInt(sepInput[2]))) {
                        printCardError();
                        continue;
                    } else {
                        printColour(GREEN, "Card is valid!");
                        attempting = false;
                        break;
                    }
                } catch (Exception e) {
                    printCardError();
                    continue;
                }
            }
            s.close();
        }

        // Print receipt
        printColour(GREEN, String.format("You recieved %sx %s.", inputs.get(3), slot.getContents().toString()));
        printColour(GREEN, String.format("You paid $%.2f.", totalCost));
        printColour(GREEN, changeString);
        printColour(YELLOW, "Thank you for shopping at Sammy's Snackies!");

        // Removes purchased items from the machine
        slot.sellContents(Integer.valueOf(inputs.get(3)));
        // Adds the transaction to the database
        vm.addTransaction(inputs.get(1).toLowerCase(), slot.getContents(), Integer.valueOf(inputs.get(3)));
        return true;
    }
  
    // Verifies whether a card is valid or not
    public static boolean verifyCard(long cardNumber, String date, int cvc) {

        // Checks the date is of the form mm/yy or m/yy
        String dateArr[] = date.split("/");
        if (dateArr.length != 2 || dateArr[0].length() > 2 || dateArr[1].length() > 2 || dateArr[0].length() <= 0) {
            return false;
        }
            
        LocalDate localDate = LocalDate.now();
        try {
            // Attempts to format the given date
            date = String.format("20%d-%02d-01",Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]));
            // Checks that the expiry date on the card is not in the past, and that the other components are of the correct length
            if (String.valueOf(cardNumber).length() == 16 && (String.valueOf(cvc).length() == 3 || String.valueOf(cvc).length() == 4) && localDate.isBefore(LocalDate.parse(date))) {
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    // Restock a given product in the machine
    public static boolean restockProduct(ArrayList<String> inputs, VendingMachine vm){

        // Check number of inputs
        if (inputs.size() != 3){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    restock <slot name> <restock count>");
            return false;
        }

        Slot slot = null;
        // Checks all slots in the machine for a matching product code
        for (String s : vm.getSlots().keySet()) {
            if (s.equals(inputs.get(1).toUpperCase())){
                slot = vm.getSlots().get(s);
                break;
            }
        }

        // Checks that the product exists in the machine
        if(slot==null){
            printColour(RED, "Could not find slot " + inputs.get(1));
            return false;
        }

        // Checks that the inputted amount is a valid integer
        int restockCount;
        try {
            restockCount = Integer.parseInt(inputs.get(2));

            if (restockCount < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            printColour(RED, "Please enter a valid integer for restock count");
            return false;
        }

        // Attempts to restock the product
        try{
            slot.restockContents(restockCount);
        } catch (IndexOutOfBoundsException e){
            printColour(RED, e.getMessage());
            return false;
        }

        printColour(GREEN, "Successfully restocked " + inputs.get(2) +" "+slot.getContents().getName()+"'s, new stock count is " + Integer.toString(slot.getCount()) + " with a value of $" +String.format("%.2f", slot.getCount()*slot.getContents().getPrice()));
        return true;
    }

    // Adds a new product to the vending machine
    public static boolean addProduct(ArrayList<String> inputs, VendingMachine vm){

        // Checks number of inputs
        if (inputs.size() != 6){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    product add <slot name> <product name> <product price> <product category> <product stock>");
                
            return false;
        }

        // Check if the given slot exists/ is empty
        String slotName = inputs.get(1).toUpperCase();
        Slot currentSlot = null;
        for (String name : vm.getSlots().keySet()){
            if (name.equals(slotName)){
                currentSlot = vm.getSlots().get(name);
                if (currentSlot.getCount()!=0){
                    printColour(RED, "Slot already exists and is non empty! Please choose an empty slot or try \"restock\"");
                    return false;
                }
            }
        }
        // Checks that the given category is valid
        Category foodCategory = null;
        switch (inputs.get(4).toLowerCase()){
            case "drink":
                foodCategory = Category.DRINK;
            break;
            case "chocolate":
                foodCategory = Category.CHOCOLATE;
            break;
            case "chips":
                foodCategory = Category.CHIPS;
            break;
            case "candy":
                foodCategory = Category.CANDY;
            break;
            default:
                printColour(RED, "Category not found! Please choose one of the following categories:");
                printColour(GREEN, "    CHOCOLATE | CANDY | CHIPS | DRINK");
                return false;
        }

        try{
            // Retrieves price
            String priceStr = inputs.get(3);
            if (priceStr.charAt(0) == '$'){
                priceStr = priceStr.substring(1);
            }
            Double price = Double.parseDouble(priceStr);
    
            // Creates new footItem
            FoodItem newFood = new FoodItem(inputs.get(2).toLowerCase(), price, foodCategory);
            if (currentSlot != null){
                vm.getSlots().remove(currentSlot.getName());
            }

            // Creates new slot
            currentSlot = new Slot(slotName, newFood,Integer.parseInt(inputs.get(5)));
            if (currentSlot.getCount() > 15){
                printColour(RED, "Slots can only hold up to 15 items! Please try again");
            }

            // Adds new slot to the machine
            vm.getSlots().put(slotName, currentSlot);
            printColour(GREEN, "Added " + currentSlot.getContents().getName() + " to slot " + currentSlot.getName() + " at a price of $" + String.format("%.2f", newFood.getPrice()));
        } catch (NumberFormatException e){
            printColour(RED, "Please use a decimal number for price and an integer for food item count");
            return false;
        }
        return true;
    }

    // Removes a product from the machine
    public static boolean removeProduct(ArrayList<String> inputs, VendingMachine vm){

        // Checks input size
        if (inputs.size() != 2){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    product remove <slot name>");
            return false;
        }

        // Checks that the given product exists in a slot in the machine
        Slot slot = null;
        for (String s : vm.getSlots().keySet()){
            if (s.equals(inputs.get(1).toUpperCase())){
                slot = vm.getSlots().get(s);
                break;
            }
        }
        if (slot == null){
            printColour(RED, "Slot not found, please try a valid slot name.");
            printColour(GREEN, "Try use \"products\" to get a list of all available products and slots!");
            return false;
        }
        // Removes the given amount of product from the slot
        printColour(GREEN, "Removing product " + slot.getContents().getName() + " from slot " + inputs.get(1).toUpperCase() + " for a total value of $" + String.format("%.2f", slot.getContents().getPrice()*slot.getCount()));
        vm.getSlots().remove(slot.getName());
        return true;
    }

    // Adds a user of a given type to the system
    public static boolean addUser(ArrayList<String> inputs) {
        
        // Ensures that the user is logged in as an owner
        if (currentType != UserType.OWNER){
            printColour(RED, "You are unauthorised!! Owner role is required, please log in.");
            return false;
        }

        // Checks input size
        if (inputs.size() != 4){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage: ");
            printColour(GREEN, "    user add <username> <password> <user type>");
            return false;
        }

        // Checks if a user already exists with the given username
        String username = inputs.get(1);
        for (UserLogin user : userLogins){
            if (user.getUsername().equals(username)){
                printColour(RED, "User already exists, please choose a unique username");
                return false;
            }
        }

        // Creates user and adds them to the system
        String password = inputs.get(2);
        UserType type = UserType.fromName(inputs.get(3).toLowerCase());
        UserLogin user = new UserLogin(username, password, type);
        userLogins.add(user);
        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
        printColour(GREEN, "New user added with username " + username + " with role of " + type);
        return true;
    }

    // Logs the user into the given account
    public static boolean userLogin(ArrayList<String> inputs) {

        // Checks input size
        if (inputs.size() != 3) {
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    login <username> <password>");
            return false;
        }

        // Checks that the given login  exists
        for (UserLogin user : userLogins){
            if (user.verifyLogin(inputs.get(1), inputs.get(2))){
                printColour(GREEN, "Welcome, " + user.getUsername());
                // Logs the user in
                currentType = user.getType();
                printColour(YELLOW, "You are now logged in as a " + user.getType());
                return true;
            }
        }
        printColour(RED, "Login not found, try again");
        return false;
    }

    // Removes a user from the system
    public static boolean removeUser(ArrayList<String> inputs){

        // Ensures that the user is logged in as an owner
        if (currentType != UserType.OWNER){
            printColour(RED, "You are unauthorised!! Owner role is required, please log in.");
            return false;
        }

        // Checks input size
        if (inputs.size() != 2){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage: ");
            printColour(GREEN, "    user remove <username>");
            return false;
        }

        // Finds the user and removes them
        String username = inputs.get(1);
        boolean isFound = false;
        for (int i = 0; i < userLogins.size(); i++){
            if (userLogins.get(i).getUsername().equals(username)){
                userLogins.remove(i);
                printColour(GREEN, "Removed user " + username);
                isFound = true;
                break;
            }
        }
        if (!isFound){
            printColour(RED, "User not found, please choose another username");
            return false;
        }
        // Writes user to file
        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
        return true;
    }

    // Attempts to convert a price string to a double
    private static double checkPrice(String price) {

        double ret;
        try { 
            ret = Double.parseDouble(price); 
        }
        catch (NumberFormatException e) { 
            return -1; 
        }
        return ret;
    }

    // Sets the category of a slot
    public static void setCategory(Slot s, String category) {
        switch (category) {
            case "drink":
                s.getContents().setCategory(Category.DRINK);
                return;
            case "chocolate":
                s.getContents().setCategory(Category.CHOCOLATE);
                return;
            case "chips":
                s.getContents().setCategory(Category.CHIPS);
                return;
            case "candy":
                s.getContents().setCategory(Category.CANDY);
                return;
        }
    }

    // Allows the user to modify a slot or product
    public static String modify(ArrayList<String> inputs, VendingMachine vm) {

        List<String> categories = Arrays.asList("drink","chocolate","chips","candy");
        String success = "successfully modified product(s)";

        /*
        To change a slot's name:
        modify <old_slot_name> slot name to <new_slot_name>
        Note: this name must be unique in the vending machine

        To change just one slot's product's details:
        modify <slot_name> product name to <new_product_name>
        modify <slot_name> product price to <new_price>
        modify <slot_name> product category to <new_category>

        To change all product's details that have the same name:
        modify <product_name> name to <new_product_name>
        modify <product_name> price to <new_price>
        modify <product_name> category to <new_category>
        */

        // ensure correct permissions
        // if (currentType != UserType.SELLER)
        //     return RED + "You are unauthorised!! Seller role is required, please log in." + RESET;

        // ensure correct number of arguments

        if (inputs.size() < 5 || inputs.size() > 6)
            return RED + "\nIncorrect paramaters! Use \"help modify\" to recieve help!" + RESET;

        String input1 = inputs.get(1).toUpperCase();
        String input2 = inputs.get(2).toLowerCase();
        String input3 = inputs.get(3).toLowerCase();
        String input4 = inputs.get(4).toLowerCase();
        String input5 = inputs.size() == 6 ? inputs.get(5).toLowerCase() : "";

        // check to search for product name or slot name
        boolean referencedBySlot = false;
        if (input2.equals("slot") || input2.equals("product")) {
            referencedBySlot = true;
            if (!vm.getSlots().containsKey(input1))
                return RED + "\nSlot name doesn't exist." + RESET;
        }

        // slot referenced, changing product details
        if (referencedBySlot && input2.equals("product") && input4.equals("to")) {
            Slot s = vm.getSlots().get(input1);
            switch (input3) {
                case "name":
                    s.getContents().setName(inputs.get(5));
                    return success;
                case "price":
                    double price = checkPrice(input5.replace("$", ""));
                    if (price < 0.05)
                        return RED + "\nPlease ensure new price is a valid price." + RESET;
                    s.getContents().setPrice(price);
                    return success;
                case "category":
                    if (!categories.contains(input5))
                        return RED + "\nPlease ensure the category is a valid category." + RESET;
                    setCategory(s, input5);
                    return success;
                default:
                    return RED + "\nIncorrect paramaters! Use \"help modify\" to recieve help!" + RESET;    
            }
        }

        // slot referenced, changing slot name
        if (referencedBySlot && input2.equals("slot") && input3.equals("name") && input4.equals("to")) {
            Slot s = vm.getSlots().get(input1);
            if (vm.getSlots().containsKey(input5.toUpperCase()))
                return RED + "\nSlot already exists. Please give this slot a new name." + RESET;
            s.setName(input5.toUpperCase());
            return success;
        }

        // product referenced, changing all products
        if (!referencedBySlot && input3.equals("to")) {
            
            // get the slots containing the product referenced
            List<Slot> slots = new ArrayList<>();
            for (String key : vm.getSlots().keySet()) {
                Slot s = vm.getSlots().get(key);
                if (s.getContents().getName().toUpperCase().equals(input1))
                    slots.add(s);
            }

            if (input2.equals("name")) {
                slots.forEach((Slot s) -> { s.getContents().setName(inputs.get(4)); });
            } 
            else if (input2.equals("price")) {

                double price = checkPrice(input4.replace("$", ""));
                if (price < 0.05)
                    return RED + "\nPlease ensure new price is a valid price." + RESET;

                slots.forEach((Slot s) -> { s.getContents().setPrice(price); });
            } 
            else if (input2.equals("category")) {

                if (!categories.contains(input5))
                    return RED + "\nPlease ensure the category is a valid category." + RESET;
                slots.forEach((Slot s) -> { setCategory(s, input5); });
            } 
            else {
                return RED + "\nIncorrect paramaters! Use \"help modify\" to recieve help!" + RESET;
            }

            return success;
        }

        return RED + "\nIncorrect paramaters! Use \"help modify\" to recieve help!" + RESET;
    }

    public static void userList(ArrayList<String> inputs){

        // Checks input size
        if (inputs.size() != 1){
            printColour(RED, "This command takes no user input!");
        }

        int maxUsername = 9;
        int maxPassword = 9;
        int maxType = 5;
        for (UserLogin userLogin : userLogins){
            maxUsername = (userLogin.getUsername().length() > maxUsername) ? userLogin.getUsername().length() : maxUsername;
            maxPassword = (userLogin.getPassword().length() > maxPassword) ? userLogin.getPassword().length() : maxPassword;
            maxType = (userLogin.getType().toString().length() > maxType) ? userLogin.getType().toString().length() : maxType;
        }
        printColour(YELLOW, "All users in the database: \n");
        printColour(GREEN, String.format("    | " + RESET + YELLOW + "%-" + maxType + "s" + RESET + GREEN + " | " + RESET + YELLOW + "%-" + maxUsername + "s" + RESET + GREEN + " | " + RESET + YELLOW + "%-" + maxPassword + "s" + RESET + GREEN + " |", "TYPE", "USERNAME", "PASSWORD"));
        printColour(GREEN, "    |-" + String.format("%-" + maxType + "s", "").replace(' ', '-') + "-+-" + String.format("%-" + maxUsername + "s", "").replace(' ', '-') + "-+-" + String.format("%-" + maxPassword + "s", "").replace(' ', '-') + "-|");

        for (UserLogin userLogin : userLogins){
            printColour(GREEN, String.format("    | %-" + maxType + "s | %-" + maxUsername + "s | %-" + maxPassword + "s |", userLogin.getType().toString().toUpperCase(), userLogin.getUsername(), userLogin.getPassword()));
        }
    }
  
    // Displays the help commands
    public static void helpCommand(ArrayList<String> inputs) {

        // Checks input size
         if (inputs==null || inputs.size() == 1) {
            printColour(YELLOW, "---------------Available Commands:---------------");
            printColour(GREEN, "    buy -" + RESET + " purchase a product");
            printColour(GREEN, "    products -" + RESET + " list available products in the vending machine");
            printColour(GREEN, "    login -" + RESET + " login to a cashier/owner/seller account");
            printColour(GREEN, "    help -" + RESET + " display this screen");
            printColour(GREEN, "    quit -" + RESET + " quit the program");

            if(currentType == UserType.SELLER){
                printColour(YELLOW, "---------------Seller Commands-------------");
                printColour(GREEN, "    restock -" + RESET + " restock a specific item in the machine");
                printColour(GREEN, "    product add -" + RESET + " add a new item to the machine");
                printColour(GREEN, "    product remove -" + RESET + " remove an existing item from the machine");
                printColour(GREEN, "    modify -" + RESET + " modify an existing item in the machine");
            }
            if(currentType == UserType.CASHIER){
                printColour(YELLOW, "---------------Cashier Commands-------------");
                printColour(GREEN, "    cash check -" + RESET + " Returns the current denominations of all cash in the machines");
                printColour(GREEN, "    cash add -" + RESET + " Add a number of a denomination into the machine");
                printColour(GREEN, "    cash remove -" + RESET + " Remove a number of a denomination into the machine");
            }
            if(currentType == UserType.OWNER){
                printColour(YELLOW, "---------------Owner Commands-------------");
                printColour(GREEN, "    user add -" + RESET + " Add a new user to the system");
                printColour(GREEN, "    user remove -" + RESET + " Remove an existing user from the system");
                printColour(GREEN, "    user list -" + RESET + " Generate a list of users and their role");
                printColour(GREEN, "    list transactions -" + RESET + " View transaction history");
            }

        } else if (inputs.size() >= 2) {
            if (inputs.size() == 3){
                inputs.add(1, inputs.get(1)+inputs.get(2));
            }
            switch(inputs.get(1).toLowerCase()) {

                // Universal commands
                case "buyer":
                case "buy":
                    printColour(YELLOW, "Use this command to purchase a product from the vending machine.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    buy <cash/card> <product> <amount> [denominations...]");
                break;
                case "products":
                    printColour(YELLOW, "Use this command to list all products in the vending machine.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    products");
                break;
                case "login":
                    printColour(YELLOW, "Use this command to log in to a cashier/owner/seller account.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    login <username> <password>");
                break;
                case "help":
                    printColour(YELLOW, "Use this command to see available commands or for more information on a command");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    help <command>");
                break;
                case "exit":
                case "quit":
                case ":wq":
                case ":q!":
                    printColour(YELLOW, "Use this command to quit the program.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    quit");
                break;

                // Cashier Commands
                case "cashcheck":
                    printColour(YELLOW, "CASHIER USE ONLY: Returns the denominations fo coins currently in the machine");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    cash check");
                break;
                case "cashadd":
                    printColour(YELLOW, "CASHIER USE ONLY: Use this command to add money to the machine");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    cash add [<num>*<denomination> ...]");
                    break;
                case "cashremove":
                    printColour(YELLOW, "CASHIER USE ONLY: Use this command to remove money from the machine");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    cash remove [<num>*<denomination> ...]");
                break;

                 
                // Seller commands
                case "restock":
                    printColour(YELLOW, "SELLER USE ONLY: Use this command restock an item.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    restock <slot name> <restock count>");
                break;
                case "productadd":
                    printColour(YELLOW, "SELLER USE ONLY: Use this command add a new item.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    product add <slot name> <product name> <product price> <product category> <product stock>");
                break;
                case "productremove":
                    printColour(YELLOW, "SELLER USE ONLY: Use this command remove an existing item.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    product remove <slot name>");
                break;

               
                // Owner Commands
                case "useradd":
                    printColour(YELLOW, "OWNER USE ONLY: Use this command to add a new user to the list of logins");
                    printColour(YELLOW, "Usage: ");
                    printColour(GREEN, "    user add <username> <password> <user type>");
                break;
                case "userremove":
                    printColour(YELLOW, "OWNER USE ONLY: Use this command to remove a new user");
                    printColour(YELLOW, "Usage: ");
                    printColour(GREEN, "    user remove <username>");
                break;
                case "userlist":
                    printColour(YELLOW, "OWNER USE ONLY: Use this command to generate a list of all users and their types");
                    printColour(YELLOW, "Usage: ");
                    printColour(GREEN, "    user list");
                break;
                case "modify":
                    printColour(YELLOW, "SELLER USE ONLY");
                    printColour(YELLOW, "To change a slot's name:");
                    printColour(GREEN, "    modify <old_slot_name> slot name to <new_slot_name>\n");
                    printColour(YELLOW, "Note: this name must be unique in the vending machine");
                    printColour(YELLOW, "To change just one slot's product's details:");
                    printColour(GREEN, "    modify <slot_name> product name to <new_product_name>");
                    printColour(GREEN, "    modify <slot_name> product price to <new_price>");
                    printColour(GREEN, "    modify <slot_name> product category to <new_category>\n");
                    printColour(YELLOW, "To change all product's details that have the same name:");
                    printColour(GREEN, "    modify <product_name> name to <new_product_name>");
                    printColour(GREEN, "    modify <product_name> price to <new_price>");
                    printColour(GREEN, "    modify <product_name> category to <new_category>");
                break;
                default:
                    printColour(RED,String.format("Unrecognised command: %s", inputs.get(1)));
                break;
            }
        } 
    }

    // Displays a dynamically sized table of cash denominations in the machine
    public static void cashCheck(VendingMachine vm){

        printColour(YELLOW, "Cash Reserves:\n");

        int maxDenom = 13;
        int maxAmount = 7;
        // Gets correct spacing length for each column
        HashMap<String, Integer> currencyCounts = vm.getCurrencyCounts();
        String denomArr[] = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
        for(String currency : denomArr){
            String amount = String.valueOf(currencyCounts.get(currency));
            maxAmount = (amount.length() > maxAmount) ? amount.length() : maxAmount;
        }

        // Initialises and prints label row
        String denomSub = String.format(RESET + YELLOW + "%-" + maxDenom + "s" + RESET + GREEN, "DENOMINATION");
        String amountSub = String.format(RESET + YELLOW + "%-" + maxAmount + "s" + RESET + GREEN, "AMOUNT");
        printColour(GREEN, String.format("    | %s | %s |", denomSub, amountSub));

        // Initialises and prints spacing row
        String denomSpace = String.format("%-" + maxDenom + "s", "").replace(' ', '-');
        String amountSpace = String.format("%-" + maxAmount + "s", "").replace(' ', '-');
        printColour(GREEN, "    |-" + denomSpace + "-+-" + amountSpace + "-|");

        // Prints dynamically spaced contents
        for(String currency : denomArr){
            String amount = String.valueOf(currencyCounts.get(currency));
            printColour(GREEN, String.format("    | %-" + maxDenom + "s | %-" + maxAmount + "s |", currency, amount));
        }
    }

    // Displays a dynamically sized table of transactions
    public static void listTransactions(VendingMachine vm) {

        printColour(YELLOW, "Transaction History:\n");
        int maxID = 15;
        int maxPayment = 15;
        int maxPurchase = 9;

        // Checks if there are no transactions stored in the machine
        if (vm.getTransactions().size() == 0) {
            printColour(RED, "There are no transactions to display.");
        }

        // Gets spacing for each column
        for (Transaction transaction : vm.getTransactions()) {
                
            String id = String.valueOf(transaction.getID());
            String payment = transaction.getPaymentMethod();
            String purchase = String.format("%dx %s -> $%.2f", transaction.getQty(), transaction.getProductBought().getName(), transaction.getTotalAmount());

            maxID = (id.length() > maxID) ? id.length() : maxID;
            maxPayment = (payment.length() > maxPayment) ? payment.length() : maxPayment;
            maxPurchase = (purchase.length() > maxPurchase) ? purchase.length() : maxPurchase;
        }

        // Initialises and prints label row
        String idSub = String.format(RESET + YELLOW + "%-" + maxID + "s" + RESET + GREEN, "TRANSACTION ID");
        String paymentSub = String.format(RESET + YELLOW + "%-" + maxPayment + "s" + RESET + GREEN, "PAYMENT METHOD");
        String purchaseSub = String.format(RESET + YELLOW + "%-" + maxPurchase + "s" + RESET + GREEN, "PURCHASE");
        printColour(YELLOW, "Products available:\n");
        printColour(GREEN, String.format("    | %s | %s | %s |", idSub, paymentSub, purchaseSub));

        // Initialises and prints spacing row
        String idSpace = String.format("%-" + maxID + "s", "").replace(' ', '-');
        String paymentSpace = String.format("%-" + maxPayment + "s", "").replace(' ', '-');
        String purchaseSpace = String.format("%-" + maxPurchase + "s", "").replace(' ', '-');
        printColour(GREEN, "    |-" + idSpace + "-+-" + paymentSpace + "-+-" + purchaseSpace + "-|");

        // Prints information rows
        for (Transaction transaction : vm.getTransactions()) {

            String id = String.valueOf(transaction.getID());
            String payment = transaction.getPaymentMethod();
            String purchase = String.format("%dx %s -> $%.2f", transaction.getQty(), transaction.getProductBought().getName(), transaction.getTotalAmount());
            printColour(GREEN, String.format("    | %-" + maxID + "s | %-" + maxPayment + "s | %-" + maxPurchase + "s |", id, payment, purchase));
        }
    }

    // Adds cash to the machine
    public static boolean cashAdd(VendingMachine vm, ArrayList<String> inputs){

        // Checks input size
        if(inputs.size() < 2){
            printColour(RED, "Invalid input.");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    cash add [<num> <denomination> ...]");
            return false;
        }

        // Parses and checks input denominations
        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));
        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                printColour(RED, "Unrecognisable denomination.");
                printColour(GREEN, "Please use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                return false;
            }
                            
            // Ensures that entered amount is valid
            int amt = -1;
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return false;
            } 
            
            if (amt <= 0) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return false;
            }
            
            if (parseDenom(values[1]) == -1) return false;

            // Adds given currency to machine
            vm.addCurrencyCount(values[1], amt);
        }
        printColour(GREEN, "Currency successful added");
        return true;
    }

    // Removes cash from the machine
    public static boolean cashRemove(VendingMachine vm, ArrayList<String> inputs) {

        // Checks input size
        if(inputs.size() < 2){
            printColour(RED, "Invalid input");
            printColour(YELLOW, "Usage:");
            printColour(GREEN, "    cash remove [<num>*<denomination> ...]");
            return false;
        }

        // Parses and checks the input denominations
        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));
        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                printColour(RED, "Unrecognisable denomination.");
                printColour(GREEN, "Please use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                return false;
            }
                         
            // Ensures that entered amount is valid
            int amt = -1;
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return false;
            } 
            
            if (amt <= 0) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return false;
            }
            
            if (parseDenom(values[1]) == -1) return false;

            try {
                vm.removeCurrencyCount(values[1], amt);
            } catch (NoSuchElementException e) {
                printColour(RED, e.getMessage());
                return false;
            }
        }
        printColour(GREEN, "Currency successful removed");
        return true;
    }

    // Ends the program, and saves machine data to file
    public static void endProgram(VendingMachine vm) {
        vm.writeToFile(saveFilePath);
        printColour(YELLOW, "Quitting...");
    }

    // Runs the startup sequence for the machine
    public static VendingMachine initProgram() {

        // Restores the state of the vending machine to the latest save
        VendingMachine vm = new VendingMachine();
        vm.readFromFile(saveFilePath);

        String introBanner[] = new String[] 
        {"       @@@@@@    @@@@@@   @@@@@@@@@@   @@@@@@@@@@   @@@ @@@   @@@@@@           \n",
         "      @@@@@@@   @@@@@@@@  @@@@@@@@@@@  @@@@@@@@@@@  @@@ @@@  @@@@@@@           \n",
         "      !@@       @@!  @@@  @@! @@! @@!  @@! @@! @@!  @@! !@@  !@@               \n",
         "      !@!       !@!  @!@  !@! !@! !@!  !@! !@! !@!  !@! @!!  !@!               \n", 
         "      !!@@!!    @!@!@!@!  @!! !!@ @!@  @!! !!@ @!@   !@!@!   !!@@!!            \n",
         "       !!@!!!   !!!@!!!!  !@!   ! !@!  !@!   ! !@!    @!!!    !!@!!!           \n",
         "           !:!  !!:  !!!  !!:     !!:  !!:     !!:    !!:         !:!          \n",
         "          !:!   :!:  !:!  :!:     :!:  :!:     :!:    :!:        !:!           \n",
         "      :::: ::   ::   :::  :::     ::   :::     ::      ::    :::: ::           \n",
         "      :: : :     :   : :   :      :     :      :       :     :: : :            \n",
         "       @@@@@@   @@@  @@@   @@@@@@    @@@@@@@  @@@  @@@  @@@  @@@@@@@@   @@@@@@ \n",
         "      @@@@@@@   @@@@ @@@  @@@@@@@@  @@@@@@@@  @@@  @@@  @@@  @@@@@@@@  @@@@@@@ \n",
         "      !@@       @@!@!@@@  @@!  @@@  !@@       @@!  !@@  @@!  @@!       !@@     \n",
         "      !@!       !@!!@!@!  !@!  @!@  !@!       !@!  @!!  !@!  !@!       !@!     \n",
         "      !!@@!!    @!@ !!@!  @!@!@!@!  !@!       @!@@!@!   !!@  @!!!:!    !!@@!!  \n",
         "       !!@!!!   !@!  !!!  !!!@!!!!  !!!       !!@!!!    !!!  !!!!!:     !!@!!! \n",
         "           !:!  !!:  !!!  !!:  !!!  :!!       !!: :!!   !!:  !!:            !:!\n",
         "          !:!   :!:  !:!  :!:  !:!  :!:       :!:  !:!  :!:  :!:           !:! \n",
         "      :::: ::   ::   ::   ::   :::   ::: :::  ::  :::   ::   :: ::::  :::: ::  \n",
         "      :: : :    ::   :    :    : :   :: :: :  :   :::   :    : :: ::   :: : :  \n\n"};

        // Animates the intro banner for the program. NOTE that this only really works on UNIX systems
        // as it uses the clear terminal excape character, which isnt accepted on windows.
        double charCounter = 79;
        while (charCounter >= 0) {
            char[] line = new char[81];
            System.out.print("\033\143");
            System.out.flush();  
            for (String str : introBanner) {
                str.getChars(((Double) charCounter).intValue(),80, line,1);
                line[0] = '\r';
                System.out.print(line);
            }
            charCounter-= 1;
            try {
                Thread.sleep(80);
            }
            catch (Exception e) {
           
            printColour(RED, e.toString());
            }
        }
        System.out.print("\033\143");
        System.out.flush();
        for (String str : introBanner) {
            System.out.print(str);
        }
        printColour(GREEN, "Welcome to Sammy's Snackies!");
        helpCommand(null);
        System.out.print("\n" + MAGENTABG + currentType.toString().toUpperCase() + RESET + " > ");
        return vm;
    }

    // Unknown command error message
    public static void unknownCommand(ArrayList<String> inputs) {
        printColour(RED, "Unknown Command, use the help command to see available commands.");
    }

    public static void main(String[] args) {
        
        Scanner s = new Scanner(System.in);
        FoodItem f = new FoodItem("water", 1.50, Category.DRINK);

        VendingMachine vm = initProgram();
        vm.addSlot("A1", f, 5);

        userLogins = UserLogin.readFromFile(userLoginFilepath);

        // Continually takes input from the user
        while (true){
            while(s.hasNextLine()){
                // Gets and parses inputs 
                String input = s.nextLine();
                System.out.println();
                String[] userInput = input.split(" ");
                ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
                String cmd = inputs.get(0);
                
                // Runs command corresponding to input value
                switch(cmd.toLowerCase()) {

                    case "buy":
                        buyer(inputs, vm);
                    break;
                    case "user":
                        if (inputs.size() > 1) {
                            // ensures the user is logged in as an owner
                            if(currentType != UserType.OWNER){
                                unknownCommand(inputs);
                            } else {
                                if (inputs.get(1).equals("remove")) {
                                    inputs.remove(0);
                                    removeUser(inputs);
                                } else if (inputs.get(1).equals("add")) {
                                    inputs.remove(0);
                                    addUser(inputs);
                                } else if (inputs.get(1).equals("list")) {
                                    inputs.remove(0);
                                    userList(inputs);
                                } else {
                                    unknownCommand(inputs);
                                }
                            }
                        } else { 
                            unknownCommand(inputs);
                        }
                        
                        break;
                    case "list":
                        if (inputs.size() > 1) {
                            if (inputs.get(1).equals("transactions")) {
                                if (currentType != UserType.OWNER) {
                                    unknownCommand(inputs);
                                } else {
                                    listTransactions(vm);
                                }
                            } else {
                                unknownCommand(inputs);
                            }
                        } else{
                            unknownCommand(inputs);
                        }
                    break;
                    case "login":
                        userLogin(inputs);
                    break;
                    case "restock":
                        if (currentType != UserType.SELLER) {
                            unknownCommand(inputs);
                        } else {
                            restockProduct(inputs, vm);
                        }
                    break;
                    case "product":
                        if (inputs.size() > 1) {
                            if (currentType != UserType.SELLER) {
                                unknownCommand(inputs);
                            } else {
                                if (inputs.get(1).equals("remove")) {
                                    inputs.remove(0);
                                    removeProduct(inputs, vm);
                                } else if (inputs.get(1).equals("add")) {
                                    inputs.remove(0);
                                    addProduct(inputs, vm);
                                } else {
                                    unknownCommand(inputs);
                                }
                            }
                        } else{
                            unknownCommand(inputs);
                        }
                    break;
                    case "modify":
                        if (currentType != UserType.SELLER) {
                            unknownCommand(inputs);

                        } else {
                            System.out.println(modify(inputs, vm));
                        }
                    break;
                    case "products":
                        litProducts(vm);
                    break;
                    case "help":
                        helpCommand(inputs);
                    break;
                    case "cash":
                    if (inputs.size() > 1) {
                            
                        if (currentType != UserType.CASHIER) {
                            unknownCommand(inputs);
                        } else {
                            if (inputs.get(1).equals("remove")) {
                                inputs.remove(0);
                                cashRemove(vm, inputs);
                            } else if (inputs.get(1).equals("add")) {
                                inputs.remove(0);
                                cashAdd(vm, inputs);
                            } else if (inputs.get(1).equals("check")) {
                                inputs.remove(0);
                                cashCheck(vm);
                            } else {
                                unknownCommand(inputs);
                            }
                        }
                    } else{
                        unknownCommand(inputs);
                    }
                    
                    break;

                    case "exit":
                    case "quit":
                    case ":wq":
                    case ":q!":
                        endProgram(vm);
                        s.close();
                        return;
                    default:
                        unknownCommand(inputs);
                    break;
                }
                System.out.print("\n" + MAGENTABG + currentType.toString().toUpperCase() + RESET + " > ");
            }
        }
    }
}
