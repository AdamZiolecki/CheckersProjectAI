package com.example.adam.checkersproject;

import sac.State;
import sac.StateFunction;

public class HeuristicRandom extends StateFunction {
    public double calculate(State state) {
        return Math.random() % 10;
    }
}
