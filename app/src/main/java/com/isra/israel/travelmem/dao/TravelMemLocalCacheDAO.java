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

public class TravelMemLocalCacheDAO {

    private static final String PATH_TRAVEL_MEM = "travel_mem";
    private static final String EXT_TRAVEL = ".travel";
    private static final String PATH_TRAVELS = PATH_TRAVEL_MEM + "/travels";

    private static File getTravelsDir(Context context) {
        File travelsDir = new File(context.getCacheDir().getPath() + "/" + PATH_TRAVELS);
        if (!travelsDir.exists()) {
            travelsDir.mkdirs();
        }

        return travelsDir;
    }

    public static ArrayList<Travel> getTravels(Context context) {
        File travelsDir = getTravelsDir(context);

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

    public static boolean doesTravelExist(Context context, String id) {
        ArrayList<Travel> travels = getTravels(context);

        for (Travel travel : travels) {
            if (travel.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public static void addTravel(Context context, Travel travel) {
        File travelsDir = getTravelsDir(context);

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

    public static void deleteTravel(Context context, String id) {
        ArrayList<Travel> travels = getTravels(context);

        for (Travel travel : travels) {
            if (travel.getId().equals(id)) {
                File travelFile = new File(getTravelsDir(context).getPath() + "/" + travel.getId() + EXT_TRAVEL);

                travelFile.delete();
            }
        }
    }

    public static void updateTravel(Context context, Travel inTravel) {
        ArrayList<Travel> travels = getTravels(context);

        for (Travel travel : travels) {
            if (travel.getId().equals(inTravel.getId())) {
                File tempTravelFile = new File(getTravelsDir(context).getPath() + "/" + travel.getId() + EXT_TRAVEL + ".tmp");

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

                File travelFile = new File(getTravelsDir(context).getPath() + "/" + travel.getId() + EXT_TRAVEL);

                tempTravelFile.renameTo(travelFile);
            }
        }
    }

}
