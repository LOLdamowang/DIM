package com.inductiveautomation.ignition.examples.tagdriver.configuration.settings;

import org.apache.wicket.validation.validator.RangeValidator;

import com.inductiveautomation.ignition.gateway.localdb.persistence.BooleanField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;

import simpleorm.dataset.SFieldFlags;

/**
 * Implements all functionality needed to save a device and its settings in the internal database.
 */
public class DimClientSettings extends PersistentRecord {

    private static final long serialVersionUID = 1L;

    /**
     * Needed so that the device record can be saved in the internal database.
     */
    public static final RecordMeta<DimClientSettings> META =
            new RecordMeta<DimClientSettings>(DimClientSettings.class, "DClientSettings");

    /**
     * Reference to parent DeviceSettingsRecord: holds items like Device Name setting and Enabled setting.
     * These fields also appear in the General category section when creating a new driver in the Gateway.
     */
    public static final LongField DEVICE_SETTINGS_ID =
            new LongField(META, "DeviceSettingsId", SFieldFlags.SPRIMARY_KEY);

    /**
     * Needed to link a device settings record to the device record in the internal database.
     */
    public static final ReferenceField<DeviceSettingsRecord> DEVICE_SETTINGS = new ReferenceField<>(
        META,
        DeviceSettingsRecord.META,
        "DeviceSettings",
        DEVICE_SETTINGS_ID
    );

     /* Connectivity */
    public static StringField Hostname = new StringField(META, "Hostname", SFieldFlags.SMANDATORY, SFieldFlags.SDESCRIPTIVE);
    public static IntField Port = new IntField(META, "Port", SFieldFlags.SMANDATORY);
    public static BooleanField ConnectOnStartup = new BooleanField(META, "ConnectOnStartup");
    public static StringField Servername = new StringField(META,"Servername",SFieldFlags.SMANDATORY, SFieldFlags.SDESCRIPTIVE);


    /**
     * Settings specific to the ExampleDevice; each one must be placed in a Category.
     */
    public static final IntField TAG_COUNT = new IntField(META, "TagCount", SFieldFlags.SMANDATORY);

    /**
     * Categories specific to the ExampleDevice; each category appears below the General category in the Gateway
     * when creating a new driver.
     * <p>
     * In this case, the displayKey below is referencing ExampleDeviceSettings.properties, which should be located
     * in the same package as the class file on the file system. You must put the actual category name into this file.
     * <p>
     * The order number determines the order in which multiple categories are displayed on the page.
     */
        /* Categories */
     public static final Category Connectivity = new Category("DimClientSettings.Category.Connectivity", 1001)
     .include(Hostname, Port, ConnectOnStartup,Servername);

     public static final Category EXAMPLE_CATEGORY =
            new Category("DimClientSettings.ExampleCategory", 1002).include(TAG_COUNT);

    static {
        // Hides some generic ReferenceField settings that are not needed in our driver example.
        DEVICE_SETTINGS.getFormMeta().setVisible(false);
	
	Hostname.setDefault("");
        Port.setDefault(1999);
        Port.addValidator(new RangeValidator<Integer>(1, 65535));
        ConnectOnStartup.setDefault(true);
	Servername.setDefault("");
    }

    /**
     * Get the number of tags that will be exposed to the driver
     *
     * @return an int with the saved tag count
     */
    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public String getHostname() {
        return getString(Hostname);
    }

    public int getPort() {
        return getInt(Port);
    }

    public boolean getConnectOnStartup() {
        return getBoolean(ConnectOnStartup);
    }

    public String getServername() {
	return getString(Servername);
    }

    public int getTagCount() {
        return getInt(TAG_COUNT);
    }

    /**
     * Set the number of tags that will be exposed to the driver
     *
     * @param tagCount an int that represents the tag count to save
     */
    public void setHostname(String hostname) {
        setString(Hostname, hostname);
    }

    public void setPort(int port) {
        setInt(Port, port);
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        setBoolean(ConnectOnStartup, connectOnStartup);
    }

    public void setServername(String servername) {
	setString(Servername, servername);
    }

    public void setTagCount(int tagCount) {
        setInt(TAG_COUNT, tagCount);
    }

}
