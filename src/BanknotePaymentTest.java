import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
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

    /**
    *
    * Method: validBanknoteDetected(BanknoteValidator validator, Currency currency, int value)
    *
    */
    @Test
    public void testValidBanknote() throws Exception {
        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ControlSoftware cs = new ControlSoftware();
            BanknoteSlotController slotController = new BanknoteSlotController();
            BanknoteValidatorController validatorController = new BanknoteValidatorController(cs);
            BanknotesStorageUnitController storageController = new BanknotesStorageUnitController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.banknoteInput.register(slotController);
            station.banknoteValidator.register(validatorController);
            station.banknoteStorage.register(storageController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Banknote fiveDollars = new Banknote(5, currency);
            try {
                station.banknoteInput.accept(fiveDollars);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }
            try {
                assertEquals(cs.getTotal(), new BigDecimal("95.10"));
                assertEquals(station.banknoteStorage.getBanknoteCount(), 1);
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
    public void testInvalidBanknoteValue() throws Exception {
        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            ControlSoftware cs = new ControlSoftware();
            BanknoteSlotController slotController = new BanknoteSlotController();
            BanknoteValidatorController validatorController = new BanknoteValidatorController(cs);
            BanknotesStorageUnitController storageController = new BanknotesStorageUnitController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.banknoteInput.register(slotController);
            station.banknoteValidator.register(validatorController);
            station.banknoteStorage.register(storageController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Banknote invalidBanknote = new Banknote(11, currency);
            try {
                station.banknoteInput.accept(invalidBanknote);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }

            try {
                assertEquals(cs.getTotal(), new BigDecimal("100.10"));
                assertEquals(station.banknoteStorage.getBanknoteCount(), 0);
                assertEquals("An invalid banknote bill was inserted.", outContent.toString().split("\\r?\\n")[1]);
                assertEquals("A banknote was ejected.", outContent.toString().split("\\r?\\n")[2]);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 2) throw new AssertionError();
            }
            finally {
                System.setOut(originalOut);
            }
        }
    }

    @Test
    public void testInvalidBanknoteCurrency() throws Exception {
        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            ControlSoftware cs = new ControlSoftware();
            BanknoteSlotController slotController = new BanknoteSlotController();
            BanknoteValidatorController validatorController = new BanknoteValidatorController(cs);
            BanknotesStorageUnitController storageController = new BanknotesStorageUnitController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.banknoteInput.register(slotController);
            station.banknoteValidator.register(validatorController);
            station.banknoteStorage.register(storageController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Banknote invalidBanknote = new Banknote(11, Currency.getInstance("USD"));
            try {
                station.banknoteInput.accept(invalidBanknote);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }

            try {
                assertEquals(cs.getTotal(), new BigDecimal("100.10"));
                assertEquals(station.banknoteStorage.getBanknoteCount(), 0);
                assertEquals("An invalid banknote bill was inserted.", outContent.toString().split("\\r?\\n")[1]);
                assertEquals("A banknote was ejected.", outContent.toString().split("\\r?\\n")[2]);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 2) throw new AssertionError();
            }
            finally {
                System.setOut(originalOut);
            }
        }
    }

    @Test
    public void testRemoveDanglingNote() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        BanknoteSlotController slotController = new BanknoteSlotController();
        SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
        station.banknoteInput.register(slotController);
        Banknote note = new Banknote(5, currency);
        try {
            station.banknoteInput.emit(note);
        }
        catch (DisabledException | SimulationException e) {
            throw new AssertionError(e.toString());
        }
        assertEquals("A banknote was ejected.\r\n", outContent.toString());

        station.banknoteInput.removeDanglingBanknote();
        assertEquals("A banknote was removed.", outContent.toString().split("\\r?\\n")[1]);
    }
}
