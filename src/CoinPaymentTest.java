import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class CoinPaymentTest {
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

    @Test
    public void testValidCoin() {
        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ControlSoftware cs = new ControlSoftware();
            CoinSlotController slotController = new CoinSlotController();
            CoinValidatorController validatorController = new CoinValidatorController(cs);
            CoinStorageController storageController = new CoinStorageController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.coinSlot.register(slotController);
            station.coinValidator.register(validatorController);
            station.coinStorage.register(storageController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Coin toonie = new Coin(new BigDecimal("2.00"), currency);
            try {
                station.coinSlot.accept(toonie);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }
            try {
                assertEquals(cs.getTotal(), new BigDecimal("98.10"));
                assertEquals(station.coinDispensers.get(new BigDecimal("2.00")).size(), 1);
                assertEquals(station.coinDispensers.get(new BigDecimal("1.00")).size(), 0);
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 2) throw new AssertionError();
            }
        }
    }

    @Test
    public void testInvalidValue() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ControlSoftware cs = new ControlSoftware();
            CoinSlotController slotController = new CoinSlotController();
            CoinValidatorController validatorController = new CoinValidatorController(cs);
            CoinStorageController storageController = new CoinStorageController();
            CoinTrayController trayController = new CoinTrayController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.coinSlot.register(slotController);
            station.coinValidator.register(validatorController);
            station.coinStorage.register(storageController);
            station.coinTray.register(trayController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Coin invalidCoin = new Coin(new BigDecimal("3.50"), currency);
            try {
                station.coinSlot.accept(invalidCoin);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }

            try {
                assertEquals(cs.getTotal(), new BigDecimal("100.10"));
                assertEquals(station.coinStorage.getCoinCount(), 0);
                assertEquals("A coin was added to the coin tray.", outContent.toString().split("\\r?\\n")[2]);
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
    public void testInvalidCurrency() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        int failCounter = 0;
        for (int i = 0; i < 5; i++) {
            ControlSoftware cs = new ControlSoftware();
            CoinSlotController slotController = new CoinSlotController();
            CoinValidatorController validatorController = new CoinValidatorController(cs);
            CoinStorageController storageController = new CoinStorageController();
            CoinTrayController trayController = new CoinTrayController();
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.coinSlot.register(slotController);
            station.coinValidator.register(validatorController);
            station.coinStorage.register(storageController);
            station.coinTray.register(trayController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);
            Coin invalidCoin = new Coin(new BigDecimal("2.00"), Currency.getInstance("USD"));
            try {
                station.coinSlot.accept(invalidCoin);
            }
            catch (DisabledException de) {
                System.out.println("Disabled.");
            }

            try {
                assertEquals(cs.getTotal(), new BigDecimal("100.10"));
                assertEquals(station.coinStorage.getCoinCount(), 0);
                assertEquals("A coin was added to the coin tray.", outContent.toString().split("\\r?\\n")[2]);
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
}
