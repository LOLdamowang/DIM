package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceServices;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;

import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.DimClientSettings;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;



public class AbstractTagDevice extends ManagedAddressSpaceServices implements Device {

    private static final UByte READ_ONLY = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_ONLY));
    private static final UByte READ_WRITE = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_WRITE));

    private final SubscriptionModel subscriptionModel;

    private String typeID;
    private DeviceContext deviceContext;
    private DimClientSettings settings;
    
    private UaFolderNode rootNode;

    public AbstractTagDevice(DeviceContext deviceContext, String typeID) {
        super(deviceContext.getServer());
		//this.typeID = "";

        this.deviceContext = deviceContext;
        //this.settings = settings;
        this.typeID = typeID;
        //this.typeID = "";
        
        subscriptionModel = new SubscriptionModel(deviceContext.getServer(), this);
    }

    @Nonnull
    @Override
    public String getName() {
        return deviceContext.getName();
    }

    @Nonnull
    @Override
    public String getStatus() {
        return "Running";
    }

    @Nonnull
    @Override
    public String getTypeId() {
        return typeID;
    }

    @Override
    public void onStartup() {
        super.onStartup();

        
        // create a folder node for our configured device
        UaFolderNode rootNode = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(getName()),
            deviceContext.qualifiedName(String.format("[%s]", getName())),
            new LocalizedText(String.format("[%s]", getName()))
        );

        // add the folder node to the server
        getNodeManager().addNode(rootNode);

        // add a reference to the root "Devices" folder node
        rootNode.addReference(new Reference(
            rootNode.getNodeId(),
            Identifiers.Organizes,
            deviceContext.getRootNodeId().expanded(),
            Reference.Direction.INVERSE
        ));

        //addSimpleFolder(rootNode, "static", settings.getTagCount(), READ_WRITE);
        //addSimpleFolder(rootNode, "readOnly", settings.getTagCount(), READ_ONLY);

        //addDynamicNodes(rootNode);

        // fire initial subscription creation
        //List<DataItem> dataItems = deviceContext.getSubscriptionModel().getDataItems(getName());
        //onDataItemsCreated(dataItems);
    }

    public void addStaticNode(UaFolderNode folder, String nodeName, NodeId datatype, DataValue val){
        //rootNode.addOrganizes(folder);
        UaVariableNode node = UaVariableNode.builder(getNodeContext())
            .setNodeId(deviceContext.nodeId(nodeName))
            .setBrowseName(deviceContext.qualifiedName(nodeName))
            .setDisplayName(new LocalizedText(nodeName))
            .setDataType(datatype)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setAccessLevel(READ_ONLY)
            .setUserAccessLevel(READ_ONLY)
            .build();

        node.setValue(val);
        getNodeManager().addNode(node);
        folder.addOrganizes(node);
    }
    
    
    protected void addReadOnlyNode(UaFolderNode folder, String nodeName, NodeId datatype, ReadOnlyTag tag){
        //rootNode.addOrganizes(folder);
        UaVariableNode node = UaVariableNode.builder(getNodeContext())
            .setNodeId(deviceContext.nodeId(nodeName))
            .setBrowseName(deviceContext.qualifiedName(nodeName))
            .setDisplayName(new LocalizedText(nodeName))
            .setDataType(datatype)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setAccessLevel(READ_ONLY)
            .setUserAccessLevel(READ_ONLY)
            .build();

        node.setAttributeDelegate(new AttributeDelegate() {
            @Override
            public DataValue getValue(AttributeContext context, VariableNode node) {
                return tag.getter();
            }
        });

        getNodeManager().addNode(node);
        folder.addOrganizes(node);
    }

    protected void addReadWriteNode(UaFolderNode folder, String nodeName, NodeId datatype, WritableTag tag){
        //rootNode.addOrganizes(folder);
        UaVariableNode node = UaVariableNode.builder(getNodeContext())
            .setNodeId(deviceContext.nodeId(nodeName))
            .setBrowseName(deviceContext.qualifiedName(nodeName))
            .setDisplayName(new LocalizedText(nodeName))
            .setDataType(datatype)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setAccessLevel(READ_WRITE)
            .setUserAccessLevel(READ_WRITE)
            .build();

        node.setAttributeDelegate(new AttributeDelegate() {
            @Override
            public DataValue getValue(AttributeContext context, VariableNode node) {
                return tag.getter();
            }
            @Override
            public void setValue(AttributeContext context, VariableNode node, DataValue cmd) {
                tag.setter(cmd);
            }
        });

        getNodeManager().addNode(node);
        folder.addOrganizes(node);
    }
 

    public interface WritableTag{
        public DataValue getter();
        public void setter(DataValue cmd);
    }
    public interface ReadOnlyTag{
        public DataValue getter();
    }
    
    
    public UaFolderNode addFolder(UaFolderNode rootNode, String name) {
        UaFolderNode folder = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(name),
            deviceContext.qualifiedName(name),
            new LocalizedText(name)
        );
        getNodeManager().addNode(folder);
        //rootNode.addOrganizes(folder);
        return folder;
    }
    
    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }
    
    @Override
    public void onShutdown() {
        super.getNodeContext();

        deviceContext.getGatewayContext()
            .getExecutionManager()
            .unRegister(ExampleDeviceType.TYPE_ID, deviceContext.getName());
    }

}
