package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.List;

import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.DimClientSettings;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;

public class DimDevice extends AbstractTagDevice {
    private DeviceContext deviceContext;
    private DimClientSettings settings;
    private String typeID;

    private UaFolderNode rootNode;
    private UaFolderNode serverFolder;
    private UaFolderNode serviceFolder;

    DimDriver drv;
    Thread driverThread;

    public DimDevice(DeviceContext deviceContext, DimClientSettings settings){
      //this.deviceContext = deviceContext;
      super(deviceContext, ExampleDeviceType.TYPE_ID);
      typeID = ExampleDeviceType.TYPE_ID;
      //super(deviceContext, typeID);
      this.deviceContext = deviceContext;
      this.settings = settings;

      //has the driver logic and actual communication with the physical device
      drv = new DimDriver(settings, deviceContext.getName());
      //it's generally useful to the driver logic and actual communication running on a thread
      driverThread = new Thread(drv);
      driverThread.start();
    }

    //@Nonnull
    //@Override
    //public String getStatus() {
    //    return drv.status;
    //}

    @Override
    public void onStartup() {
        super.onStartup();
        
        /*UaFolderNode rootNode = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(getName()),
            deviceContext.qualifiedName(String.format("[%s]", getName())),
            new LocalizedText(String.format("[%s]", getName()))
        );*/

	//Create whatever device folders you want, these are just an example
        //don't forget to instance UaFolderNode objects
	    serverFolder = super.addFolder(rootNode,"Server");
	    serviceFolder = super.addFolder(rootNode, "Services");

        //Config tags - Static, like hostname, ports, and other config variables needed
	    addStaticNode(serverFolder, "Hostname", BuiltinDataType.String.getNodeId(), new DataValue(new Variant(settings.getHostname())));
        addReadOnlyNode(serverFolder, "ID", BuiltinDataType.String.getNodeId(), new ReadOnlyTag() {
            @Override
            public DataValue getter(){
                return drv.getID();
            }
        });

	//example creating multiple similar tags/nodes dinamically
        for(int mi = 0; mi < drv.numberServices; mi++ ){
          String nodeName = "Service " + String.valueOf(mi);
          final Integer i = Integer.valueOf(mi);
          addReadWriteNode(serviceFolder, nodeName, BuiltinDataType.Float.getNodeId(), new WritableTag(){
              @Override
              public void setter(DataValue val) {
                  drv.setServiceValue(val, i);
              }

              @Override
              public DataValue getter() {
                  return drv.getServiceValue(i);
              }
          });
        }

	// fire initial subscription creation
        List<DataItem> dataItems = deviceContext.getSubscriptionModel().getDataItems(getName());
        onDataItemsCreated(dataItems);
    }

    @Override
    public void onShutdown() {
        super.onShutdown();

        //clean up thread
        drv.running.set(false);
        //drv.interrupt();

    }
}
