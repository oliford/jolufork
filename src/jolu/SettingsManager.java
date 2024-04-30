package jolu;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogManager;

/** Provides settings file lookup/storage.
 * This is general an abstract and needs to
 * have the baseName set by it's deriving class*
 */
public class SettingsManager {

    private static final Logger logger = System.getLogger(SettingsManager.class.getCanonicalName());

	private static SettingsManager defaultGlobalInstance;

	/* private Properties properties; - Implement our own on account of all the nasty character escaping, yuk */
	Hashtable<String, String> entries;
	private String settingsFile;

	//	private boolean isReadOnly = false;

	protected String baseName, envVar;

	protected SettingsManager inheritedSettings;

	/**
	 * Empty constructor, so you need to specify stuff via, e.g., global().
	 */
	public SettingsManager() {}

	/**
	 * Sets the reference file and corresponding info.
	 * @param fileOrBasename a filename or a base name like "minerva"
	 * @return the current settingsManager instance
	 */
	public SettingsManager name(String fileOrBasename) {
		File maybeFile = new File(fileOrBasename);
		if (maybeFile.canRead()) {
			this.settingsFile = maybeFile.getAbsolutePath();
		} else {
			this.baseName = fileOrBasename;
			this.settingsFile = determineSettingsFile();
		}

		this.baseName = fileOrBasename;

		try {
			entries = load(settingsFile);
		} catch (IOException err) {
			err.printStackTrace();
		}
		return this;
	}

	/**
	 * If true, use these settings globally.
	 * @param useAsGlobalSettings if true, use this SettingsManager globally
	 * @return the current settingsManager instance
	 */
	public SettingsManager global(boolean useAsGlobalSettings) {
		if (useAsGlobalSettings) defaultGlobalInstance = this;
		return this;
	}

	/**
	 * Use these settings globally.
	 * @return the current settingsManager instance
	 */
	public SettingsManager global() {
		defaultGlobalInstance = this;
		return this;
	}

	/**
	 * Inherits the settings from the specified SettingsManager.
	 * @param inheritedSettings the manager to inherit settings from
	 * @return the current settingsManager instance
	 */
	public SettingsManager inherit(SettingsManager inheritedSettings) {
		this.inheritedSettings = inheritedSettings;
		return this;
	}

	public SettingsManager(String fileOrBasename, boolean useAsGlobalSettings) {
		this(fileOrBasename, useAsGlobalSettings, null);
	}

	/**
	 * @param baseName
	 * @param globalSettings If true, this instance will be used for the 'default global' settings.
	 * @param inheritedSettings If non-null, settings from that SettingsManager will be checked if the settings don't exist in this one.
	 * 			New entries will be written to that one, not this one. So this manager becomes a list of specifically overridden settings.
	 */
	public SettingsManager(String fileOrBasename, boolean useAsGlobalSettings, SettingsManager inheritedSettings) {
		File maybeFile = new File(fileOrBasename);
		if(maybeFile.canRead() && !maybeFile.isDirectory()){
			this.settingsFile = maybeFile.getAbsolutePath();
		}else{
			this.baseName = fileOrBasename;
			this.settingsFile = determineSettingsFile();
		}

		this.baseName = fileOrBasename;

		try {
			entries = load(settingsFile);
		} catch(IOException err) {
			err.printStackTrace();
		}

		this.inheritedSettings = inheritedSettings;

		if(useAsGlobalSettings) {
			defaultGlobalInstance = this;
		}

		applyLogSettings();
	}

