package de.ulrich.shares.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ulrich.shares.Share;
import de.ulrich.shares.ShareHandler;

public class ShareHandlerTest {
	ShareHandler shareHandler = new ShareHandler();
	Calendar today = Calendar.getInstance();

	Share java1 = new Share("JAVA", 10, today, today);
	Share java2 = new Share("JAVA", 20, today, today);
	Share java3 = new Share("JAVA", 30, today, today);
	Share dai1 = new Share("DAI", 200, today, today);
	Share dai2 = new Share("DAI", 300, today, today);
	Share dai3 = new Share("DAI", 400, today, today);
	Share dai4 = new Share("DAI", 500, today, today);

	@Before
	public void setUp() {
		shareHandler.add(java1);
		shareHandler.add(java2);
		shareHandler.add(java3);
		shareHandler.add(dai1);
		shareHandler.add(dai2);
		shareHandler.add(dai3);
		shareHandler.add(dai4);
	}

	@Test
	public void testGetShares() {
		List<Share> shares = shareHandler.get("JAVA");
		//System.out.println(shares);
		assertTrue(shares.contains(java1));
		assertTrue(shares.contains(java2));
		assertTrue(shares.contains(java3));
		
		shares = shareHandler.get("DAI");
		//System.out.println(shares);
		assertTrue(shares.contains(dai1));
		assertTrue(shares.contains(dai2));
		assertTrue(shares.contains(dai3));
		// assertEquals(shareHandler.get("JAVA"), new
		// ArrayList<Share>().add(java));
	}

	@Test
	public void testToString() {
		String st = shareHandler.toString();
		//System.out.println(st);
	}

	@Test
	public void testBuy() {
		Share share = new Share("JAVA", 1000, today, null);
		shareHandler.buy(share);
		Share shareJava = shareHandler.get("JAVA", 1000);
		assertEquals(shareJava, share);
	}
	
	@Test
	public void testSell() {
		Share share = new Share("JAVA", 1000, today, null);
		shareHandler.buy(share);
		shareHandler.sell("JAVA", 1000, today);
		
		share.setSell(today);
		Share shareJava = shareHandler.get("JAVA", 1000);
		assertEquals(shareJava, share);
	}
	
	@Test
	public void testSell2() {
		shareHandler.sell("JAVA", 30, today);
		
		java3.setSell(today);
		Share shareJava = shareHandler.get("JAVA", 30);
		assertEquals(shareJava, java3);
	}

	@Test
	public void testSell3() {
		shareHandler.sell("JAVA", 60, today);
		
		java3.setSell(today);
		Share shareJava = shareHandler.get("JAVA", 30);
		assertEquals(shareJava, java3);
	}
}


