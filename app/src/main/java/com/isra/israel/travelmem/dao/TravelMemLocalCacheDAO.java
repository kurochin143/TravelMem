package com.isra.israel.travelmem.dao;

import android.content.Context;

import com.isra.israel.travelmem.gson.TravelMemGson;
import com.isra.israel.travelmem.model.Travel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// TODO NOW user dir
public class TravelMemLocalCacheDAO {

    private static final String PATH_TRAVEL_MEM = "travel_mem";
    private static final String EXT_TRAVEL = ".travel";
    private static final String PATH_TRAVELS = PATH_TRAVEL_MEM + "/travels";

    private static File getTravelsDir(Context context, String uid) {
        File travelsDir = new File(context.getCacheDir().getPath() + "/" + PATH_TRAVELS + "/" + uid);
        if (!travelsDir.exists()) {
            travelsDir.mkdirs();
        }

        return travelsDir;
    }

    public static ArrayList<Travel> getTravels(Context context, String uid) {
        File travelsDir = getTravelsDir(context, uid);

        File[] travelFiles = travelsDir.listFiles();

        ArrayList<Travel> travels = new ArrayList<>();
        travels.ensureCapacity(travelFiles.length);
        for (File travelFile : travelFiles) {
            try {
                FileReader fileReader = new FileReader(travelFile);

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                StringBuilder travelJsonStrB = new StringBuilder();
                travelJsonStrB.ensureCapacity((int)travelFile.length());
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    travelJsonStrB.append(line);
                }
                bufferedReader.close();

                travels.add(TravelMemGson.gson.fromJson(travelJsonStrB.toString(), Travel.class));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return travels;
    }

    public static boolean doesTravelExist(Context context, String id, String uid) {
        ArrayList<Travel> travels = getTravels(context, uid);

        for (Travel travel : travels) {
            if (travel.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public static void addTravel(Context context, String uid, Travel travel) {
        File travelsDir = getTravelsDir(context, uid);

        File travelFile = new File(travelsDir.getPath() + "/" + travel.getId() + EXT_TRAVEL);

        if (!travelFile.exists()) {
            try {
                travelFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(travelFile);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(TravelMemGson.gson.toJson(travel));
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteTravel(Context context, String uid, String id) {
        ArrayList<Travel> travels = getTravels(context, uid);

        for (Travel travel : travels) {
            if (travel.getId().equals(id)) {
                File travelFile = new File(getTravelsDir(context, uid).getPath() + "/" + travel.getId() + EXT_TRAVEL);

                travelFile.delete();
            }
        }
    }

    public static void updateTravel(Context context, String uid, Travel inTravel) {
        ArrayList<Travel> travels = getTravels(context, uid);

        for (Travel travel : travels) {
            if (travel.getId().equals(inTravel.getId())) {
                File tempTravelFile = new File(getTravelsDir(context, uid).getPath() + "/" + travel.getId() + EXT_TRAVEL + ".tmp");

                try {
                    tempTravelFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileWriter fileWriter = new FileWriter(tempTravelFile);

                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(TravelMemGson.gson.toJson(inTravel));
                    bufferedWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                File travelFile = new File(getTravelsDir(context, uid).getPath() + "/" + travel.getId() + EXT_TRAVEL);

                tempTravelFile.renameTo(travelFile);
            }
        }
    }

    public static ArrayList<Travel> updateTravels(Context context, String uid, ArrayList<Travel> inTravels) {
        ArrayList<Travel> travels = getTravels(context, uid);

        ArrayList<Travel> newTravels = new ArrayList<>();
        for (int i = 0; i < inTravels.size(); ++i) {
            Travel inTravel = inTravels.get(i);
            boolean found = false;
            for (int j = 0; j < travels.size(); ++j) {
                Travel travel = travels.get(j);
                if (inTravel.getId().equals(travel.getId())) {
                    found = true;
                    // inTravel is newer
                    if (inTravel.getLastUpdatedTime() > travel.getLastUpdatedTime()) {
                        travels.set(j, inTravel);
                        updateTravel(context, uid, inTravel);
                    }
                }
            }

            if (!found) {
                newTravels.add(inTravel);
                addTravel(context, uid, inTravel);
            }
        }

        travels.addAll(newTravels);
        return travels;
    }

}
