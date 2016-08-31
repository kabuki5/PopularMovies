package com.benavides.ramon.popularmovies.data;

public class Trailer {

    private final static String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
    private String id;
    private String name;
    private String source;
    private int movieID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = YOUTUBE_PATH + source;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }
}
