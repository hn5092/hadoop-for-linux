package com.xym.hadoop.yarn.yarnapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.applications.distributedshell.Client;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.CallbackHandler;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyApplicationMaster {
	private static final Log LOG = LogFactory.getLog(MyApplicationMaster.class);
	static YarnClient yarnClient;
	static YarnConfiguration yarnConf;

	@BeforeClass
	public static void setup() {
		yarnConf = new YarnConfiguration(new Configuration());
		CallbackHandler callbackHandler = new MyCallbackHandle();
		InetSocketAddress socketAddr = yarnConf.getSocketAddr(
				YarnConfiguration.RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_PORT);
		System.out.println(socketAddr.getHostName() + "|"
				+ socketAddr.getHostString() + "|" + socketAddr.getPort() + "|"
				+ socketAddr.getAddress());
		yarnConf.setSocketAddr("yarn.resourcemanager.address",
				new InetSocketAddress("192.168.80.103", 8032));
		socketAddr = yarnConf.getSocketAddr(YarnConfiguration.RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_PORT);
		System.out.println(socketAddr.getHostName() + "|"
				+ socketAddr.getHostString() + "|" + socketAddr.getPort() + "|"
				+ socketAddr.getAddress());
	}

	@Test
	public void testCluster() {
		YarnClient yarnClient = YarnClient.createYarnClient();
		yarnClient.init(yarnConf);
		yarnClient.start();
		YarnClusterMetrics clusterMetrics;
		try {
			clusterMetrics = yarnClient.getYarnClusterMetrics();
			System.out.println("this is the cluster name"
					+ yarnClient.getName());
			LOG.info("Got Cluster metric info from ASM" + ", numNodeManagers="
					+ clusterMetrics.getNumNodeManagers());
			//get  
			List<NodeReport> clusterNodeReports = yarnClient
					.getNodeReports(NodeState.RUNNING);
			LOG.info("Got Cluster node info from ASM");
			for (NodeReport node : clusterNodeReports) {
				LOG.info("Got node report from ASM for" + ", nodeId="
						+ node.getNodeId() + ", nodeAddress"
						+ node.getHttpAddress() + ", nodeRackName"
						+ node.getRackName() + ", nodeNumContainers"
						+ node.getNumContainers());
//				 QueueInfo queueInfo = yarnClient.getQueueInfo(this.amQueue);
//				    LOG.info("Queue info"
//				        + ", queueName=" + queueInfo.getQueueName()
//				        + ", queueCurrentCapacity=" + queueInfo.getCurrentCapacity()
//				        + ", queueMaxCapacity=" + queueInfo.getMaximumCapacity()
//				        + ", queueApplicationCount=" + queueInfo.getApplications().size()
//				        + ", queueChildQueueCount=" + queueInfo.getChildQueues().size());		

				    List<QueueUserACLInfo> listAclInfo = yarnClient.getQueueAclsInfo();
				    for (QueueUserACLInfo aclInfo : listAclInfo) {
				      for (QueueACL userAcl : aclInfo.getUserAcls()) {
				        LOG.info("User ACL Info for Queue"
				            + ", queueName=" + aclInfo.getQueueName()			
				            + ", userAcl=" + userAcl.name());
				      }
			}}
			YarnClientApplication app = yarnClient.createApplication();
		} catch (YarnException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testClient() {
		try {
			yarnConf.set("fs.defaultFS", "hdfs://xym01:9000");
			Client client = new Client(yarnConf);
			String[] args ={"-jar","src/hadoop-1620141472975040262.jar","-shell_command","ls","-num_containers","10","-container_memory","350","-master_memory","350"};
			client.init(args);
			client.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
