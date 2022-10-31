package Sammys.Snackies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

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
                System.out.println(RED + "Please enter a valid monetary denomination." + RESET);
                return -1;
        }
    }

    private static void products(VendingMachine v) {

        System.out.println("\nProducts available:");
        boolean noProducts = true;
        for (String key : v.getSlots().keySet()) {
            if (v.getSlots().get(key).getCount() > 0) {
                System.out.println(v.getSlots().get(key));
                noProducts = false;

            }
        }
        if (noProducts) System.out.println(RED + "\nSorry, there are no products available in this machine." + RESET);
    }

    private static void buyer(ArrayList<String> inputs, VendingMachine vm) {

        // ensure enough arguments
        if (inputs.size() < 4) {
            System.out.println(RED + "Not enough arguments. Use \"help buyer\" to see required arguments." + RESET);
            return;
        }
        // check cash or card
        boolean cash = false;
        if (inputs.get(1).toLowerCase().equals("cash")) {
            cash = true;

            // ensure enough arguments for cash payment
            if (inputs.size() < 4) {
                System.out.println(RED + "Not enough arguments. Use \"help buyer\" to see required arguments." + RESET);
                return;
            }
        }
        else if (!inputs.get(1).toLowerCase().equals("card")) {
            System.out.println(RED + "Please specify payment type (cash or card)." + RESET);
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
            System.out.println(RED + "Please enter a valid product code. This machine contains no item with code: " + inputs.get(2) + RESET);
            return;
        }

        // check the machine has sufficient quantity 
        int productAmt = -1;
        try {
            productAmt = Integer.parseInt(inputs.get(3));
        } catch (NumberFormatException e) {
            System.out.println(RED + "\nPlease ensure the product amount is a positive integer." + RESET);
            return;
        }

        if (productAmt <= 0) {
            System.out.println(RED + "\nPlease ensure the product amount is a positive integer." + RESET);
            return;
        }

        if (slot.getCount() < productAmt) {
            if (slot.getCount() == 0){
                System.out.println(RED + "Unfortunately, this machine is all out of stock of " + slot.getContents() + RESET);
            } else {
                System.out.println(RED + "Unfortunately, this machine only has " + slot.getCount() + "x " + slot.getContents().toString() + " available." + RESET);
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
            System.out.println(RED + "\nSorry an internal error occured, please try again." + RESET);
            return;
        }

        double totalCost = productAmt*price;

        // handle all cash specific items
        if (cash) {

            ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(4, inputs.size()));
            HashMap<String, Integer> givenDenominations = new HashMap<>();

            for (String s : inputDenoms) {

                String[] values = s.split("\\*");
                String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
                ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
                


                if (values.length != 2 || !(denomSet.contains(values[1]))) {
                    System.out.println(RED + "Unrecognisable denomination.\nPlease use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer." + RESET);
                    return;
                }
                
                String v = values[1];                
                String amount = values[0];
                int amt = -1;
                
                try {
                    amt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    System.out.println(RED + "\nPlease ensure the cash amount is a positive integer." + RESET);
                    return;
                } 
                
                if (amt <= 0) {
                    System.out.println(RED + "\nPlease ensure the amount is a positive integer." + RESET);
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
                System.out.println(RED + "\nSorry, we don't have the change to give you.\nReturning money..." + RESET);
                return;
            }

            boolean givingChange = false;
            changeString = "Change given: ";
            for (String s : changeToGive.keySet()) {
                if (changeToGive.get(s) > 0) {
                    givingChange = true;
                    changeString += String.format("%dx%s, ", changeToGive.get(s), s);
                }
            }
            changeString += "\n\n";

            if (!givingChange) {
                changeString = "Correct change given, no change to give\n\n";
            }    
        }

        // dispense products and change
        System.out.println(String.format("\nYou recieved %sx %s.", inputs.get(3), slot.getContents().toString()));
        System.out.println(String.format("You paid $%.2f.", totalCost));
        System.out.print(changeString);
        System.out.println("Thank you for shopping at Sammy's Snackies!");

        slot.sellContents(Integer.valueOf(inputs.get(3)));

        vm.addTransaction(inputs.get(1).toLowerCase(), slot.getContents(), Integer.valueOf(inputs.get(3)));
        // TODO
        // ensure enough money given, need the vending machine to know the price.
    }



    private static void restockProduct(ArrayList<String> inputs, VendingMachine vm){
        // takes itemname itemcount 

        if (inputs.size() != 3){
            System.out.println("Invalid input. Use \"help restockContents\" for help");
            return;
        }

        // if (!vm.isInMachine(inputs.get(1))){
        //     System.out.println("Item not found, use \"help restockContents\" for help");
        //     return;
        // }


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
            System.out.println("Could not find slot " + inputs.get(1));
            return;
        }
        int restockCount;
        try {
            restockCount = Integer.parseInt(inputs.get(2));
        } catch (NumberFormatException e){
            System.out.println("Please enter a valid integer for restock count");
            return;
        }
        try{
            slot.restockContents(restockCount);
        } catch (IndexOutOfBoundsException e){
            System.out.println(e);
            return;
        }

        System.out.println("Successfully restocked " + inputs.get(2) +" "+slot.getContents().getName()+"'s, new stock count is " + Integer.toString(slot.getCount()) + " with a value of $" +String.format("%.2f", slot.getCount()*slot.getContents().getPrice()));



    }


    private static void addProduct(ArrayList<String> inputs, VendingMachine vm){

        // Input: 
        // addProduct <slot name> <product name> <product price> <product category> <product stock>

        if (inputs.size() != 6){
            System.out.println("Invalid input, use \"help addProduct\" to get help");
            return;
        }

        String slotName = inputs.get(1).toUpperCase();
        Slot currentSlot = null;
        for (String name : vm.getSlots().keySet()){
            if (name.equals(slotName)){
                currentSlot = vm.getSlots().get(name);
                if (currentSlot.getCount()!=0){
                    System.out.println("Slot already exists and is non empty! Please choose an empty slot or try \"restockProduct\"");
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
                System.out.println("Category not found! Please choose one of the following categories:");
                System.out.println("CHOCOLATE | CANDY | CHIPS | DRINK");
                return;

        }
        try{
            FoodItem newFood = new FoodItem(inputs.get(2).toLowerCase(), Double.parseDouble(inputs.get(3)), foodCategory);
            if (currentSlot != null){
                vm.getSlots().remove(currentSlot.getName());
            }
            currentSlot = new Slot(slotName, newFood,Integer.parseInt(inputs.get(5)));
            if (currentSlot.getCount() > 15){
                System.out.println("Slots can only hold up to 15 items! Please try again");
            }
            vm.getSlots().put(slotName, currentSlot);
            System.out.println("Added " + currentSlot.getContents().getName() + " to slot " + currentSlot.getName() + " at a price of $" + String.format("%.2f", newFood.getPrice()));
        } catch (NumberFormatException e){
            System.out.println("Please use a decimal number for price and an integer for food item count");
            return;
        }




    }

    private static void cashier(ArrayList<String> inputs) {
        if (currentType != UserType.CASHIER){
            System.out.println(RED + "You are unauthorised!! Cashier role is required, please log in." + RESET);
            return;
        }
    }

    private static void addUser(ArrayList<String> inputs) {
        if (currentType != UserType.OWNER){
            System.out.println(RED + "You are unauthorised!! Owner role is required, please log in." + RESET);
            return;
        }

        if (inputs.size() != 4){
            System.out.println(RED + "Incorrect number of parameters. Use \"help addUser\" for more information." + RESET);
            return;
        }
        String username = inputs.get(1);
        for (UserLogin user : userLogins){
            if (user.getUsername().equals(username)){
                System.out.println(RED + "User already exists, please choose a unique username" + RESET);
                return;
            }
        }
        String password = inputs.get(2);
        UserType type = UserType.fromName(inputs.get(3).toLowerCase());
        UserLogin user = new UserLogin(username, password, type);
        userLogins.add(user);
        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
        System.out.println(GREEN + "New user added with username " + username + " with role of " + type + RESET);

    }

    private static void userLogin(ArrayList<String> inputs) {

        if (inputs.size() != 3) {
            System.out.println(RED + "Incorrect number of parameters. Use \"help login\" for more information." + RESET);
            return;
        }

        for (UserLogin user : userLogins){
            if (user.verifyLogin(inputs.get(1), inputs.get(2))){
                System.out.println(GREEN + "Welcome, " + user.getUsername() + RESET);
                currentType = user.getType();
                System.out.println("You are now logged in as a " + user.getType());
                return;
            }
        }

        System.out.println(RED + "Login not found, try again" + RESET);
        // TODO
        // check login, maybe we use a file of users and pwds?
    }

    private static void removeUser(ArrayList<String> inputs){
        if (currentType != UserType.OWNER){
            System.out.println(RED + "You are unauthorised!! Owner role is required, please log in." + RESET);
            return;
        }
        if (inputs.size() != 2){
            System.out.println(RED + "Incorrect paramaters! Use \"help removeUser\" to recieve help!" + RESET);
            return;
        }
        String username = inputs.get(1);
        boolean isFound = false;
        for (int i = 0; i < userLogins.size(); i++){
            if (userLogins.get(i).getUsername().equals(username)){
                userLogins.remove(i);
                System.out.println(GREEN + "Removed user " + username + RESET);
                isFound = true;
                break;

            }
        }
        if (!isFound){
            System.out.println(RED + "User not found, please choose another username" + RESET);
            return;
        }

        UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
    }

    // TODO
    // add message at the end saying something like "to see more on a command use help <command>"
    private static void helpCommand(ArrayList<String> inputs) {
         if (inputs==null || inputs.size() == 1) {
            System.out.println(YELLOW + "\n---------------Available Commands:---------------" + RESET);
            System.out.println("buyer - buy a product");
            System.out.println("seller - TODO"); // TODO
            System.out.println("products - list available products in the vending machine");
            System.out.println("login - login to a cashier/owner/seller account");
            System.out.println("help - display this screen");
            System.out.println("quit - quit the program");

            if(currentType == UserType.SELLER){
                System.out.println(YELLOW + "---------------Seller Commands-------------" + RESET);
                System.out.println("restockContents - restock a specific item in the machine");
                System.out.println("addProduct - add a new item to the machine");

            }
            if(currentType == UserType.CASHIER){
                System.out.println(YELLOW + "---------------Cashier Commands-------------" + RESET);
                System.out.println("CashCheck - Returns the current denominations of all cash in the machines");
                System.out.println("CashAdd - Add a number of a denomination into the machine");
                System.out.println("CashRemove - Remove a number of a denomination into the machine");
            }
            if(currentType == UserType.OWNER){
                System.out.println(YELLOW + "---------------Owner Commands-------------" + RESET);
                System.out.println("addUser - Add a new user to the system");
                System.out.println("removeUser - Remove an existing user from the system");
                System.out.println("listTransactions - View transaction history");
            }

        } else if (inputs.size() >= 2) {

            switch(inputs.get(1).toLowerCase()) {
                case "buyer":
                case "buy":
                    System.out.println("\nUse this command to buy a product from the vending machine.");
                    System.out.println("Usage:");
                    System.out.println("buyer <cash/card> <product> <amount> [denominations...]\n");
                break;
                
                case "adduser":
                    System.out.println("\n OWNER USE ONLY: Use this command to add a new user to the list of logins");
                    System.out.println("Useage: ");
                    System.out.println(GREEN + "addUser <username> <password> <user type>" + RESET);
                break;
                case "removeuser":
                System.out.println("\n OWNER USE ONLY: Use this command to remove a new user");
                System.out.println("Useage: ");
                System.out.println(GREEN + "removeUser <username>" + RESET);
                break;
                case "cashier":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("cashier TODO");
                break;
                case "login":
                    System.out.println("\nUse this command to log in to a cashier/owner/seller account.");
                    System.out.println("Usage:");
                    System.out.println(GREEN + "login <username> <password>" + RESET);
                break;
                case "products":
                case "product":
                System.out.println("\nUse this command to list all products in the vending machine.");
                System.out.println("Usage:");
                System.out.println(GREEN + "products" + RESET);
                break;
                case "help":
                    System.out.println("\nUse this command to see available commands or for more information on a command");
                    System.out.println("Usage:");
                    System.out.println(GREEN + "help [command]" + RESET);
                break;
                case "exit":
                case "quit":
                case ":wq":
                case ":q!":
                    System.out.println("\nUse this command to quit the program.");
                    System.out.println("Usage:");
                    System.out.println(GREEN + "quit" + RESET);
                break;
                case "restockcontents":
                    System.out.println("\nUse this command restock an item.");
                    System.out.println("Usage:");
                    System.out.println("restockcontents <slot name> <restock count>\n");
                break;
                case "addproduct":
                    System.out.println("\nUse this command add a new an item.");
                    System.out.println("Usage:");
                    System.out.println("addproduct <slot name> <product name> <product price> <product category> <product stock>\n");
                break;
                case "cashcheck":
                System.out.println("\nCASHIER USE ONLY: Returns the denominations fo coins currently in the machine");
                System.out.println("Usage:");
                System.out.println(GREEN + "cashremove" + RESET);
                break;
                case "cashadd":
                System.out.println("\nCASHIER USE ONLY: Use this command to add money to the machine");
                System.out.println("Usage:");
                System.out.println(GREEN + "cashadd [num] [denomination]" + RESET);
                break;
                case "cashremove":
                    System.out.println("\nCASHIER USE ONLY: Use this command to remove money from the machine");
                    System.out.println("Usage:");
                    System.out.println(GREEN + "cashremove [num] [denomination]" + RESET);
                break;
                default:
                    System.out.println(String.format(RED + "\nUnrecognised command: %s", inputs.get(1)) + RESET);
                break;
            }
        } 
    }

    private static void cashCheck(VendingMachine vm){
        HashMap<String, Integer> currencyCounts = vm.getCurrencyCounts();
        System.out.println(GREEN + "Currency : Number" + RESET);
        String denomArr[] = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
        for(String currency : denomArr){
            System.out.println(currency + ":" + currencyCounts.get(currency));
        }
    }

    private static void listTransactions(VendingMachine vm) {
        System.out.println(GREEN + "\nTransaction History:" + RESET);
        for (Transaction transaction : vm.getTransactions()) {
            System.out.println(transaction.toOutput());
        }
        System.out.println();
    }

    private static void cashAdd(VendingMachine vm, ArrayList<String> inputs){

        if(inputs.size() < 2){
            System.out.println("Incorrect number of parameters. Use \"help cashadd\" for more information.");
            return;
        }

        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));

        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                System.out.println("\nUnrecognisable denomination.\nPlease use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.\n");
                return;
            }
                            
            int amt = -1;
            
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                System.out.println("\nPlease ensure the cash amount is a positive integer.\n");
                return;
            } 
            
            if (amt <= 0) {
                System.out.println("\nPlease ensure the cash amount is a positive integer.\n");
                return;
            }
            
            if (parseDenom(values[1]) == -1) return;

            vm.addCurrencyCount(values[1], amt);
        }

        System.out.println("Currency successful added");
    }

    private static void cashRemove(VendingMachine vm, ArrayList<String> inputs) {

        if(inputs.size() < 2){
            System.out.println("Incorrect number of parameters. Use \"help cashremove\" for more information.");
            return;
        }

        ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(1, inputs.size()));

        for (String s : inputDenoms) {

            String[] values = s.split("\\*");

            String[] currencyValues = new String[] {"5c","10c","20c","50c","$1","$2","$5","$10","$20","$50","$100"};
            ArrayList<String> denomSet = new ArrayList<String>(Arrays.asList(currencyValues));
            if (values.length != 2 || !(denomSet.contains(values[1]))) {
                System.out.println("\nUnrecognisable denomination.\nPlease use the format <amount>*<value>, where value can be 50c, $2, $5 etc. and amount is a positive integer.\n");
                return;
            }
                            
            int amt = -1;
            
            try {
                amt = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                System.out.println("\nPlease ensure the cash amount is a positive integer.\n");
                return;
            } 
            
            if (amt <= 0) {
                System.out.println("\nPlease ensure the cash amount is a positive integer.\n");
                return;
            }
            
            if (parseDenom(values[1]) == -1) return;

            vm.addCurrencyCount(values[1], amt);
        }
        System.out.println(GREEN + "Currency successful removed" + RESET);

    }

    private static void endProgram(VendingMachine vm) {
        vm.writeToFile(saveFilePath);
        System.out.println(YELLOW + "Quitting..." + RESET);
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
           
            System.out.println(RED + e + RESET);
            }
        }
        System.out.print("\033\143");
        System.out.flush();
        for (String str : introBanner) {
            System.out.print(str);
        }
        System.out.println(GREEN + "Welcome to Sammy's Snackies!" + RESET);
        helpCommand(null);
        System.out.print("\n> ");
        return vm;
    }

    private static void unknownCommand(ArrayList<String> inputs) {
        System.out.println(RED + "\nUnknown Command, use the help command to see available commands" + RESET);
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
                String[] userInput = input.split(" ");
                ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
                String cmd = inputs.get(0);
                
                switch(cmd.toLowerCase()) {
    
                    case "buyer":
                    case "buy":
                        buyer(inputs, vm);
                    break;
                    case "cashier":
                        cashier(inputs);
                    break;
                    case "adduser":
                    if(currentType != UserType.OWNER){
                        unknownCommand(inputs);
                    }
                    else{
                        addUser(inputs);
                    }
                    break;
                    case "removeuser":
                        if(currentType != UserType.OWNER){
                            unknownCommand(inputs);
                        }
                        else{
                            removeUser(inputs);
                        }
                        break;
                    case "listtransactions":
                        if(currentType != UserType.OWNER){
                            unknownCommand(inputs);
                        }
                        else{
                            listTransactions(vm);
                        }
                    break;
                    case "login":
                        userLogin(inputs);
                    break;
                    case "restockcontents":
                        if (currentType != UserType.SELLER){
                            unknownCommand(inputs);

                        } else {
                            restockProduct(inputs, vm);
                        }
                    break;
                    case "addproduct":
                        if (currentType != UserType.SELLER){
                            unknownCommand(inputs);

                        } else {
                            addProduct(inputs, vm);
                        }
                    break;
                    case "products":
                    case "product":
                        products(vm);
                    break;
                    case "help":
                        helpCommand(inputs);
                    break;
                    case "cashcheck":
                        if(currentType != UserType.CASHIER){
                            unknownCommand(inputs);
                        }
                        else{
                            cashCheck(vm);
                        }
                    break;
                    case "cashadd":
                        if(currentType != UserType.CASHIER){
                            unknownCommand(inputs);
                            break;
                        }
                        cashAdd(vm, inputs);
                    break;
                    case "cashremove":
                        if(currentType != UserType.CASHIER){
                            unknownCommand(inputs);
                            break;
                        }
                        cashRemove(vm, inputs);
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
