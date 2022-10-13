package com.airthcompany.gradecalculator;

import android.widget.Button;

public class RowData {
//nbkhbkj
    private int id;
    private String name;
    private int score;
    private int weight;
    private Button button;

    public RowData(int id, String name, int score, int weight){
        this.id = id;
        this.name = name;
        this.score = score;
        this.weight = weight;
    }

    // captadores - mtraer

    public int getId(){  return id;    }
    public String getName(){  return name;  }
    public int getScore(){ return score; }
    public int getWeight(){ return weight; }


    // /Cambiadores-establecer

    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setScore(int score){ this.score = score; }
    public void setWeight(int weight){ this.weight = weight; }
    public void setButton(Button button){ this.button = button; }


}
