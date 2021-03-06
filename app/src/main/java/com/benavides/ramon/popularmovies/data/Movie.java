package com.benavides.ramon.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie bean class
 */
public class Movie implements Parcelable {

    private int id;
    private String poster;
    private String originalTitle;
    private String synopsis;
    private double rating;
    private String releaseDate;
    private String backdrop;
    private int order;

    public Movie() {
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        originalTitle = in.readString();
        synopsis = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
        backdrop = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", poster='" + poster + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
                ", backdrop='" + backdrop + '\'' +
                ", order=" + order +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster);
        dest.writeString(originalTitle);
        dest.writeString(synopsis);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeString(backdrop);
    }


}
