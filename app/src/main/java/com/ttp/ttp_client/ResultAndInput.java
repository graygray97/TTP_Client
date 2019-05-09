package com.ttp.ttp_client;

public class ResultAndInput {
    private String result;
    private AppTourInput tourInput;

    public ResultAndInput(String res, AppTourInput input){
        result = res;
        tourInput = input;
    }

    public String getResult() {
        return result;
    }

    public AppTourInput getTourInput() {
        return tourInput;
    }
}
