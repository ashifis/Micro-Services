package com.movieinfoservice.models;

public class Movie {

	private String movieId;
	private String name;
	private String movieOverview;
	
	
	
	
	
	public Movie(String movieId, String name, String movieOverview) {
		super();
		this.movieId = movieId;
		this.name = name;
		this.movieOverview = movieOverview;
	}
	public String getMovieId() {
		return movieId;
	}
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMovieOverview() {
		return movieOverview;
	}
	public void setMovieOverview(String movieOverview) {
		this.movieOverview = movieOverview;
	}
	@Override
	public String toString() {
		return "Movie [movieId=" + movieId + ", name=" + name + ", movieOverview=" + movieOverview + "]";
	}

	
	
	
}
