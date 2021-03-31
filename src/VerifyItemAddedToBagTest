import static org.junit.Assert.*;

import org.junit.Test;
import org.lsmr.selfcheckout.Item;

public class VerifyItemAddedToBagTest {

	@Test
	public void addItemTest() {
		ControlSoftware cs = new ControlSoftware();
		Item aItem = new Item(1234) {};
		cs.addItem(aItem);
		assertEquals (cs.verifyBagging(), true);
	}
	
	@Test
	public void addSameItemTest2() {
		ControlSoftware cs = new ControlSoftware();
		Item aItem = new Item(1234) {};
		Item bItem = new Item(1234) {};
		cs.addItem(aItem);
		cs.addItem(bItem);
		assertEquals (cs.verifyBagging(), true);
	}
	
	@Test
	public void falseTest() {
		ControlSoftware cs = new ControlSoftware();
		Item aItem = new Item(1234) {};
		cs.setPreviousWeight(cs.getBaggingAreaWeight());
		assertEquals (cs.verifyBagging(), false);
	}
}
