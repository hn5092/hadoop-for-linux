package com.xym.hadoop.asyncdispatcher;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;

public class TestDAsyncDispatcherForMachineState {
	public static void main(String[] args) throws Exception {
		String jobID = "job_20131215_12";
		MachineStateMast machineStateMast = new MachineStateMast("Simple MRAppMaster", jobID,
				5);
		YarnConfiguration conf = new YarnConfiguration(new Configuration());
		machineStateMast.serviceInit(conf);
		//因为这里是多服务,所以这里得到的server 是需要get出来的 如果较多服务直接使用for循环启动
		Service service = machineStateMast.getServices().get(0);
		service.start();
		//因为add服务的时候是 add一个object对象!!!!!!  拿出来的时候是要自己转型的
		Dispatcher dispatcher = (Dispatcher) service;
		//通过调度器启动事件
		dispatcher.getEventHandler().handle( new JobEvent(jobID, JobEventType.JOB_PREPARE));
		
	}
}
