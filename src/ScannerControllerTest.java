import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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


    @Before
    public void setUpStream() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
    }

    @Test
    public void testEnabled() {
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

    @Test
    public void testDisabled() {
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

    @Test
    public void testMainScanner() {
        ControlSoftware cs = new ControlSoftware();
        ScannerController controller = new ScannerController(cs);
        SelfCheckoutStation station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 100, 1);
        station.mainScanner.register(controller);

    }
}
