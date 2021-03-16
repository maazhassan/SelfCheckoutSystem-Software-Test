import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ScannerControllerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final Currency currency = Currency.getInstance("CAD");
    private final int[] banknoteDenominations = {5, 10, 20, 50, 100};
    private final BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.10"),
            new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};


    @Test
    public void testEnabled() {
        System.setOut(new PrintStream(outContent));
        try {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            station.handheldScanner.register(controller);
            station.mainScanner.enable();
            assertEquals("A BarcodeScanner has been enabled.\n", outContent.toString());
            station.handheldScanner.enable();
            assertEquals("A BarcodeScanner has been enabled.\nA BarcodeScanner has been enabled.\n", outContent.toString());
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testDisabled() {
        System.setOut(new PrintStream(outContent));
        try {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            station.handheldScanner.register(controller);
            station.mainScanner.disable();
            assertEquals("A BarcodeScanner has been disabled.\n", outContent.toString());
            station.handheldScanner.disable();
            assertEquals("A BarcodeScanner has been disabled.\nA BarcodeScanner has been disabled.\n", outContent.toString());
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testScanOneItem() {
        int failCounter = 0;
        for (int i = 0; i < 10; i++) {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            Barcode barcode = new Barcode("123456789");
            BarcodedItem item = new BarcodedItem(barcode, 1.0);
            station.mainScanner.scan(item);
            try{
                assertTrue(cs.getPurchaseList().containsKey(barcode));
                assertEquals(cs.getPurchaseList().size(), 1);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 4) throw new AssertionError();
            }
        }
    }

    @Test
    public void testScanThreeItems() {
        int failCounter = 0;
        for (int i = 0; i < 50; i++) {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            Barcode barcode1 = new Barcode("123456789");
            Barcode barcode2 = new Barcode("987654321");
            Barcode barcode3 = new Barcode("222222222");
            BarcodedItem item1 = new BarcodedItem(barcode1, 1.0);
            BarcodedItem item2 = new BarcodedItem(barcode2, 2.0);
            BarcodedItem item3 = new BarcodedItem(barcode3, 3.0);
            station.mainScanner.scan(item1);
            station.mainScanner.scan(item2);
            station.mainScanner.scan(item3);
            try {
                assertTrue(cs.getPurchaseList().containsKey(barcode1));
                assertTrue(cs.getPurchaseList().containsKey(barcode2));
                assertTrue(cs.getPurchaseList().containsKey(barcode3));
                assertEquals(cs.getPurchaseList().size(), 3);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 25) throw new AssertionError();
            }
        }
    }

    @Test
    public void testScanOneItemTwice() {
        int failCounter = 0;
        for (int i = 0; i < 20; i++) {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            Barcode barcode = new Barcode("12345678");
            BarcodedItem item = new BarcodedItem(barcode, 1.0);
            station.mainScanner.scan(item);
            station.mainScanner.scan(item);
            try {
                assertTrue(cs.getPurchaseList().containsKey(barcode));
                assertEquals(cs.getPurchaseList().size(), 1);
                assertEquals(cs.getPurchaseList().get(barcode), (Integer) 2);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 10) throw new AssertionError();
            }
        }
    }

    @Test
    public void testBothScanners() {
        int failCounter = 0;
        for (int i = 0; i < 20; i++) {
            ControlSoftware cs = new ControlSoftware();
            ScannerController controller = new ScannerController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.mainScanner.register(controller);
            station.handheldScanner.register(controller);
            Barcode barcode1 = new Barcode("123456789");
            Barcode barcode2 = new Barcode("987654321");
            BarcodedItem item1 = new BarcodedItem(barcode1, 1.0);
            BarcodedItem item2 = new BarcodedItem(barcode2, 2.0);
            station.mainScanner.scan(item1);
            station.handheldScanner.scan(item2);
            try {
                assertTrue(cs.getPurchaseList().containsKey(barcode1));
                assertTrue(cs.getPurchaseList().containsKey(barcode2));
                assertEquals(cs.getPurchaseList().size(), 2);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 10) throw new AssertionError();
            }
        }
    }
}