	/** Apply log settings from the settings file to the common java loggers (java.util.logging and log4j) */
	private void applyLogSettings() {

		// Setup log4j reflectively if a config file is specified so we don't haveto
		// depend on log4j
		if (propertyDefined("org.apache.log4j.configFile")) {
			try {
				Class<?> propertyConfiguratorClass = Class.forName("org.apache.log4j.PropertyConfigurator");
				Method propertyConfiguratorConfigure = propertyConfiguratorClass.getMethod("configure", String.class);
				propertyConfiguratorConfigure.invoke(null, getProperty("org.apache.log4j.configFile", null));
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// no log4j PropertyConfigurator available at runtime -> skip
			}
		} else if (propertyDefined("org.apache.log4j.xmlConfigFile")) {
			try {
				Class<?> domConfiguratorClass = Class.forName("org.apache.log4j.xml.DOMConfigurator");
				Method domConfiguratorConfigure = domConfiguratorClass.getMethod("configure", String.class);
				domConfiguratorConfigure.invoke(null, getProperty("org.apache.log4j.xmlConfigFile", null));
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// no log4j DOMConfigurator available at runtime -> skip
			}
		} else {
			// otherwise use the default configurator, but with INFO level
			try {
				Class<?> basicConfiguratorClass = Class.forName("org.apache.log4j.BasicConfigurator");
				Method basicConfiguratorConfigure = basicConfiguratorClass.getMethod("configure");
				basicConfiguratorConfigure.invoke(null);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// no log4j BasicConfigurator available at runtime -> skip
			}

			try {
				Class<?> loggerClass = Class.forName("org.apache.log4j.Logger");
				Method loggerGetLogger = loggerClass.getMethod("getRootLogger");
				Object logger = loggerGetLogger.invoke(null);

				Class<?> levelClass = Class.forName("org.apache.log4j.Level");
				Method loggerSetLevel = loggerClass.getMethod("setLevel", levelClass);
				Object levelWarn = levelClass.getField("WARN");
				loggerSetLevel.invoke(logger, levelWarn);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
				// no log4j BasicConfigurator available at runtime -> skip
			}
		}

		if (propertyDefined("java.util.logging.config.file")) {
			System.setProperty("java.util.logging.config.file", getProperty("java.util.logging.config.file", null));
		}

		Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
		if (handlers.length == 1) {
			try {
				LogManager.getLogManager().readConfiguration();
			} catch (SecurityException | IOException e) {
				//no user or workspace configuration, set up the standard console only at INFO level

				String conf = " "
						+ ".level=ALL \n"
						+ "handlers = java.util.logging.ConsoleHandler \n"
						+ "java.util.logging.ConsoleHandler.level = INFO \n"
						+ "java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter \n"
						+ "java.util.logging.SimpleFormatter.format=%4$s: %5$s [%1$tc]%n \n";

				try {
					LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(conf.getBytes()));
				} catch (SecurityException | IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/** Returns the current default global settings manager.
	 * This will return null, in which case you probably want to do this at the start of your program:
	 * 	new SettingsManager(baseName, true);
	 *
	 * e.g. to use Minerva's settings file, baseName will need to be 'minerva'
	 * For the specific case of Minerva, this will be done by the initialization of the MinerverSettings class.
	 *
	 * @return
	 */
	public static final SettingsManager defaultGlobal() {
		if (defaultGlobalInstance == null) {
            // we cannot log a message here, as we may need the settings manager while
            // setting up the logger, which can lead to circularity
			new SettingsManager("minerva", true);
		}
		return defaultGlobalInstance;
	}

	public void reload() {
		//only replaces the table if the file loads successfully, so we don't
		//end up wiping and refilling the settings if we can't open it for some reason (i.e. a process lock)
		try {
			entries = load(settingsFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Hashtable<String, String> load(String fileName) throws IOException {
		try (FileInputStream fin = new FileInputStream(fileName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fin));) {

			Hashtable<String, String> newTable = new Hashtable<>();
			String line = null;

			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}

				/* ignore comments: start of line then whitespace x (0 or more) then (a hash or 2 or more '/'s)) */
				//if (line.matches("^\\s*\\#.*")|| line.matches("^\\s*/{2,}"))
				if (line.matches("^\\s*[#/{2,}].*")) {
					continue;
				}
				String parts[] = line.split("=", 2);
				if (parts.length < 2) {
					continue;
				}
				newTable.put(parts[0], parts[1]);
			}

			return newTable;
		}
	}

	private String get(String name){
		return entries.get(name);
	}

	private void add(String name, String value, boolean isReadOnly) {
		if(name.contains("=") || name.contains("\n") || value.contains("\n")) {
			throw new IllegalArgumentException("Cannot have '=' in name or newline (LF) in name or value.");
		}

		entries.put(name, value);
		if (!isReadOnly) {
			appendStringToFile(name + "=" + value);
		}
	}

	private void appendStringToFile(String str) {
		try {
			FileOutputStream out = new FileOutputStream(settingsFile, true);
			try{
				PrintStream ps = new PrintStream(out);
				try{
					ps.println(str);
				}finally{
					ps.close();
				}
			}finally{
				out.close();
			}

		} catch (Exception ex) {
			throw new RuntimeException("Error saving default setting, is the settings file '"+settingsFile+"' writable?...\n"
										+ ex.getMessage());
		}
	}



	/** Returns the path and filename of the file used to load/store settings */
	public String getSettingsFile(){
		return settingsFile;
	}

	public static String determineSettingsFileName(String baseName) {
		String settingsFile = null;

		String envVar = baseName.toUpperCase() + "_SETTINGS_FILE";
		String defaultFileName = baseName + "-settings";

		//First look for a specific env variable

		settingsFile = System.getenv(envVar);
		if (settingsFile != null) {
			if (Files.isReadable(Paths.get(settingsFile))) {
			    return settingsFile;
			}
			logger.log(Level.DEBUG, "Cannot read '" + settingsFile + "' specified by '" + envVar + "', looking elsewhere.");
		}

		//Next try the default file in the user's dir
		String userDir = System.getenv("USERPROFILE");	//Windows user dir

		//The unix user dir
		if (userDir == null) {
			userDir = System.getenv("HOME");
		}

		//If we got a user dir...
		if (userDir != null) {
			//try the file directly
			settingsFile = userDir + "/" + defaultFileName;
			if ((new File(settingsFile)).canRead()) {
			    return settingsFile;
			}

			settingsFile = userDir + "/." + baseName + "/" + defaultFileName;
			if ((new File(settingsFile)).canRead()) {
			    return settingsFile;
			}

			//have a user dir, create it there

			logger.log(Level.DEBUG, "Couldn't find environment variable '" + envVar
					+ "' or a '" + defaultFileName + "' in your user dir '"
					+ userDir + "', creating an empty one there now.");

			settingsFile = userDir + "/" + defaultFileName;
			return settingsFile;
		}

		//Failing that, try in the temp dir
		logger.log(Level.DEBUG, "Couldn't find environment variable '"+envVar+"' or a '"+defaultFileName+"' in your user dir.");
		settingsFile = System.getProperty("java.io.tmpdir");
		if (settingsFile == null) {
		    throw new RuntimeException("No settings file and no temp dir");
		}

		settingsFile += "/" + defaultFileName;
		logger.log(Level.DEBUG, " Using " + defaultFileName + " from tmp dir: " + settingsFile);

		return settingsFile;
	}

	/** Tries to find or create the settings file in the standard places */
	private String determineSettingsFile() {
		String settingsFile = determineSettingsFileName(baseName);

		//is is already there?
		if((new File(settingsFile)).canRead()) {
			return settingsFile;
		}

		try {
			(new File(settingsFile)).createNewFile();
			return settingsFile;
		} catch (IOException e) {
			throw new UncheckedIOException("Couldn't write new settings file to '"+settingsFile+"'", e);
		}
	}

	/** Return the value of a specified "volatile" property. (This property is not necessaruly stored in the settings file).
	 * @param name	Settings to return.
	 * @return The value, or null if it doesn't exist
	 */
	public String getVolatileProperty(String name) {
		return get(name);
	}


	/** sets the value of a specified "volatile" property. (This property is not necessaruly stored in the settings file).
	 * @param name	Name of property.
	 * @param value	Value of property.
	 */
	public void setVolatileProperty(String name, String value) {
		if (name == null) {
		    throw new RuntimeException("Volatile property name has not been set");
		}
		if (value == null) {
		    throw new RuntimeException("Volatile property '"+name+"' has no value provided.");
		}
		add(name, value,true);
	}

	/** Return the value of the specified property using the given default value
	 * if it's not in the settings file. If defaultValue is null an exception is throw
	 * if the setting is not present.
	 * @param name	Settings to return. "Module.Setting" by convention.
	 * @param defaultValue	What to return if the setting is no present.
	 * @return The value.
	 */
	public String getProperty(String name, String defaultValue) {
		String value = get(name);
		if (value != null) {
			return value;
		}

		if (inheritedSettings != null) {
			value = inheritedSettings.getProperty(name, defaultValue);
			return value; //let the inherited add it, if it wasn't present, or throw the error if there's no default
		}

		if (defaultValue == null) {
		    throw new RuntimeException("Settings file '"+settingsFile+"' has no entry '"+name+"' and no default is avaliable.");
		}

		add(name, defaultValue,false);

		return defaultValue;
	}

	public String getProperty(String name) {
		return getProperty(name, null);
	}

	public boolean propertyDefined(String name) {
		String value = get(name);
		if (value == null && inheritedSettings != null) {
		    return inheritedSettings.propertyDefined(name);
		}
		return value != null;
	}

	/**
	 * Returns a property that describes a path in the file system.
	 * The path will be created if it does not exist.
	 *
	 * @param name The property name
	 * @param defaultPath A default path.
	 * @return The path pointed to by the given property name.
	 */
	public String getPathProperty(String name, String defaultPath) {
		String path = getProperty(name, defaultPath);

		path += File.separator; // "/"; //just make sure
		path = path.replaceAll(File.separator + "+$", File.separator);
		(new File(path)).mkdirs();
		return path;
	}

	public String getPathProperty(String name) {
		return getPathProperty(name, null);
	}

	public String getBaseName() {
		return baseName;
	}

	public Set<Entry<String, String>> getEntrySet() {

		Set<Entry<String, String>> set = new HashSet<>();
		set.addAll(entries.entrySet());
		if(inheritedSettings != null){
			set.addAll(inheritedSettings.getEntrySet());
		}
		return set;
	}

	public Set<String> getKeys() {
		Set<String> set = new HashSet<>();
		set.addAll(entries.keySet());
		if(inheritedSettings != null){
			set.addAll(inheritedSettings.getKeys());
		}
		return set;
	}
}
