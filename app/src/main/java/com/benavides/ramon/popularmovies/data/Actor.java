package com.benavides.ramon.popularmovies.data;

/**
 * Function:
 */
public class Actor {
    private int id;
    private String character;
    private String name;
    private String picture;

    private int movieId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", character='" + character + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", movieId=" + movieId +
                '}';
    }
}
