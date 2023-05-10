package de.safe_ev.transparenzsoftware.i18n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * Translation helper which provides a simple interface to load
 * strings out of resource bundle.
 */
public class Translator {

    private static final Logger LOGGER = LogManager.getLogger(Translator.class);
    private static ResourceBundle rb = null;
    private static Locale locale;

    /**
     * Initialises the resource bundles
     * @param locale - locale which should be used
     */
    public static void init(Locale locale) {
        Translator.locale = locale;
        
        ResourceBundle.Control control = Control.getControl(Control.FORMAT_PROPERTIES);
        String bname = control.toBundleName("i18n/lang", locale);
        String resname = control.toResourceName(bname,  "properties");
        resname = resname.replaceAll("_DE", "");
        InputStream ins = Translator.class.getClassLoader().getResourceAsStream(resname);
        try {
            Reader rdr = new InputStreamReader(ins,Charset.forName("UTF-8"));
			Translator.rb = new PropertyResourceBundle(rdr);
		} catch (Exception e) {
		}
        if (Translator.rb == null) {
        	Translator.rb = ResourceBundle.getBundle("i18n/lang",locale);
        }
        LOGGER.debug(String.format("Locale used will be '%s'", locale.getLanguage()));
    }
    /**
     * Initialises the resource bundles
     * @param locale - locale which should be used
     */
    public static void init(String locale) {
        if(locale.contains("de") || locale.contains("DE")){
            Translator.init(new Locale("de", "DE"));
        } else {
            Translator.init(new Locale("en", "EN"));
        }
    }

    /**
     * Gets a string for a translation key
     *
     * @param key - key of the translation which should be loaded
     * @return translation or key if no translation could be found
     * @throws MissingResourceException if no key was found
     */
    public static String get(String key) throws MissingResourceException {
        return get(key, key);
    }


    /**
     * Gets a string for a translation key
     *
     * @param key - key of the translation which should be loaded
     * @param defaultValue - default value returned if key not found
     * @return translation or key if no translation could be found
     * @throws MissingResourceException if no key was found
     */
    public static String get(String key, String defaultValue) throws MissingResourceException {
        if (rb == null) {
            //locale load with short code as java is behaving strange here otherwise
            String language = Locale.getDefault() != null ? Locale.getDefault().getLanguage() : "de";
            init(language);
        }
        try {
        	String result = rb.getString(key);
            return result;
        } catch (Exception e) {
            // just to be sure we do not want to crash our app
            // caused by an missing string
            LOGGER.error("Error on loading localisation text", e);
            return defaultValue;
        }
    }

    /**
     * Opens the help file and reads the content as a string
     *
     * @return content of the help file as string
     */
    public static String getHelpFileContent() {
        InputStream content = ClassLoader.getSystemResourceAsStream("help/" + locale.getLanguage() + ".html");
        if (content == null) {
            content = ClassLoader.getSystemResourceAsStream("help/en.html");
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        try {
            while ((nRead = content.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException | NullPointerException e) {
            LOGGER.error("Error on loading help text", e);
            return Translator.get("app.view.error.loadinghelp");
        }
        byte[] byteArray = buffer.toByteArray();
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
