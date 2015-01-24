package no.hon95.bukkit.hchat.common.util;

import java.util.HashMap;
import java.util.Map;

public class StringKeyHashMap<V> extends HashMap<String, V> {

	private static final long serialVersionUID = 1L;

	public StringKeyHashMap() {}

	public StringKeyHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public StringKeyHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public StringKeyHashMap(Map<? extends String, ? extends V> m) {
		super(m);
	}

	public String getCaseInsensitiveKey(String key) {
		if (key == null)
			return null;
		for (String s : keySet()) {
			if (s != null && s.equalsIgnoreCase(key))
				return s;
		}
		return null;
	}

	public V getCaseInsensitiveValue(String key) {
		if (key == null)
			return get(null);
		return get(getCaseInsensitiveKey(key));
	}

	public V removeCaseInsensitive(String key) {
		return remove(getCaseInsensitiveKey(key));
	}

	public boolean containsCaseInsensitive(String key) {
		return getCaseInsensitiveKey(key) != null;
	}
}
