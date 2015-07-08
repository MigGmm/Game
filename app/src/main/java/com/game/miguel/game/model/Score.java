package com.game.miguel.game.model;

/**
 * Model for work with the scores.
 */
public class Score {

    private String name;
    private int score;

    public Score() {
    }

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getNombre() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
