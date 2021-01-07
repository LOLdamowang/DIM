package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.DimClientSettings;

import dim.DimClient;
import dim.DimInfo;
import dim.DimService;

public class DimDriver implements Runnable {
  public String name;
  DimClientSettings settings;

  public AtomicBoolean running = new AtomicBoolean(true);
  private AtomicBoolean stopped = new AtomicBoolean(false);
  private String hostname;
  public int numberServices = 1;

  //store node/tag values internally
  DataValue id;
  DataValue[] Services = new DataValue[numberServices];

  public final Logger log = Logger.getLogger(DimDriver.class.getName());

  public DimDriver(DimClientSettings settings, String name){
    this.settings = settings;
    this.name = name;

    hostname = settings.getHostname();
    log.info(" [DimDriver] - Starting out mock device with hostname: localhost");

  }

  public DataValue getID(){
    int idnr = (int) (Math.random() * 1000);
    id = new DataValue(new Variant("id-"+String.valueOf(idnr)), StatusCode.GOOD);
    return id;
  }

  public DataValue getServiceValue(int idx){
    //request value from device maybe
    float val =  (float) (Math.random() * 5);
    Services[idx] = new DataValue(new Variant(val), StatusCode.GOOD);
    return Services[idx];
  }

  public void setServiceValue(DataValue val, Integer idx){
	    //val DataValue comes from Ignitions OPC-UA server
	    float fval = Float.valueOf(val.getValue().getValue().toString().trim());
	    //do something with it, write to device etc.. maybe check if write was successfull
	    boolean wsuccess = true;
	    if(wsuccess){
	      Services[idx] = new DataValue(new Variant(fval), StatusCode.GOOD);
	    }else{
	      //never reached, just as an example of setting node/tag quality based on readback
	      //or something of the sort
	      Services[idx] = new DataValue(new Variant(-1.0), StatusCode.BAD);
	    }
	  }
  
  @Override
  public void run(){

    while(running.get()){
      //do stuff here
    	DimService sid1 = new DimService("TEST_IT_INT",1);
        sid1.updateService(2);

        DimInfo runNumber = new DimInfo("test_service",-1);
        DimClient.sendCommand("test_server", "Do_it");

      try{
        Thread.sleep(1000);
      }catch(InterruptedException ex){
        log.info("interrupted, time to stop sleeping and say goodbye!");
        Thread.currentThread().interrupt();
      }
    }
    //clean up
    stopped.set(true);
  }

}
