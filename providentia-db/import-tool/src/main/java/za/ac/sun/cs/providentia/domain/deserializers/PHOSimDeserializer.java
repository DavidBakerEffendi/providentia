package za.ac.sun.cs.providentia.domain.deserializers;

import za.ac.sun.cs.providentia.domain.SimResponse;

public class PHOSimDeserializer {

    public static SimResponse deserialize(String[] line) {
        SimResponse sim = new SimResponse();

        sim.setX(Float.parseFloat(line[0]));
        sim.setY(Float.parseFloat(line[1]));
        sim.setT(Integer.parseInt(line[2]));
        sim.setPrio(Integer.parseInt(line[3]));
        sim.setTimeToAmbulanceStarts(Float.parseFloat(line[4]));
        sim.setOnSceneDuration(Float.parseFloat(line[5]));
        sim.setTransfer("1".equals(line[6]));
        sim.setTimeAtHospital(Float.parseFloat(line[7]));
        sim.setTravelTimePatient(Float.parseFloat(line[8]));
        sim.setTravelTimeHospital(Float.parseFloat(line[9]));
        sim.setTravelTimeStation(Float.parseFloat(line[10]));
        sim.setResource(Short.parseShort(line[11]));
        sim.setxDest(Float.parseFloat(line[12]));
        sim.setyDest(Float.parseFloat(line[13]));
        sim.setZone(Integer.parseInt(line[14]));
        sim.setDestZone(Integer.parseInt(line[15]));
        sim.setNode(Integer.parseInt(line[16]));
        sim.setDestNode(Integer.parseInt(line[17]));
        sim.setResourceReadyTime(Float.parseFloat(line[18]));
        sim.setId(Integer.parseInt(line[19]));

        return sim;
    }

}
