package de.ulrich.shares;

import java.util.Calendar;

public class Share {
	private String name;
	private int quantity;
	private Calendar buy;
	private Calendar sell;

	public Share(String name, int quantity, Calendar buy, Calendar sell) {
		this.name = name;
		this.quantity = quantity;
		this.buy = buy;
		this.sell = sell;
	}

	public String getName() {
		return name;
	}

	public Calendar getBuy() {
		return buy;
	}

	public int getQuantity() {
		return quantity;
	}

	public Calendar getSell() {
		return sell;
	}

	public void setSell(Calendar sell) {
		this.sell = sell;
	}

	@Override
	public String toString() {
		return "Share={" + name + "," + quantity + ","
				+ (buy != null ? buy.getTime() : "null") + ","
				+ (sell != null ? sell.getTime() : "null") + "}";
	}

}
