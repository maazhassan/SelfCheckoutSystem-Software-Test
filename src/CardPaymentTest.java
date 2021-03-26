import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import static org.junit.Assert.*;

public class CardPaymentTest {
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
    public void testDebitTap() {
        int failCounter = 0;
        for (int i = 0; i < 20; i++) {
            ControlSoftware cs = new ControlSoftware();
            CardReaderController readerController = new CardReaderController(cs);
            SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
            station.cardReader.register(readerController);
            Barcode barcode = new Barcode("123456789");
            cs.increaseTotal(barcode);

            CardIssuer issuer = new CardIssuer("TestIssuer");
            cs.registerCardIssuer(issuer);
            Card debitCard = new Card("Debit", "12345", "TestHolder", "123", "1234", true, true);
            Calendar expiry = Calendar.getInstance();
            expiry.set(Calendar.YEAR, 2023);
            issuer.addCardData("12345", "TestHolder", expiry, "123", new BigDecimal("1000"));

            try {
                station.cardReader.tap(debitCard);
            }
            catch (IOException e) {
                System.out.println("There was an error! Please try again.");
            }

            try {
                assertEquals(0, cs.getTotal().compareTo(BigDecimal.ZERO));
            }
            catch (AssertionError ae) {
                failCounter++;
                if (failCounter > 10) throw new AssertionError(ae.toString());
            }
        }
    }
}
