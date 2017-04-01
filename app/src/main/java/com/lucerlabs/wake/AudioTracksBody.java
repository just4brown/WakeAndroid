package com.lucerlabs.wake;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioTracksBody {

	@JsonProperty("tracks")
	private List<String> tracks;

	public AudioTracksBody() {
	}

	/**
	 *
	 * @param tracks
	 */
	public AudioTracksBody(List<String> tracks) {
		this.tracks = tracks;
	}

	/**
	 *
	 * @return
	 * The tracks
	 */
	@JsonProperty("tracks")
	public List<String> getTracks() {
		return this.tracks;
	}
}


