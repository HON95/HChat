package no.hon95.bukkit.hchat;

import java.util.Map;
import java.util.Map.Entry;

public final class ChatCensor {

	public static String censor(String msg, Map<String, String> censoredWords) {
		for (Entry<String, String> e : censoredWords.entrySet()) {
			char[] m = msg.toLowerCase().toCharArray();
			char[] w = e.getKey().toLowerCase().toCharArray();
			for (int i = 0; i < m.length; i++) {
				if (m.length - i < w.length)
					break;
				if (m[i] != w[0])
					continue;
				for (int j = 0; j < m.length && j < w.length; j++) {
					if (m[i + j] != w[j])
						break;
					if (j != w.length - 1)
						continue;
					StringBuilder sb = new StringBuilder();
					sb.append(msg.substring(0, i)).append(e.getValue()).append(msg.substring(i + j + 1, msg.length()));
					msg = sb.toString();
				}
			}
		}
		return msg;
	}

}
