package de.ulrich.shares.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import de.ulrich.shares.Share;

public class ShareTest {
	Share share;
	Calendar buy = Calendar.getInstance();
	Calendar sell = Calendar.getInstance();

	@Before
	public void setUp() {
		buy.set(2008, 11, 01, 11, 11, 0);
		sell.set(2009, 11, 01, 11, 12, 0);
		share = new Share("JAVA", 1000, buy, sell);
	}

	@Test
	public void testShare() {
		assertEquals(share.getName(), "JAVA");
		assertTrue(share.getQuantity() == 1000);
		assertTrue(share.getBuy() == buy);
		assertTrue(share.getSell() == sell);
		sell.set(2009, 12, 01, 10, 5);
		share.setSell(sell);
		assertTrue(share.getSell() == sell);
	}

	@Test
	public void testToString() {
		// System.out.println(share);
		String match = "Share={JAVA,1000,Mon Dec 01 11:11:00 CET 2008,Tue Dec 01 11:12:00 CET 2009}";
		assertTrue(share.toString().equals(match));
	}
}
