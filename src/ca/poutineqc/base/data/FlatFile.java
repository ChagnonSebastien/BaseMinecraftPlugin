package ca.poutineqc.base.data;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import ca.poutineqc.base.instantiable.SavableParameter;
import ca.poutineqc.base.plugin.PPlugin;
import ca.poutineqc.base.utils.PYAMLFile;
import ca.poutineqc.base.utils.Pair;

/**
 * A FlatFile that is used to store data on. It can read or write Data. It
 * implements DataStorage.
 * 
 * @author S�bastien Chagnon
 * @see DataStorage
 */
public class FlatFile implements DataStorage {
	
	// =========================================================================
	// Fields
	// =========================================================================

	private PYAMLFile file;

	
	// =========================================================================
	// Constructor(s)
	// =========================================================================

	/**
	 * Default constructor.
	 * 
	 * @param plugin
	 *            - the main class of the plugin
	 * @param fileName
	 *            - the file name to be used for the creation of the file with
	 *            the PYAMLFile
	 * @param folders
	 *            - the folders in which the file should be stored
	 * @see PPlugin
	 */
	public FlatFile(PPlugin plugin, String fileName, String... folders) {
		file = new PYAMLFile(fileName, false, folders);
	}


	// =========================================================================
	// Data Accessors
	// =========================================================================

	@Override
	public List<UUID> getAllIdentifications(SavableParameter identification) {
		List<UUID> identifications = new ArrayList<UUID>();

		for (String key : file.getKeys(false))
			identifications.add(UUID.fromString(key));

		return identifications;
	}

	@Override
	public void createTableIfNotExists(SavableParameter identification, Collection<SavableParameter> parameters) {
		// Does nothing
	}

	@Override
	public void newInstance(Pair<SavableParameter, UUID> identification,
			List<Pair<SavableParameter, String>> createParameters) {
		for (Pair<SavableParameter, String> entry : createParameters)
			setString(identification, entry.getKey(), entry.getValue());
	}

	@Override
	public Map<SavableParameter, String> getIndividualData(Pair<SavableParameter, UUID> identification,
			Collection<SavableParameter> parameters) {

		Map<SavableParameter, String> user = new HashMap<SavableParameter, String>();

		ConfigurationSection cs = file.getConfigurationSection(identification.getValue().toString());

		for (SavableParameter parameter : parameters) {

			switch (parameter.getType()) {
			case BOOLEAN:
				user.put(parameter, String.valueOf(cs.getBoolean(parameter.getKey())));
				break;
			case DOUBLE:
			case FLOAT:
				user.put(parameter, String.valueOf(cs.getDouble(parameter.getKey())));
				break;
			case INTEGER:
				user.put(parameter, String.valueOf(cs.getInt(parameter.getKey())));
				break;
			case LONG:
				user.put(parameter, String.valueOf(cs.getLong(parameter.getKey())));
				break;
			case STRING:
				user.put(parameter, cs.getString(parameter.getKey()));
				break;

			}
		}

		return user;
	}
	
	@Override
	public void setString(Pair<SavableParameter, UUID> identification, SavableParameter parameter, String value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setInt(Pair<SavableParameter, UUID> identification, SavableParameter parameter, int value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setDouble(Pair<SavableParameter, UUID> identification, SavableParameter parameter, double value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setLong(Pair<SavableParameter, UUID> identification, SavableParameter parameter, long value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setBoolean(Pair<SavableParameter, UUID> identification, SavableParameter parameter, boolean value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setFloat(Pair<SavableParameter, UUID> identification, SavableParameter parameter, float value) {
		file.set(identification.getValue().toString() + "." + parameter.getKey(), value);
		file.save();
	}

	@Override
	public void setValues(Pair<SavableParameter, UUID> identification, List<Pair<SavableParameter, String>> entries)
			throws InvalidParameterException {
		throw new InvalidParameterException("A flat file can't write multiple values at once");
	}
}
