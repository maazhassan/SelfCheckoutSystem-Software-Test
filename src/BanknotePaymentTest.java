import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

/**
* BanknoteValidatorController Tester. 
* 
* @author <Authors Irtaza>
* @since <pre>Mar. 17, 2021</pre> 
* @version 1.0 
*/ 

public class BanknotePaymentTest {
    private final Currency currency = Currency.getInstance("CAD");
    private final int[] banknoteDenominations = {5, 10, 20, 50, 100};
    private final BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.10"),
            new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};

    @Before
    public void addToDatabase() {
        Barcode barcode = new Barcode("123456789");
        BarcodedProduct product = new BarcodedProduct(barcode, "A generic product.", new BigDecimal("100.10"));
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
    }


    @After
    public void clearDatabase() {
        ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
    }

    }
/**
* 
* Method: validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) 
* 
*/

@Test
public void testValidBanknoteDetected() throws Exception {
    int failCounter = 0;
    for (int i = 0; i < 5; i++) {
        BanknoteSlotController slotController = new BanknoteSlotController();
        BanknoteValidatorController validatorController = new BanknoteValidatorController(cs);
        BanknoteStorageController storageController = new BanknoteStorageController();
        SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
        station.BanknoteSlot.register(slotController);
        station.BanknoteValidator.register(validatorController);
        station.BanknoteStorage.register(storageController);
        Barcode barcode = new Barcode("123456789");
        cs.increaseTotal(barcode);
        Banknote fiveDollars = new Banknote(new BigDecimal("5.00"), currency);
        try {
            station.BanknoteSlot.accept(fiveDollars);
        }
        catch (DisabledException de) {
            System.out.println("Disabled.");
        }
        try {
            assertEquals(getTotal(), new BigDecimal("98.10"));
            assertEquals(station.banknoteStorage.getCoinCount(), 1);
        }
        catch (AssertionError ae) {
            failCounter++;
            if (failCounter > 2) throw new AssertionError();
        }
    }
}

/** 
* 
* Method: invalidBanknoteDetected(BanknoteValidator validator) 
* 
*/ 
@Test
public void testInvalidBanknoteDetected() throws Exception {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    int failCounter = 0;
    for (int i = 0; i < 5; i++) {
        BanknoteSlotController slotController = new BanknoteSlotController();
        BanknoteValidatorController validatorController = new BanknoteValidatorController(cs);
        BanknoteStorageController storageController = new BanknoteStorageController();
        BanknoteTrayController trayController = new BanknoteTrayController();
        SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
        station.BanknoteSlot.register(slotController);
        station.BanknoteValidator.register(validatorController);
        station.BanknoteStorage.register(storageController);
        station.BanknoteTray.register(trayController);
        Barcode barcode = new Barcode("123456789");
        cs.increaseTotal(barcode);
        Banknote invalidBanknote = new Banknote(new BigDecimal("10.00"), currency);
        try {
            station.BanknoteSlot.accept(invalidBanknote);
        }
        catch (DisabledException de) {
            System.out.println("Disabled.");
        }

        try {
            assertEquals(cs.getTotal(), new BigDecimal("100.00"));
            assertEquals(station.BanknoteStorage.getCoinCount(), 0);
            assertEquals("A coin was added to the coin tray.", outContent.toString().split("\\r?\\n")[2]);
        }
        catch (AssertionError ae) {
            failCounter++;
            if (failCounter > 2) throw new AssertionError();
        }
        finally {
            System.setOut(originalOut);
        }
    }}

/** 
* 
* Method: enabled(AbstractDevice<? extends AbstractDeviceListener> device) 
* 
*/ 
}
