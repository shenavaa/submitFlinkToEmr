package com.amazon.example;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.Cluster;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterRequest;
import com.amazonaws.services.elasticmapreduce.model.Instance;
import com.amazonaws.services.elasticmapreduce.model.ListInstancesRequest;


public class EmrUtils {
	private AmazonElasticMapReduce emrClient = EmrUtils.initEmrUtils();
	private static AWSCredentials credentials_profile = null;		
	
	static private AmazonElasticMapReduce initEmrUtils() {
		try {
			credentials_profile = new DefaultAWSCredentialsProviderChain().getCredentials();
	    } catch (Exception e) {
	        throw new AmazonClientException(
	                "Cannot load credentials from .aws/credentials file. " +
	                "Make sure that the credentials file exists and the profile name is specified within it.",
	                e);
	    }
		
		AmazonElasticMapReduce emrClient = AmazonElasticMapReduceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials_profile))
				.withRegion(Regions.valueOf(Constants.REGION_NAME))
				.build();
		
		return emrClient;
		
	}
	
	public List<String> getMastersPrivateNames() {

		DescribeClusterRequest describeRequest = new DescribeClusterRequest().withClusterId(Constants.CLUSTER_ID);
		Cluster cluster = emrClient.describeCluster(describeRequest).getCluster();
		List<Instance> instances = null;
		if (cluster.getInstanceCollectionType().contentEquals("INSTANCE_GROUP")) { //InstanceGroup
			instances = emrClient.listInstances(new ListInstancesRequest().withClusterId(Constants.CLUSTER_ID).withInstanceGroupTypes("MASTER")).getInstances();
			
		} else { // Instance Fleet
			instances = emrClient.listInstances(new ListInstancesRequest().withClusterId(Constants.CLUSTER_ID).withInstanceFleetType("MASTER")).getInstances();
		}
		ArrayList<String> names = new ArrayList<String>();
		for (Instance instance: instances) {
			if (instance.getStatus().getState().contains("RUNNING")) {
				names.add(instance.getPrivateDnsName()); 
			}
		}
		
		return names;
	}
	
	
}

