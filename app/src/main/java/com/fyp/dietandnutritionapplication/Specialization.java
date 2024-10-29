package com.fyp.dietandnutritionapplication;

import java.io.Serializable;

public class Specialization implements Serializable {
    private String specId;
    private String name;


    public Specialization(){

    }

    public Specialization(String specId, String name) {
        this.specId = specId;
        this.name = name;
    }

    public String getSpecId(){ return specId;
    }

    public void setSpecId(String specId){ this.specId = specId;
    }

    public String getName() { return name;
    }

    public void setName(String name) { this.name = name;
    }

}


