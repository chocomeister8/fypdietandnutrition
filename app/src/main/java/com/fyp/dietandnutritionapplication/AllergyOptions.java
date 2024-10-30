package com.fyp.dietandnutritionapplication;

import java.io.Serializable;

public class AllergyOptions implements Serializable {
    private String allergyOptionId;
    private String allergyOption;


    public AllergyOptions(){

    }

    public AllergyOptions(String allergyOptionId, String allergyOption) {
        this.allergyOptionId = allergyOptionId;
        this.allergyOption = allergyOption;
    }

    public String getAllergyOptionId(){ return allergyOptionId;
    }

    public void setAllergyOptionId(String allergyOptionId){ this.allergyOptionId = allergyOptionId;
    }

    public String getAllergyOption() { return allergyOption;
    }

    public void setAllergyOption(String allergyOption) { this.allergyOption = allergyOption;
    }
}


