package de.ulrich.shares;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
        return shares.get(name);
	}
	
	public Share get(String name, int quantity) {
		List<Share> shares = get(name);
        for (Share share1 : shares) {
            if (share1.getQuantity() == quantity) {
                return share1;
            }
        }
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Set<String> keys = shares.keySet();
        for (String key : keys) {
            sb.append(key).append("\n");
            List<Share> shareList = shares.get(key);
            for (Share share : shareList) {
                sb.append("\t").append(share).append("\n");
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
            for (Share share1 : shares) {
                if (share1.getQuantity() < quantity) {
                    quantity = -share1.getQuantity();
                    share1.setSell(today);
                    assert (quantity >= 0) : "We can not sell more as we have";
                }
            }
		}
        assert share != null;
        share.setSell(today);
	}
}
