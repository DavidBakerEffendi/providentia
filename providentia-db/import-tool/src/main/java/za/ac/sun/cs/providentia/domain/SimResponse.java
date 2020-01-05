package za.ac.sun.cs.providentia.domain;

public class SimResponse {

    private float lat;
    private float lon;
    private int t;
    private int prio;
    private float timeToAmbulanceStarts;
    private float onSceneDuration;
    private boolean transfer;
    private float timeAtHospital;
    private float travelTimePatient;
    private float travelTimeHospital;
    private float travelTimeStation;
    private short resource;
    private float latDest;
    private float lonDest;
    private int zone;
    private int destZone;
    private int node;
    private int destNode;
    private double resourceReadyTime;
    private int id;

    public float getLat() {
        return lat;
    }

    public void setLat(final float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(final float lon) {
        this.lon = lon;
    }

    public int getT() {
        return t;
    }

    public void setT(final int t) {
        this.t = t;
    }

    public int getPrio() {
        return prio;
    }

    public void setPrio(final int prio) {
        this.prio = prio;
    }

    public float getTimeToAmbulanceStarts() {
        return timeToAmbulanceStarts;
    }

    public void setTimeToAmbulanceStarts(final float timeToAmbulanceStarts) {
        this.timeToAmbulanceStarts = timeToAmbulanceStarts;
    }

    public float getOnSceneDuration() {
        return onSceneDuration;
    }

    public void setOnSceneDuration(final float onSceneDuration) {
        this.onSceneDuration = onSceneDuration;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(final boolean transfer) {
        this.transfer = transfer;
    }

    public float getTimeAtHospital() {
        return timeAtHospital;
    }

    public void setTimeAtHospital(final float timeAtHospital) {
        this.timeAtHospital = timeAtHospital;
    }

    public float getTravelTimePatient() {
        return travelTimePatient;
    }

    public void setTravelTimePatient(final float travelTimePatient) {
        this.travelTimePatient = travelTimePatient;
    }

    public float getTravelTimeHospital() {
        return travelTimeHospital;
    }

    public void setTravelTimeHospital(final float travelTimeHospital) {
        this.travelTimeHospital = travelTimeHospital;
    }

    public float getTravelTimeStation() {
        return travelTimeStation;
    }

    public void setTravelTimeStation(final float travelTimeStation) {
        this.travelTimeStation = travelTimeStation;
    }

    public short getResource() {
        return resource;
    }

    public void setResource(final short resource) {
        this.resource = resource;
    }

    public float getLatDest() {
        return latDest;
    }

    public void setLatDest(final float latDest) {
        this.latDest = latDest;
    }

    public float getLonDest() {
        return lonDest;
    }

    public void setLonDest(final float lonDest) {
        this.lonDest = lonDest;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(final int zone) {
        this.zone = zone;
    }

    public int getDestZone() {
        return destZone;
    }

    public void setDestZone(final int destZone) {
        this.destZone = destZone;
    }

    public int getNode() {
        return node;
    }

    public void setNode(final int node) {
        this.node = node;
    }

    public int getDestNode() {
        return destNode;
    }

    public void setDestNode(final int destNode) {
        this.destNode = destNode;
    }

    public double getResourceReadyTime() {
        return resourceReadyTime;
    }

    public void setResourceReadyTime(final double resourceReadyTime) {
        this.resourceReadyTime = resourceReadyTime;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
