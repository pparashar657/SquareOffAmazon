package com.example.myapplication;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Package implements Serializable {

    @Exclude private String id;
    private String trackingId;
    private String sourceNode;
    private String targetNode;
    private String groupId;
    private String reconcilationState;
    private String processingState;
    private String transactionType;
    private String shipmenttype;
    private String currentNode;
    private long lastUpdatedTime;


    public Package(){

    }


    public Package(String trackingId, String sourceNode, String targetNode, String groupId, String reconcilationState, String processingState, String transactionType, String shipmenttype, String currentNode, long lastUpdatedTime) {
        this.trackingId = trackingId;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.groupId = groupId;
        this.reconcilationState = reconcilationState;
        this.processingState = processingState;
        this.transactionType = transactionType;
        this.shipmenttype = shipmenttype;
        this.currentNode = currentNode;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getReconcilationState() {
        return reconcilationState;
    }

    public String getProcessingState() {
        return processingState;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getShipmenttype() {
        return shipmenttype;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public String getId() {
        return id;
    }

    public void setReconcilationState(String reconcilationState) {
        this.reconcilationState = reconcilationState;
    }

    public void setProcessingState(String processingState) {
        this.processingState = processingState;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String searchbyId(List<Package> list, String id){

        for(Package p:list){
            if(p.getTrackingId().equals(id)){
                return p.getId();
            }
        }
        return null;
    }

    public static void sortByTime(List<Package> list){
        Collections.sort(list,new MyComp());
    }


    static class MyComp implements Comparator<Package> {

        @Override
        public int compare(Package p1, Package p2) {
            if(p1.getLastUpdatedTime() < p2.getLastUpdatedTime()){
                return 1;
            } else {
                return -1;
            }
        }
    }

}
