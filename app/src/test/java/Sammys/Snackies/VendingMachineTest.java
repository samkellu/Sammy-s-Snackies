package Sammys.Snackies;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;

// Tests for the vending machine class and its functionalities
class VendingMachineTest {

    // Tests the file writing module by creating a machine, and making it write its state to file.
    @Test void writeToFile() {

        String fp = "testWrite.json";

        // Creates a new vending machine and populates its contents/ cash
        FoodItem marsBar = new FoodItem("Mars Bar", 1.02f, Category.CHOCOLATE);
        FoodItem sprite = new FoodItem("Sprite", 2.0f, Category.DRINK);
        FoodItem doritos = new FoodItem("Doritos", 221.0f, Category.CHIPS);

        VendingMachine vm = new VendingMachine();

        HashMap<String, Integer> currencyCounts = vm.getCurrencyCounts();

        currencyCounts.put("5c", 111);
        currencyCounts.put("10c", 222);
        currencyCounts.put("20c", 333);
        currencyCounts.put("50c", 444);
        currencyCounts.put("$1", 555);
        currencyCounts.put("$2", 666);
        currencyCounts.put("$5", 777);
        currencyCounts.put("$10", 888);
        currencyCounts.put("$20", 999);
        currencyCounts.put("$50", 1111);
        currencyCounts.put("$100", 2222);

        vm.addSlot("A1", marsBar, 9);
        vm.addSlot("A2", sprite, 200);
        vm.addSlot("A3", doritos, 55);

        // Attempts to write to file
        try {

            vm.writeToFile(fp);
        } catch (Exception e) {
            assertEquals(false, true, "Failed to write");
        }

        String control = "[{\"$5\":777,\"$20\":999,\"$10\":888,\"10c\":222,\"20c\":333,\"50c\":444,\"$100\":2222,\"$1\":555,\"5c\":111,\"$2\":666,\"$50\":1111},{\"slotName\":\"A1\",\"itemName\":\"Mars Bar\",\"itemCategory\":\"CHOCOLATE\",\"slotCount\":9,\"itemPrice\":1.0199999809265137},{\"slotName\":\"A2\",\"itemName\":\"Sprite\",\"itemCategory\":\"DRINK\",\"slotCount\":200,\"itemPrice\":2.0},{\"slotName\":\"A3\",\"itemName\":\"Doritos\",\"itemCategory\":\"CHIPS\",\"slotCount\":55,\"itemPrice\":221.0}]";

        // Checks actual file contents against the control string
        try (FileReader fr = new FileReader(fp)) {
            char buf[] = new char[control.length()];
            // Automatically mitigates buffer overflow so this defence is not necessary
            fr.read(buf, 0, control.length());
            assertEquals(control, String.valueOf(buf),"Written successfully");
        } catch(IOException e) {
            e.printStackTrace();
            assertEquals(false, true, "Failed to write");
        }

    }


    // Tests the file reading function by reading a set vending machine state from a file
    @Test void readFromFile() {

        // Writes the set vending machine state to file
        String fp = "testRead.json";
        String toWrite = "[{\"$5\":777,\"$20\":999,\"$10\":888,\"10c\":222,\"20c\":333,\"50c\":444,\"$100\":2222,\"$1\":555,\"5c\":111,\"$2\":666,\"$50\":1111},{\"slotName\":\"A1\",\"itemName\":\"Mars Bar\",\"itemCategory\":\"CHOCOLATE\",\"slotCount\":9,\"itemPrice\":1.02},{\"slotName\":\"A2\",\"itemName\":\"Sprite\",\"itemCategory\":\"DRINK\",\"slotCount\":200,\"itemPrice\":2.0},{\"slotName\":\"A3\",\"itemName\":\"Doritos\",\"itemCategory\":\"CHIPS\",\"slotCount\":55,\"itemPrice\":221.0}]";
        // Attempts to write the JSONArray to file
        try (FileWriter fw = new FileWriter(fp)) {
            fw.write(toWrite);
            fw.flush();
            fw.close();
        } catch(IOException e) {
            System.out.println("Failed to write to file");
            e.printStackTrace();
        }

        // Attempts to create the vending machine from the file
        VendingMachine vm = new VendingMachine();
        assertEquals("", vm.toString());

        vm.readFromFile(fp);

        // Confirms the contents of the machine are correct
        assertEquals("A1 Mars Bar $1.02\nA2 Sprite $2.00\nA3 Doritos $221.00\n", vm.toString());
        HashMap<String, Integer> currencyCounts = vm.getCurrencyCounts();
        assertEquals(111,currencyCounts.get("5c"));
        assertEquals(222,currencyCounts.get("10c"));
        assertEquals(333,currencyCounts.get("20c"));
        assertEquals(444,currencyCounts.get("50c"));
        assertEquals(555,currencyCounts.get("$1"));
        assertEquals(666,currencyCounts.get("$2"));
        assertEquals(777,currencyCounts.get("$5"));
        assertEquals(888,currencyCounts.get("$10"));
        assertEquals(999,currencyCounts.get("$20"));
        assertEquals(1111,currencyCounts.get("$50"));
        assertEquals(2222,currencyCounts.get("$100"));
    }
}