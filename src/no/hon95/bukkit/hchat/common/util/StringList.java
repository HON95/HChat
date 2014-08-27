package no.hon95.bukkit.hchat.common.util;

import java.util.ArrayList;
import java.util.Collection;


public class StringList extends ArrayList<String> {

	private static final long serialVersionUID = 1L;

	public StringList() {}

	public StringList(int initialCapacity) {
		super(initialCapacity);
	}

	public StringList(Collection<? extends String> c) {
		super(c);
	}

	public String getCaseInsensitive(String str) {
		if (str == null)
			return null;
		for (String s : this) {
			if (s != null && s.equalsIgnoreCase(str))
				return s;
		}
		return null;
	}

	public boolean removeCaseInsensitive(String str) {
		return remove(getCaseInsensitive(str));
	}

	public boolean containsCaseInsensitive(String str) {
		return getCaseInsensitive(str) != null;
	}
}
