package de.ulrich.shares;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShareHandler {
	Map<String, List<Share>> shares = new HashMap<String, List<Share>>();

	public void add(Share java) {
		List<Share> shareList = shares.get(java.getName());
		if (shareList == null) {
			shareList = new ArrayList<Share>();
			shares.put(java.getName(), shareList);
		}
		shareList.add(java);
	}

	public List<Share> get(String name) {
		List<Share> shareList = shares.get(name);
		return shareList;
	}
	
	public Share get(String name, int quantity) {
		List<Share> shares = get(name);
		Iterator<Share> it = shares.iterator();
		while (it.hasNext()) {
			Share share = it.next();
			if (share.getQuantity() == quantity) {
				return share;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Set<String> keys = shares.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String ticker = it.next();
			sb.append(ticker + "\n");
			List<Share> shareList = shares.get(ticker);
			for (Share share : shareList) {
				sb.append("\t" + share + "\n");
			}
		}
		return sb.toString();
	}

	public void buy(Share share) {
		add(share);
	}

	public void sell(String name, int quantity, Calendar today) {
		Share share = get(name, quantity);
		if (share == null) {
			List<Share> shares = get(name);
			System.out.println(shares);
			Iterator<Share> it = shares.iterator();
			while (it.hasNext()) {
				Share shareIt = it.next();
				if (shareIt.getQuantity() < quantity) {
					quantity =- shareIt.getQuantity();
					shareIt.setSell(today);
					assert(quantity >= 0) : "We can not sell more as we have";
				}
			}
		}
		share.setSell(today);
	}
}
