import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class FinishAddingItemsTest {

	@Test
	public void testFinishAddingItems() {
		ControlSoftware cs = new ControlSoftware();
		Barcode item = new Barcode("1234");
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(item, new BarcodedProduct(item, "banana", new BigDecimal(10)));
		cs.increaseTotal(item);
		assertEquals ("The total for all of your items are " + 10, cs.finishAddingItems());
	}
	
	@Test
	public void testFinishAddingItems2() {
		ControlSoftware cs = new ControlSoftware();
		Barcode item = new Barcode("1234");
		Barcode item2 = new Barcode("123");
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(item, new BarcodedProduct(item, "banana", new BigDecimal(10)));
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(item2, new BarcodedProduct(item2, "watermelon", new BigDecimal(6)));
		cs.increaseTotal(item);
		cs.increaseTotal(item2);
		assertEquals ("The total for all of your items are " + 16, cs.finishAddingItems());
	}

}
