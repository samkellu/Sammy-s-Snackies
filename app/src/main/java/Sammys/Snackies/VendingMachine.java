package Sammys.Snackies;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VendingMachine {
    private HashMap<String, Slot> allSlots;
    private final String fp = "data.json";

    public VendingMachine(){
        this.allSlots = new HashMap<String, Slot>();
    }

    public void addSlot(String slotName, FoodItem slotContents, int contentCount){
        this.allSlots.put(slotName, new Slot(slotName, slotContents, contentCount));
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

            for (Object value : jsonData) {
                JSONObject jObj = (JSONObject) value;
                String slotName = (String) jObj.get("slotName");
                String itemName = (String) jObj.get("itemName");
                float itemPrice = ((Double) jObj.get("itemPrice")).floatValue();
                Category itemCategory = Category.valueOf((String) jObj.get("itemCategory"));
                int slotCount = ((Long) jObj.get("slotCount")).intValue();

                this.allSlots.put(slotName, new Slot(slotName, new FoodItem(itemName, itemPrice, itemCategory), slotCount));
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String fPath) {

        ArrayList<HashMap<String, Object>> jsonData = new ArrayList<>();
        
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
}