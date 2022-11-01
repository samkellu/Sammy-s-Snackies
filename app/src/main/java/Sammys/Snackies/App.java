package Sammys.Snackies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.time.*;
import java.util.NoSuchElementException;

public class App {

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    private static final String saveFilePath = "saveFile.json";
    private static UserType currentType = UserType.BUYER;
    private static ArrayList<UserLogin> userLogins;
    private static final String userLoginFilepath = "userLogins.json";

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

    private static void printColour(String colour, String str) {
        System.out.println(colour + str + RESET);
    }

    private static void products(VendingMachine v) {

        printColour(YELLOW, "Products available:\n");
        boolean noProducts = true;
        printColour(GREEN, "    | " + RESET + YELLOW + "SLOT" + RESET + GREEN + " | " + RESET + YELLOW + "NAME" + RESET + GREEN + "        | " + RESET + YELLOW + "QTY" + RESET + GREEN + "  | " + RESET + YELLOW + "PRICE" + RESET + GREEN + "    |");
        printColour(GREEN, "    |------+-------------+------+----------|");
        for (String key : v.getSlots().keySet()) {
            if (v.getSlots().get(key).getCount() > 0) {
                printColour(GREEN, "    " + v.getSlots().get(key).toString());
                noProducts = false;
            }
        }
        if (noProducts) printColour(RED, "Sorry, there are no products available in this machine.");
    }

    private static void printCardError() {
        printColour(RED, "Invalid card details entered, please try again or type \"quit\" to cancel this transaction.\nCard details must be of the form:\n");
        printColour(GREEN, "  CARD NUMBER (16) MM/YY CVC\n  **************** **/** ***");
        System.out.print("> ");
    }

    private static void buyer(ArrayList<String> inputs, VendingMachine vm) {

        // ensure enough arguments
        if (inputs.size() < 4) {
            printColour(RED, "Not enough arguments. Use \"help buyer\" to see required arguments.");
            return;
        }
        // check cash or card
        boolean cash = false;
        if (inputs.get(1).toLowerCase().equals("cash")) {
            cash = true;

            // ensure enough arguments for cash payment
            if (inputs.size() < 4) {
                printColour(RED, "Not enough arguments. Use \"help buyer\" to see required arguments.");
                return;
            }
        }
        else if (!inputs.get(1).toLowerCase().equals("card")) {
            printColour(RED, "Please specify payment type (cash or card).");
            return;
        }

        // check product code exists
        Slot slot = null;

        // TODO
        // boolean validProduct = false; // unused, if needed, just uncomment this

        // checks all slots in the machine for a matching product code
        for (Slot s : vm.getSlots().values()) {
            if (inputs.get(2).toLowerCase().equals(s.getContents().getName().toLowerCase())) {
                slot = s;
                break;
            }
        }

        if (slot == null) {
            printColour(RED, "Please enter a valid product code. This machine contains no item with code: " + inputs.get(2));
            return;
        }

        // check the machine has sufficient quantity 
        int productAmt = -1;
        try {
            productAmt = Integer.parseInt(inputs.get(3));
        } catch (NumberFormatException e) {
            printColour(RED, "\nPlease ensure the product amount is a positive integer.");
            return;
        }

        if (productAmt <= 0) {
            printColour(RED, "\nPlease ensure the product amount is a positive integer.");
            return;
        }

        if (slot.getCount() < productAmt) {
            if (slot.getCount() == 0){
                printColour(RED, "Unfortunately, this machine is all out of stock of " + slot.getContents().getName());
            } else {
                printColour(RED, "Unfortunately, this machine only has " + slot.getCount() + "x " + slot.getContents().toString() + " available.");
            }
            return;
        }

        double totalGiven = 0.00;
        String changeString = "";
        // get the cost
        double price = -1;
        for (String s : vm.getSlots().keySet()) {
            if (vm.getSlots().get(s).getContents().getName().equals(inputs.get(2))) {
                price = vm.getSlots().get(s).getContents().getPrice();
            }
        }
        if (price == -1) {
            printColour(RED, "\nSorry an internal error occured, please try again.");
            return;
        }

        double totalCost = productAmt*price;

        // handle all cash specific items
        if (cash) {

            ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(4, inputs.size()));
            HashMap<String, Integer> givenDenominations = new HashMap<>();
            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};

