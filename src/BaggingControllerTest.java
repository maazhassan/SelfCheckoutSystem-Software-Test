import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Test;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class BaggingControllerTest {
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	public static final int testlimit = 30000;
	public static final int testsens = 5;
	private final Currency currency = Currency.getInstance("CAD");
	private final int[] banknoteDenominations = {5, 10, 20, 50, 100};
	private final BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.10"),
			new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};

	
	@Test
	public void testEnabled() {
		System.setOut(new PrintStream(outContent));
		ControlSoftware cs = new ControlSoftware();
		SelfCheckoutStation a = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, testlimit, testsens);
		BaggingController bc = new BaggingController(cs);
		a.baggingArea.register(bc);
		try {
			a.baggingArea.enable();
			assertEquals("A ElectronicScale has been enabled.\n", outContent.toString());
		}
		finally {
			System.setOut(originalOut);
		}
	}
	
	@Test
	public void testDisabled() {
		System.setOut(new PrintStream(outContent));
		ControlSoftware cs = new ControlSoftware();
		SelfCheckoutStation a = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, testlimit, testsens);
		BaggingController bc = new BaggingController(cs);
		a.baggingArea.register(bc);
		try {
			a.baggingArea.disable();
			assertEquals("A ElectronicScale has been disabled.\n", outContent.toString());
		}
		finally {
			System.setOut(originalOut);
		}
	}
	
	@Test
	public void testAddItem()  {
		ControlSoftware cs = new ControlSoftware();
		SelfCheckoutStation a = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, testlimit, testsens);
		BaggingController bc = new BaggingController(cs);
		Item aItem = new Item(60) {};
		a.baggingArea.register(bc);
		a.baggingArea.add(aItem);
		assertEquals(cs.getBaggingAreaWeight(), 60, 0.01);
	}
	
	@Test
	public void testAdd3()  {
		ControlSoftware cs = new ControlSoftware();
		SelfCheckoutStation a = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, testlimit, testsens);
		BaggingController bc = new BaggingController(cs);
		Item aItem = new Item(60) {};
		Item bItem = new Item(90) {};
		Item cItem = new Item(120) {};
		a.baggingArea.register(bc);
		a.baggingArea.add(aItem);
		a.baggingArea.add(bItem);
		a.baggingArea.add(cItem);
		assertEquals(cs.getBaggingAreaWeight(), 270, 0.01);
	}
	
	@Test
	public void testOverload() throws Exception {
		System.setOut(new PrintStream(outContent));
		try {
			ControlSoftware cs = new ControlSoftware();
			SelfCheckoutStation a = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, testlimit, testsens);
			BaggingController bc = new BaggingController(cs);
			Item aItem = new Item(testlimit+1) {};
			a.baggingArea.register(bc);
			a.baggingArea.add(aItem);
			assertEquals("Please remove item from scale.\n", outContent.toString());
		}
		finally {
			System.setOut(originalOut);
		}
	}
}
