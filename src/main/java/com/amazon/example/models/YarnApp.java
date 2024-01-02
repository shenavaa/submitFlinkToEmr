package com.amazon.example.models;

public class YarnApp {
	private String name;
	private String trackingUrl;
	private String state;
	private Long startTime;
	private Long endTime;
	private String applicationType;
	private String applicationTags;
	private String id;
	private String amHostHttpAddress;
	private String user;
	private String amRPCAddress;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTrackingUrl() {
		return trackingUrl;
	}
	public void setTrackingUrl(String trackingUrl) {
		this.trackingUrl = trackingUrl;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getApplicationTags() {
		return applicationTags;
	}
	public void setApplicationTags(String applicationTags) {
		this.applicationTags = applicationTags;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAmHostHttpAddress() {
		return amHostHttpAddress;
	}
	public void setAmHostHttpAddress(String amHostHttpAddress) {
		this.amHostHttpAddress = amHostHttpAddress;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAmRPCAddress() {
		return amRPCAddress;
	}
	public void setAmRPCAddress(String amRPCAddress) {
		this.amRPCAddress = amRPCAddress;
	}
	

}
