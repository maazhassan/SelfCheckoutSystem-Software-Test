package org.lsmr.selfcheckout.controlsoftware;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class BaggingControllerTest {
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	public static final int testlimit = 30000;
	public static final int testsens = 50;
	
	SelfCheckoutStation a = new SelfCheckoutStation(null, null, null, testsens, testlimit);
	@Test
	public void constr() {
		BaggingControlSoftware bc = new BaggingControlSoftware(a);
		}
		
	
	@Test
	public void testenabled() {
		System.setOut(new PrintStream(outContent));
		try {
		a.scale.enable();
		assertEquals("A scale has been enabled.\n", outContent.toString());
		a.baggingArea.enable();
		assertEquals("A baggingArea has been enabled.\n", outContent.toString());
		}
		finally {
			System.setOut(originalOut);
		}
	}
	
	@Test
	public void testDisabled() {
		System.setOut(new PrintStream(outContent));
		try {
		a.scale.disable();
		assertEquals("A scale has been disabled.\n", outContent.toString());
		a.baggingArea.disable();
		assertEquals("A baggingArea has been disabled.\n", outContent.toString());
		}
		finally {
			System.setOut(originalOut);
		}
	}
	
	@Test
	public void testadditem() throws Exception {
		SelfCheckoutStation a = new SelfCheckoutStation(null, null, null, testsens, testlimit);
		BaggingControlSoftware bControlSoftware = BaggingControlSoftware(a);
		Item aItem = new Item(60) {};
		bControlSoftware.place(aItem);
		try {
			assertTrue(a.baggingArea.getCurrentWeight() == 60);
		}
		catch (Exception e) {
			throw new Exception("weight not correct");
		}
	}
	
	@Test
	public void testadd3() throws Exception {
		SelfCheckoutStation a = new SelfCheckoutStation(null, null, null, testsens, testlimit);
		BaggingControlSoftware bControlSoftware = BaggingControlSoftware(a);
		Item aItem = new Item(60) {};
		Item bItem = new Item(90) {};
		Item cItem = new Item(120) {};
		bControlSoftware.place(aItem);
		bControlSoftware.place(bItem);
		bControlSoftware.place(cItem);
		try {
			assertTrue(a.baggingArea.getCurrentWeight() == 270);
		}
		catch (Exception e) {
			throw new Exception("weight not correct");
		}
		 
	}
	
	@Test
	public void testoverload() throws Exception {
		SelfCheckoutStation a = new SelfCheckoutStation(null, null, null, testsens, testlimit);
		BaggingControlSoftware bControlSoftware = BaggingControlSoftware(a);
		
		Item aItem = new Item(testlimit+1) {};
		try {
			assertEquals(a.baggingArea.getCurrentWeight(),0);
		} catch (Exception e) {
			throw new Exception("limit should be exceeded");
		}
	}
	
	
	@Test
	public void testnegative() throws Exception {
		SelfCheckoutStation a = new SelfCheckoutStation(null, null, null, testsens, testlimit);
		BaggingControlSoftware bControlSoftware = BaggingControlSoftware(a);
		
		Item aItem = new Item(-90) {};
		try {
			assertEquals(a.baggingArea.getCurrentWeight(),0);
		} catch (Exception e) {
			throw new Exception("weight cannot be 0");
		}
	}


	private BaggingControlSoftware BaggingControlSoftware(SelfCheckoutStation a2) {
		// TODO Auto-generated method stub
		return null;
	}
}
