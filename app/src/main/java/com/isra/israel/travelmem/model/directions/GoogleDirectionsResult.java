package com.isra.israel.travelmem.model.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GoogleDirectionsResult {

    @SerializedName("routes")
    @Expose
    private ArrayList<Route> routes;

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }
}
