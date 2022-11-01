package Sammys.Snackies;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VendingMachine {
    private HashMap<String, Slot> allSlots;
    private HashMap<String, Integer> currencyCounts;
    private final String[] currencyNames = {"5c", "10c", "20c", "50c", "$1", "$2", "$5", "$10", "$20", "$50", "$100"};
    private final double[] currencyValues = {0.05, 0.10, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100};
    
    // TODO
    // private final String fp = "data.json"; // unsued, if needed again, just uncomment this
    
    private Integer currentTransactionID = 0;
    private ArrayList<Transaction> transactions;

    public VendingMachine(){
        this.allSlots = new HashMap<String, Slot>();
        this.currencyCounts = new HashMap<String, Integer>();
        for (String currency : currencyNames){
            this.currencyCounts.put(currency, 5);
        }
        this.transactions = new ArrayList<Transaction>();
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public boolean isInMachine(String itemName){
        for (Slot slot : allSlots.values()){
            if (slot.getContents().getName().equals(itemName)){
                return true;
            }
        }
        return false;
    }
    
    public void addTransaction(String paymentMethod, FoodItem productBought, Integer qty) {
        transactions.add(new Transaction(currentTransactionID++, paymentMethod, productBought, qty));
    }

    public void addSlot(String slotName, FoodItem slotContents, int contentCount){
        this.allSlots.put(slotName, new Slot(slotName, slotContents, contentCount));
    }

    public void addCurrencyCount(String currencyName, int currencyCount) throws NoSuchElementException{
        boolean currencyFound = false;
        for (String name : this.currencyNames){
            if (currencyName.equals(name)){
                currencyFound = true;
                break;
            }
        }
        if (!currencyFound){
            throw new NoSuchElementException("Incorrect denomination parsed! (" + currencyName + ")");
        }
        this.currencyCounts.put(currencyName, currencyCount + this.currencyCounts.get(currencyName));

    }

    public void removeCurrencyCount(String currencyName, int currencyCount) throws NoSuchElementException{
        boolean currencyFound = false;
        for (String name : this.currencyNames){
            if (currencyName.equals(name)){
                currencyFound = true;
                break;
            }
        }
        if (!currencyFound){
            throw new NoSuchElementException("Incorrect denomination parsed! (" + currencyName + ")");
        }
        if(this.currencyCounts.get(currencyName) < currencyCount){
            throw new NoSuchElementException("Not enough of this denomination in the machine");
        }
        this.currencyCounts.put(currencyName, this.currencyCounts.get(currencyName) - currencyCount);
        
    }

    public HashMap<String, Integer> getCurrencyCounts() {
        return this.currencyCounts;
    }

    public HashMap<String, Slot> getSlots() {
        return allSlots;
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        for (Slot currentSlot : this.allSlots.values()){
            output.append(currentSlot.getName() + " " + currentSlot.getContents() + "\n");
        }
        return output.toString();
    }

    public int getContentCount(String slotName) throws NoSuchElementException{
        if (this.allSlots.containsKey(slotName)){
            return this.allSlots.get(slotName).getCount();
        }

        throw new NoSuchElementException("Can not find a slot " + slotName);
    }

    public void readFromFile(String fPath) {

        this.allSlots = new HashMap<String, Slot>();
        JSONParser parser = new JSONParser();

        try (FileReader fr = new FileReader(fPath)) {
            Object obj = parser.parse(fr);
            JSONArray jsonData = (JSONArray) obj;

            JSONObject currencies = (JSONObject) jsonData.remove(0);

            for (String key : currencyNames) {
                this.currencyCounts.put(key, ((Long)(currencies.get(key))).intValue());
            }

            JSONObject transactionData = (JSONObject) jsonData.remove(0);

            for (Object value : jsonData) {
                JSONObject jObj = (JSONObject) value;
                String slotName = (String) jObj.get("slotName");
                String itemName = (String) jObj.get("itemName");
                float itemPrice = ((Double) jObj.get("itemPrice")).floatValue();
                Category itemCategory = Category.valueOf((String) jObj.get("itemCategory"));
                int slotCount = ((Long) jObj.get("slotCount")).intValue();

                allSlots.put(slotName, new Slot(slotName, new FoodItem(itemName, itemPrice, itemCategory), slotCount));
            }

            for (Object key : transactionData.keySet()) {
                this.currentTransactionID = Integer.valueOf((String) key) + 1;
                List<String> l = Arrays.asList(((String) transactionData.get((String) key)).split("\\s*,\\s*"));

                FoodItem foodItem = null;
                for (Slot s : allSlots.values()) {
                    if (s.getContents().getName().toLowerCase().equals(l.get(2).toLowerCase())) {
                        foodItem = s.getContents();
                    }
                }
                if (foodItem == null) {
                    System.out.println("Error loading product from file. Please check .json file itegrity.\n\n");
                    continue;
                }
                this.transactions.add(new Transaction(Integer.valueOf(l.get(0)), l.get(1), foodItem, Integer.valueOf(l.get(3))));
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String fPath) {

        ArrayList<HashMap<String, Object>> jsonData = new ArrayList<>();
        
        HashMap<String, Object> currencyData = new HashMap<String, Object>();
        for (String key : currencyCounts.keySet()) {
            currencyData.put(key, (Object) currencyCounts.get(key));
        }
        jsonData.add(currencyData);

        HashMap<String, Object> transactionData = new HashMap<String, Object>();
        for (Transaction transaction : transactions) {
            transactionData.put(String.valueOf(transaction.getID()), transaction.toString());
        }
        jsonData.add(transactionData);

        for (Slot slot : allSlots.values()) {
            HashMap<String, Object> slotData = new HashMap<String, Object>();
            slotData.put("slotName", slot.getName());
            slotData.put("itemName", slot.getContents().getName());
            slotData.put("itemPrice", slot.getContents().getPrice());
            slotData.put("itemCategory", slot.getContents().getCategory().toString());
            slotData.put("slotCount", slot.getCount());
            jsonData.add(slotData);
        }
        
        // Attempts to write the JSONArray to file
        try (FileWriter fw = new FileWriter(fPath)) {
            org.json.simple.JSONArray.writeJSONString(jsonData, fw);
            fw.flush();
            fw.close();
        } catch(IOException e) {
            System.out.println("Failed to write to file");
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> getChangeFromCash(double totalGiven) throws IndexOutOfBoundsException{
        double currentTotal = totalGiven;
        int currentCurrency = currencyNames.length-1;
        HashMap<String, Integer> retval = new HashMap<>();
        while (currentCurrency >= 0){
            double currentValue = currencyValues[currentCurrency];
            int currentValueCount = (int)(currentTotal / currentValue);
            if (currentValueCount >= this.currencyCounts.get(currencyNames[currentCurrency])){
                currentTotal = (currentTotal % currentValue);
            } else {
                currentTotal = currentTotal % currentValue;
            }
            retval.put(currencyNames[currentCurrency], currentValueCount);
            currentCurrency--;
        }
        if (currentTotal <= 0.04){
            return retval;
        }
        throw new IndexOutOfBoundsException("The vending machine does not have sufficient change");
    }
}