            for (String s : inputDenoms) {

                String[] values = s.split("\\*");
                ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
                
                if (values.length != 2 || !(denomSet.contains(values[1]))) {
                    printColour(RED, "Unrecognisable denomination.\nPlease use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                    return;
                }
                
                String v = values[1];                
                String amount = values[0];
                int amt = -1;
                
                try {
                    amt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    printColour(RED, "\nPlease ensure the cash amount is a positive integer.");
                    return;
                } 
                
                if (amt <= 0) {
                    printColour(RED, "\nPlease ensure the amount is a positive integer.");
                    return;
                }
                

                double value = parseDenom(v);
                if (value == -1) return;

                givenDenominations.put(v, amt);
                totalGiven += value*amt;
            }

            // get the change
            HashMap<String, Integer> changeToGive;


            try {
                changeToGive = vm.getChangeFromCash(totalGiven-totalCost);
            } catch (IndexOutOfBoundsException e) {
                printColour(RED, "\nSorry, we don't have the change to give you.\nReturning money...");
                return;
            }

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

        } else {

            boolean attempting = true;
            Scanner s = new Scanner(System.in);
            String input = new String();

            printColour(YELLOW, "Please enter your card details, or type \"quit\" to cancel this transaction.\nCard details must be of the form:\n");
            printColour(GREEN, "  CARD NUMBER (16) MM/YY CVC\n  **************** **/** ***");
            System.out.print("> ");

            while (attempting) {
                input = s.nextLine();
                System.out.println();
                if (input.toLowerCase().equals("quit")) {
                    printColour(GREEN, "Transaction cancelled.");
                    return;
                }

                String sepInput[] = input.split(" ");

                if (sepInput.length != 3) {
                    printCardError();
                    continue;
                }

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
                }
            }
        }

        // dispense products and change
        printColour(GREEN, String.format("You recieved %sx %s.", inputs.get(3), slot.getContents().toString()));
        printColour(GREEN, String.format("You paid $%.2f.", totalCost));
        printColour(GREEN, changeString);
        printColour(YELLOW, "Thank you for shopping at Sammy's Snackies!");

        slot.sellContents(Integer.valueOf(inputs.get(3)));

        vm.addTransaction(inputs.get(1).toLowerCase(), slot.getContents(), Integer.valueOf(inputs.get(3)));
        // TODO
        // ensure enough money given, need the vending machine to know the price.
    }
  
    public static boolean verifyCard(long cardNumber, String date, int cvc) {

        String dateArr[] = date.split("/");
        if (dateArr.length != 2 || dateArr[0].length() > 2 || dateArr[1].length() > 2 || dateArr[0].length() <= 0 || dateArr[1].length() <= 0) {
            return false;
        }
            
        LocalDate localDate = LocalDate.now();
        try {
            date = String.format("20%d-%02d-01",Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]));
            if (String.valueOf(cardNumber).length() == 16 && (String.valueOf(cvc).length() == 3 || String.valueOf(cvc).length() == 4) && localDate.isBefore(LocalDate.parse(date))) {
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private static void restockProduct(ArrayList<String> inputs, VendingMachine vm){
        // takes itemname itemcount 

        if (inputs.size() != 3){
            printColour(RED, "Invalid input. Use \"help restock\" for help");
            return;
        }

        // check product code exists
        Slot slot = null;
        // checks all slots in the machine for a matching product code
        for (String s : vm.getSlots().keySet()) {
            if (s.equals(inputs.get(1).toUpperCase())){
                slot = vm.getSlots().get(s);
                break;
            }
        }
        if(slot==null){
            printColour(RED, "Could not find slot " + inputs.get(1));
            return;
        }
        int restockCount;
        try {
            restockCount = Integer.parseInt(inputs.get(2));
        } catch (NumberFormatException e){
            printColour(RED, "Please enter a valid integer for restock count");
            return;
        }
        try{
            slot.restockContents(restockCount);
        } catch (IndexOutOfBoundsException e){
            printColour(RED, e.getMessage());
            return;
        }

        printColour(GREEN, "Successfully restocked " + inputs.get(2) +" "+slot.getContents().getName()+"'s, new stock count is " + Integer.toString(slot.getCount()) + " with a value of $" +String.format("%.2f", slot.getCount()*slot.getContents().getPrice()));
    }

    private static void addProduct(ArrayList<String> inputs, VendingMachine vm){

        // Input: 
        // addProduct <slot name> <product name> <product price> <product category> <product stock>

        if (inputs.size() != 6){
            printColour(RED, "Invalid input, use \"help add product\" to get help");
            return;
        }

        String slotName = inputs.get(1).toUpperCase();
        Slot currentSlot = null;
        for (String name : vm.getSlots().keySet()){
            if (name.equals(slotName)){
                currentSlot = vm.getSlots().get(name);
                if (currentSlot.getCount()!=0){
                    printColour(RED, "Slot already exists and is non empty! Please choose an empty slot or try \"restock\"");
                    return;
                }
            }
        }
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
                printColour(GREEN, "CHOCOLATE | CANDY | CHIPS | DRINK");
                return;
        }

        try{
            String priceStr = inputs.get(3);
            if (priceStr.charAt(0) == '$'){
                priceStr = priceStr.substring(1);
            }
            Double price = Double.parseDouble(priceStr);
            FoodItem newFood = new FoodItem(inputs.get(2).toLowerCase(), price, foodCategory);
            if (currentSlot != null){
                vm.getSlots().remove(currentSlot.getName());
            }
            currentSlot = new Slot(slotName, newFood,Integer.parseInt(inputs.get(5)));
            if (currentSlot.getCount() > 15){
                printColour(RED, "Slots can only hold up to 15 items! Please try again");
            }
            vm.getSlots().put(slotName, currentSlot);
            printColour(GREEN, "Added " + currentSlot.getContents().getName() + " to slot " + currentSlot.getName() + " at a price of $" + String.format("%.2f", newFood.getPrice()));
        } catch (NumberFormatException e){
            printColour(RED, "Please use a decimal number for price and an integer for food item count");
            return;
        }
    }

    private static void removeProduct(ArrayList<String> inputs, VendingMachine vm){
        // Useage:
        // removeProduct <slotname>

        if (inputs.size() != 2){
            printColour(RED, "Invalid input length, use \"help removeProduct\" for help");
            return;
        }

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
            return;
        }
        printColour(GREEN, "Removing product " + slot.getContents().getName() + " from slot " + inputs.get(1).toUpperCase() + " for a total value of $" + String.format("%.2f", slot.getContents().getPrice()*slot.getCount()));
        vm.getSlots().remove(slot.getName());
        return;
    }

    private static void addUser(ArrayList<String> inputs) {
        if (currentType != UserType.OWNER){
            printColour(RED, "You are unauthorised!! Owner role is required, please log in.");
            return;
        }

        if (inputs.size() != 4){
            printColour(RED, "Incorrect number of parameters. Use \"help addUser\" for more information.");
            return;
        }
        String username = inputs.get(1);
        for (UserLogin user : userLogins){
            if (user.getUsername().equals(username)){
                printColour(RED, "User already exists, please choose a unique username");
                return;
            }
        }
        String password = inputs.get(2);
        UserType type = UserType.fromName(inputs.get(3).toLowerCase());
        UserLogin user = new UserLogin(username, password, type);
        userLogins.add(user);
        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
        printColour(GREEN, "New user added with username " + username + " with role of " + type);

    }

    private static void userLogin(ArrayList<String> inputs) {

        if (inputs.size() != 3) {
            printColour(RED, "Incorrect number of parameters. Use \"help login\" for more information.");
            return;
        }

        for (UserLogin user : userLogins){
            if (user.verifyLogin(inputs.get(1), inputs.get(2))){
                printColour(GREEN, "Welcome, " + user.getUsername());
                currentType = user.getType();
                printColour(YELLOW, "You are now logged in as a " + user.getType());
                return;
            }
        }

        printColour(RED, "Login not found, try again");
        // TODO
        // check login, maybe we use a file of users and pwds?
    }

    private static void removeUser(ArrayList<String> inputs){
        if (currentType != UserType.OWNER){
            printColour(RED, "You are unauthorised!! Owner role is required, please log in.");
            return;
        }
        if (inputs.size() != 2){
            printColour(RED, "Incorrect paramaters! Use \"help removeUser\" to recieve help!");
            return;
        }
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
            return;
        }

        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
    }

    private static void userList(ArrayList<String> inputs){

        if (inputs.size() != 1){
            printColour(YELLOW, "This command takes no user input!");
        }

        int max = 0;
        for (UserLogin userLogin :userLogins){
            if (userLogin.getUsername().length() > max){
                max = userLogin.getUsername().length();
            }
        }

        String space = "";
        for (int i = 0; i < max; i++){
            space+=" ";
        }
        printColour(YELLOW, "Username" + space + "Password");
        for (UserLogin userLogin : userLogins){
            StringBuilder str = new StringBuilder();

            str.append(userLogin.getUsername());
            for (int i = 0; i < max+8-userLogin.getUsername().toString().length(); i++){
                str.append(" ");
            }
            str.append(userLogin.getType());
            // str.append();
            printColour(GREEN, str.toString());
        }
    }
    // TODO
    // add message at the end saying something like "to see more on a command use help <command>"
    private static void helpCommand(ArrayList<String> inputs) {
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
                    printColour(GREEN, "    cash  add [<num> <denomination> ...]");
                    break;
                case "cashremove":
                    printColour(YELLOW, "CASHIER USE ONLY: Use this command to remove money from the machine");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    cash remove [<num> <denomination> ...]");
                break;

                 
                // Seller commands
                case "restockcontents":
                    printColour(YELLOW, "SELLER USE ONLY: Use this command restock an item.");
                    printColour(YELLOW, "Usage:");
                    printColour(GREEN, "    restock contents <slot name> <restock count>");
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
                default:
                    printColour(RED,String.format("Unrecognised command: %s", inputs.get(1)));
                break;
            }
        } 
    }

    private static void cashCheck(VendingMachine vm){
        HashMap<String, Integer> currencyCounts = vm.getCurrencyCounts();
        printColour(YELLOW, "   DENOMINATION " + RESET + GREEN + "|" + RESET + YELLOW + " AMOUNT");
        printColour(GREEN, "   -------------+-------");
        String denomArr[] = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
        for(String currency : denomArr){
            printColour(GREEN, String.format("   %-12s | %d", currency, currencyCounts.get(currency)));
        }
    }

    private static void listTransactions(VendingMachine vm) {
        printColour(YELLOW, "Transaction History:");
        printColour(GREEN, "\n    | " + RESET + YELLOW + "TRANSACTION ID" + RESET + GREEN + " | " + RESET + YELLOW + "PAYMENT METHOD" + RESET + GREEN + " | " + RESET + YELLOW + "PURCHASE" + RESET + GREEN + "                      |");
        printColour(GREEN, "    |----------------+----------------+-------------------------------|");
        for (Transaction transaction : vm.getTransactions()) {
            printColour(GREEN, "    " + transaction.toOutput());
        }
    }

    private static void cashAdd(VendingMachine vm, ArrayList<String> inputs){

        if(inputs.size() < 2){
            printColour(RED, "Incorrect number of parameters. Use \"help cash add\" for more information.");
            return;
        }

        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));

        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                printColour(RED, "Unrecognisable denomination.");
                printColour(GREEN, "Please use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                return;
            }
                            
            int amt = -1;
            
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return;
            } 
            
            if (amt <= 0) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return;
            }
            
            if (parseDenom(values[1]) == -1) return;

            vm.addCurrencyCount(values[1], amt);
        }

        printColour(GREEN, "Currency successful added");
    }

    private static void cashRemove(VendingMachine vm, ArrayList<String> inputs) {

        if(inputs.size() < 2){
            printColour(RED, "Incorrect number of parameters. Use \"help cash remove\" for more information.");
            return;
        }

        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));

        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                printColour(RED, "Unrecognisable denomination.");
                printColour(GREEN, "Please use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                return;
            }
                            
            int amt = -1;
            
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return;
            } 
            
            if (amt <= 0) {
                printColour(RED, "Please ensure the cash amount is a positive integer.");
                return;
            }
            
            if (parseDenom(values[1]) == -1) return;

            try {
                vm.removeCurrencyCount(values[1], amt);
            } catch (NoSuchElementException e) {
                printColour(RED, e.getMessage());
                return;
            }
        }
        printColour(GREEN, "Currency successful removed");

    }

    private static void endProgram(VendingMachine vm) {
        vm.writeToFile(saveFilePath);
        printColour(YELLOW, "Quitting...");
    }

    private static VendingMachine initProgram() {
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

        // INTRO ANIMATION CODE
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
        System.out.print("\n> ");
        return vm;
    }

    private static void unknownCommand(ArrayList<String> inputs) {
        printColour(RED, "Unknown Command, use the help command to see available commands.");
    }

    public static void main(String[] args) {
        
        Scanner s = new Scanner(System.in);
        FoodItem f = new FoodItem("water", 1.50, Category.DRINK);

        VendingMachine vm = initProgram();
        vm.addSlot("A1", f, 5);

        userLogins = UserLogin.readFromFile(userLoginFilepath);

        while (true){
            while(s.hasNextLine()){
                
                String input = s.nextLine();
                System.out.println();
                String[] userInput = input.split(" ");
                ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
                String cmd = inputs.get(0);
                
                switch(cmd.toLowerCase()) {

                    case "buy":
                        buyer(inputs, vm);
                    break;
                    // Deprecated
                    // case "cashier":
                    //     cashier(inputs);
                    // break;
                    case "user":
                        if(inputs.size() > 1){
                            
                            if(currentType != UserType.OWNER){
                                unknownCommand(inputs);
                            }
                            else{
                                if(inputs.get(1).equals("remove")){
                                    inputs.remove(0);
                                    removeUser(inputs);
                                }
                                else if(inputs.get(1).equals("add")){
                                    inputs.remove(0);
                                    addUser(inputs);
                                } if (inputs.get(1).equals("list")){
                                    inputs.remove(0);
                                    userList(inputs);
                                }
                                else{
                                    unknownCommand(inputs);
                                }
                            
                                }
                        }
                        else{
                            unknownCommand(inputs);
                        }
                        
                        break;
                    case "list":
                        if(inputs.size() > 1){
                            if(inputs.get(1).equals("transactions")){
                                if(currentType != UserType.OWNER){
                                    unknownCommand(inputs);
                                }
                                else{
                                    listTransactions(vm);
                                }
                            }
                            else{
                                unknownCommand(inputs);
                            }
                        }
                        else{
                            unknownCommand(inputs);
                        }
                    break;
                    case "login":
                        userLogin(inputs);
                    break;
                    case "restock":
                        if (currentType != UserType.SELLER){
                            unknownCommand(inputs);

                        } else {
                            restockProduct(inputs, vm);
                        }
                    break;
                    case "product":
                        if(inputs.size() > 1){
                                
                            if(currentType != UserType.SELLER){
                                unknownCommand(inputs);
                            }
                            else{
                                if(inputs.get(1).equals("remove")){
                                    inputs.remove(0);
                                    removeProduct(inputs, vm);
                                }
                                else if(inputs.get(1).equals("add")){
                                    inputs.remove(0);
                                    addProduct(inputs, vm);
                                }
                                else{
                                    unknownCommand(inputs);
                                }
                            
                            }
                        }
                        else{
                            unknownCommand(inputs);
                        }
                        
                        break;
                    
                    case "products":
                        products(vm);
                    break;
                    case "help":
                        helpCommand(inputs);
                    break;
                    case "cash":
                    if(inputs.size() > 1){
                            
                        if(currentType != UserType.CASHIER){
                            unknownCommand(inputs);
                        }
                        else{
                            if(inputs.get(1).equals("remove")){
                                inputs.remove(0);
                                cashRemove(vm, inputs);
                            }
                            else if(inputs.get(1).equals("add")){
                                inputs.remove(0);
                                cashAdd(vm, inputs);
                            }
                            else if(inputs.get(1).equals("check")){
                                inputs.remove(0);
                                cashCheck(vm);
                            }
                            else{
                                unknownCommand(inputs);
                            }
                        
                            }
                    }
                    else{
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
                System.out.print("\n> ");
            }
        }
    }
}
