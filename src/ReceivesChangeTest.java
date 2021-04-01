import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;

import static org.junit.Assert.*;

public class ReceivesChangeTest {


    @Test
    public void testReceiveChange1() throws Exception{
    	
    	BigDecimal total = new BigDecimal("123");
    	ReceivesChange rc = new ReceivesChange(total);
    	ControlSoftware control = new ControlSoftware();
    	
    	BigDecimal payment = new BigDecimal("100.30");
    	int[] denomination = rc.calculateChange(payment);
    	int[] expected = {0, 0, 2, 0, 1, 0, 1, 0, 0, 1, 0, 0}; 
    	assertTrue(Arrays.equals(expected, denomination));
 
    }

    @Test
    public void testReceiveChange2() throws Exception{
    	
    	BigDecimal total = new BigDecimal("253.57");
    	ReceivesChange rc = new ReceivesChange(total);
    	ControlSoftware control = new ControlSoftware();
    	
    	BigDecimal payment = new BigDecimal("253.57");
    	int[] denomination = rc.calculateChange(payment);
    	int[] expected = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; 
    	assertTrue(Arrays.equals(expected, denomination));
 
    }
    
    @Test
    public void testReceiveChange3() throws Exception{
    	
    	BigDecimal total = new BigDecimal("45.83");
    	ReceivesChange rc = new ReceivesChange(total);
    	ControlSoftware control = new ControlSoftware();
    	
    	BigDecimal payment = new BigDecimal("31.63");
    	int[] denomination = rc.calculateChange(payment);
    	int[] expected = {0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0}; 
    	assertTrue(Arrays.equals(expected, denomination));
 
    }
    
    @Rule public ExpectedException thrown= ExpectedException.none();

    @Test
    public void testReceiveChange4() throws Exception{
    	
    	BigDecimal total = new BigDecimal("31.63");
    	ReceivesChange rc = new ReceivesChange(total);
    	ControlSoftware control = new ControlSoftware();
    	BigDecimal payment = new BigDecimal("45.83");
    	
        thrown.expectMessage("Customer have not paid the full amount");
        
    	int[] denomination = rc.calculateChange(payment);

    }
}
