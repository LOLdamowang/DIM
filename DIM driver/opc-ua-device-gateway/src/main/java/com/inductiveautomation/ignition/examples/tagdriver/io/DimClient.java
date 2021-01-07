package com.inductiveautomation.ignition.examples.tagdriver.io;

import dim.DimServer;

public class DimClient{
	public void GetDnsNode(){
		int run=0;
		//DimService runNumber=new DimService("DELPHI/RUN_NUMBER",run);
		DimServer.start("RUN_INFO");
	}
}

