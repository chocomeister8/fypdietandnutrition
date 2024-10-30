package com.fyp.dietandnutritionapplication;

import java.io.Serializable;

public class DietPreference implements Serializable {
    private String dietpreferenceId;
    private String dietpreference;


    public DietPreference(){

    }

    public DietPreference(String dietpreferenceId, String dietpreference) {
        this.dietpreferenceId = dietpreferenceId;
        this.dietpreference = dietpreference;
    }

    public String getDietPreferenceId(){ return dietpreferenceId;
    }

    public void setDietPreferenceId(String dietpreferenceId){ this.dietpreferenceId = dietpreferenceId;
    }

    public String getDietPreference() { return dietpreference;
    }

    public void setDietpreference(String dietpreference) { this.dietpreference = dietpreference;
    }

}


