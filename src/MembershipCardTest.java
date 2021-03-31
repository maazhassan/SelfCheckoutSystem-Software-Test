import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class MembershipCardTest {

	@Test
	public void testValidNumber7() {
		ControlSoftware cs = new ControlSoftware();
		cs.registerMembershipNumber();
		assertEquals(true, cs.scanMembershipCard("7"));
	}
	
	@Test
	public void testInvalidNumber() {
		ControlSoftware cs = new ControlSoftware();
		cs.registerMembershipNumber();
		assertEquals(false, cs.scanMembershipCard("11"));
	}
	
	@Test
	public void testValidNumber2() {
		ControlSoftware cs = new ControlSoftware();
		cs.registerMembershipNumber();
		assertEquals(true, cs.scanMembershipCard("2"));
	}
	
	@Test
	public void testValidNumber0() {
		ControlSoftware cs = new ControlSoftware();
		cs.registerMembershipNumber();
		assertEquals(true, cs.scanMembershipCard("0"));
	}
	
	@Test
	public void testValidNumber10() {
		ControlSoftware cs = new ControlSoftware();
		cs.registerMembershipNumber();
		assertEquals(true, cs.scanMembershipCard("10"));
	}
	
	@Test
	public void testDiscount() {
		ControlSoftware cs = new ControlSoftware();
		Barcode item = new Barcode("1234");
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(item, new BarcodedProduct(item, "banana", new BigDecimal(10)));
		cs.increaseTotal(item);
		cs.registerMembershipNumber();
		cs.scanMembershipCard("1");
		assertEquals(new BigDecimal(9), cs.getTotal());
	}

}
