package com.inductiveautomation.ignition.examples.tagdriver;

import static org.python.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.AbstractDeviceModuleHook;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceType;

public class ModuleHook extends AbstractDeviceModuleHook {

    @Override
    public void setup(@NotNull GatewayContext context) {
        super.setup(context);
        
        BundleUtil.get().addBundle(AbstractTagDevice.class);
    }

    @Override
    public void startup(@NotNull LicenseState activationState) {
        super.startup(activationState);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        BundleUtil.get().removeBundle(AbstractTagDevice.class);
    }

    @Nonnull
    @Override
    protected List<DeviceType> getDeviceTypes() {
        return newArrayList(ExampleDeviceType.INSTANCE);
    }

}
