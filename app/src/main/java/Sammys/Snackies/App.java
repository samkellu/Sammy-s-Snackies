package Sammys.Snackies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;

public class App {

    private final String saveFilePath = "saveFile.json";
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
                System.out.println("Please enter a valid monetary denomination.");
                return -1;
        }
    }

    private static HashMap<String, Integer> getDenominations(double totalGiven){
        HashMap<String, Integer> denominations = new HashMap<String, Integer>();
        denominations.put("5c", 0);
        denominations.put("10c", 0);
        denominations.put("20c", 0);
        denominations.put("50c", 0);
        denominations.put("$1", 0);
        denominations.put("$2", 0);
        denominations.put("$5", 0);
        denominations.put("$10", 0);
        denominations.put("$20", 0);
        denominations.put("$50", 0);
        denominations.put("$100", 0);

        while( totalGiven >= 100){
            totalGiven =- 100;
            denominations.put("$100", denominations.get("$100")+ 1);
        }
        while( totalGiven >= 50){
            totalGiven =- 50;
            denominations.put("$50", denominations.get("$50")+ 1);
        }
        while( totalGiven >= 20){
            totalGiven =- 20;
            denominations.put("$20", denominations.get("$20")+ 1);
        }
        while( totalGiven >= 10){
            totalGiven =- 10;
            denominations.put("$10", denominations.get("$10")+ 1);
        }
        while( totalGiven >= 5){
            totalGiven =- 5;
            denominations.put("$5", denominations.get("$5")+ 1);
        }
        while( totalGiven >= 2){
            totalGiven =- 2;
            denominations.put("$2", denominations.get("$2")+ 1);
        }
        while( totalGiven >= 1){
            totalGiven =- 1;
            denominations.put("$1", denominations.get("$1")+ 1);
        }
        while( totalGiven >= 0.50){
            totalGiven =- 0.50;
            denominations.put("50c", denominations.get("50c")+ 1);
        }
        while( totalGiven >= 0.20){
            totalGiven =- 0.20;
            denominations.put("20c", denominations.get("20c")+ 1);
        }
        while( totalGiven >= 0.10){
            totalGiven =- 0.10;
            denominations.put("10c", denominations.get("10c")+ 1);
        }
        while( totalGiven >= 0.05){
            totalGiven =- 0.05;
            denominations.put("5c", denominations.get("5c")+ 1);
        }

        return denominations;
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
        if (noProducts) System.out.println("\nSorry, there are no products available in this machine.");
        System.out.print("\n");
    }

    private static void buyer(ArrayList<String> inputs, VendingMachine vm) {


        // ensure enough arguments
        if (inputs.size() < 4) {
            System.out.println("Not enough arguments. Use \"help buyer\" to see required arguments.\n");
            return;
        }

        // check cash or card
        boolean cash = false;
        if (inputs.get(1).toLowerCase().equals("cash"))
            cash = true;
            // ensure enough arguments for cash payment
            if (inputs.size() < 4) {
                System.out.println("Not enough arguments. Use \"help buyer\" to see required arguments.\n");
                return;
            }
        else if (!inputs.get(1).toLowerCase().equals("card")) {
            System.out.println("Please specify payment type (cash or card).\n");
            return;
        }

        // check product code exists
        Slot slot = null;
        boolean validProduct = false;
        // checks all slots in the machine for a matching product code
        for (Slot s : vm.getSlots().values()) {
            if (inputs.get(2).toLowerCase().equals(s.getContents().getName().toLowerCase())) {
                slot = s;
                break;
            }
        }

        if (slot == null) {
            System.out.println("Please enter a valid product code. This machine contains no item with code: " + inputs.get(2) + "\n");
            return;
        }

        // check the machine has sufficient quantity 
        if (slot.getCount() < Integer.valueOf(inputs.get(3))) {
            if (slot.getCount() == 0){
                System.out.println("Unfortunately, this machine is all out of stock of " + slot.getContents());
            } else {
                System.out.println("Unfortunately, this machine only has " + slot.getCount() + "x " + slot.getContents().toString() + " available.\n");
            }
            return;
        } else if (Integer.valueOf(inputs.get(3)) <= 0) {
            System.out.println("Please enter at least one for quantity.\n");
            return;
        }


        // TODO
        // if cash, check denominations
        // get totalOwed
        
        int[] denoms = new int[11];
        double totalGiven = 0.00;
        if (cash) {
            ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(4, inputs.size()-1));

            for (String s : inputDenoms) {
                String[] values = s.split("*");

                if (values.length != 2) {
                    System.out.println("Unrecognisable denomination.\nPlease use the format <value>*<amount>, where value can be 50c, $2, $5 etc. and amount is a positive integer.\n");
                    return;
                }
                
                String v = values[0];
                String amount = values[1];
                int amt = -1;
                
                try {
                    amt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    System.out.println("Please ensure the amount is a positive integer.\n");
                    return;
                } finally {
                    if (amt <= 0) {
                        System.out.println("Please ensure the amount is a positive integer.\n");
                        return;
                    }
                }

                double value = parseDenom(v);
                if (value == -1) return;

                totalGiven += value*amt;

                //Returns the denominations in a hashmap of the change
                HashMap<String, Integer> ChangeDenominations = getDenominations(totalGiven);


                // TODO: need to change change in the machine
            }
        }

        System.out.println("You recieved " + inputs.get(3) + "x " + slot.getContents().toString() + ".\nYou paid $" + slot.getContents().getPrice() * Integer.valueOf(inputs.get(3)) + ".\nThank you for shopping at Sammy's Snackies!\n");
        slot.sellContents(Integer.valueOf(inputs.get(3)));
        // TODO
        // ensure enough money given, need the vending machine to know the price.
    }

    private static void seller(ArrayList<String> inputs) {
        if (currentType != UserType.SELLER){
            System.out.println("You are unauthorised!! Seller role is required, please log in.");
            return;
        }

    }

    private static void cashier(ArrayList<String> inputs) {
        if (currentType != UserType.CASHIER){
            System.out.println("You are unauthorised!! Cashier role is required, please log in.");
            return;
        }

    }

    private static void owner(ArrayList<String> inputs) {
        if (currentType != UserType.OWNER){
            System.out.println("You are unauthorised!! Owner role is required, please log in.");
            return;
        }

        if (inputs.get(1).equals("add")){
            if (inputs.size() != 5){
                System.out.println("Incorrect number of parameters. Use \"help owner add\" for more information.");
                return;
            }
            String username = inputs.get(2);
            for (UserLogin user : userLogins){
                if (user.getUsername().equals(username)){
                    System.out.println("User already exists, please choose a unique username");
                    return;
                }
            }
            String password = inputs.get(3);
            UserType type = UserType.fromName(inputs.get(4).toLowerCase());
            UserLogin user = new UserLogin(username, password, type);
            userLogins.add(user);
            UserLogin.writeUsersToFile(userLoginFilepath, userLogins);
            System.out.println("New user added with username " + username + " with role of " + type);

        }
    }

    private static void userLogin(ArrayList<String> inputs) {

        if (inputs.size() != 3) {
            System.out.println("Incorrect number of parameters. Use \"help login\" for more information.");
            return;
        }

        for (UserLogin user : userLogins){
            if (user.verifyLogin(inputs.get(1), inputs.get(2))){
                System.out.println("Welcome, " + user.getUsername());
                currentType = user.getType();
                System.out.println("You are now logged in as a " + user.getType());
                return;
            }
        }

        System.out.println("Login not found, try again");



        // TODO
        // check login, maybe we use a file of users and pwds?
    }

    private static void helpCommand(ArrayList<String> inputs) {
         if (inputs==null || inputs.size() == 1) {
            System.out.println("\nAvailable Commands:");
            System.out.println("buyer - buy a product");
            System.out.println("seller - TODO"); // TODO
            System.out.println("owner - TODO"); // TODO
            System.out.println("cashier - TODO"); // TODO
            System.out.println("products - list available products in the vending machine");
            System.out.println("login - login to a cashier/owner/seller account");
            System.out.println("help - display this screen");
            System.out.println("quit - quit the program\n");

        } else if (inputs.size() >= 2) {

            switch(inputs.get(1).toLowerCase()) {
                case "buyer":
                    System.out.println("\nUse this command to buy a product from the vending machine.");
                    System.out.println("Usage:");
                    System.out.println("buyer <cash/card> <product> <amount> [denominations...]\n");
                break;
                case "seller":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("seller TODO\n");
                break;
                case "owner":
                    if (inputs.size()>2 && inputs.get(2).toLowerCase().equals("add")){
                        System.out.println("\n Use this command to add a new user to the list of logins");
                        System.out.println("Useage: ");
                        System.out.println("owner add <username> <password> <user type>");
                        break;
                    }
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("owner TODO\n");
                break;
                case "cashier":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("cashier TODO\n");
                break;
                case "login":
                    System.out.println("\nUse this command to log in to a cashier/owner/seller account.");
                    System.out.println("Usage:");
                    System.out.println("login <username> <password>\n");
                break;
                case "products":
                case "product":
                System.out.println("\nUse this command to list all products in the vending machine.");
                System.out.println("Usage:");
                System.out.println("products\n");
                break;
                case "help":
                    System.out.println("\nUse this command to see available commands or for more information on a command");
                    System.out.println("Usage:");
                    System.out.println("help [command]\n");
                break;
                case "quit":
                    System.out.println("\nUse this command to quit the program.");
                    System.out.println("Usage:");
                    System.out.println("quit\n");
                break;
                default:
                    System.out.println(String.format("\nUnrecognised command: %s\n", inputs.get(1)));
                break;
            }
        } 
    }

    private static void endProgram(VendingMachine vm) {
        // UNCOMMENT WHEN YOU WANT TO SAVE EVERY QUIT
        // vm.writeToFile(saveFilePath);
        System.out.println("Quitting...");
    }

    private static VendingMachine initProgram() {
        System.out.println("System Starting...");
        VendingMachine vm = new VendingMachine();
        // UNCOMMENT WHEN YOU WANT TO LOAD EVERY START
        // vm.readFromFile(saveFilePath);
        System.out.println("Welcome to Sammy's Snackies!");
        helpCommand(null);
        return vm;
    }

    private static void unknownCommand(ArrayList<String> inputs) {
        System.out.println("Unknown Command, use the help command to see available commands");
    }

    public static void main(String[] args) {
        
        Scanner s = new Scanner(System.in);
        FoodItem f = new FoodItem("water", 1.50, Category.DRINK);

        VendingMachine vm = initProgram();
        vm.addSlot("A1", f, 5);

        userLogins = UserLogin.readFromFile(userLoginFilepath);

        while (true){
            System.out.print("> ");
            while(s.hasNextLine()){
                String input = s.nextLine();
                String[] userInput = input.split(" ");
                ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
                String cmd = inputs.get(0);
    
                switch(cmd.toLowerCase()) {
    
                    case "buyer":
                        buyer(inputs, vm);
                    break;
                    case "seller":
                        seller(inputs);
                    break;
                    case "cashier":
                        cashier(inputs);
                    break;
                    case "owner":
                        owner(inputs);
                    break;
                    case "login":
                        userLogin(inputs);
                    break;
                    case "products":
                        products(vm);
                    break;
                    case "help":
                        helpCommand(inputs);
                    break;
                    case "quit":
                        endProgram(vm);
                        s.close();
                        return;
                    default:
                        unknownCommand(inputs);
                    break;
                }
                System.out.print("> ");
            }
        }
    }
}
