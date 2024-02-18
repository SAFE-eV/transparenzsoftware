package de.safe_ev.transparenzsoftware.gui.views.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailsList {

	
	static class ListEntry {
		String key;
		Object value;
	}

	List<ListEntry> list = new ArrayList<>();
	
	public List<String> keySet() {
		List<String> set = new ArrayList<>();
		for (ListEntry e : list) {
			set.add(e.key);
		}
		return set;
	}
	
	public Object get(String key) {
		for (ListEntry e : list) if (key.equals(e.key)) return e.value;
		return null;
	}
	
	public void put(String key, Object value) {
		for (ListEntry e : list) if (key.equals(e.key)) {
			e.value = value;
			return ;
		}
		ListEntry x = new ListEntry();
		x.key = key;
		x.value = value;
		list.add(x);
	}
	
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	public int size()
	{
		return list.size();
	}
}
