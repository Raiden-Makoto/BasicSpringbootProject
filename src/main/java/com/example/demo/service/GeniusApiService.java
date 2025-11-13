package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class GeniusApiService {

    public String searchSong(String artist, String title) {
        try {
            // Construct the Genius URL directly from artist and title
            String urlPath = formatUrlPath(artist, title);
            String songUrl = "https://genius.com/" + urlPath + "-lyrics";
            
            // Get lyrics from the song page
            String lyrics = getLyricsFromUrl(songUrl);
            return "Song: " + title + "\nArtist: " + artist + "\n\n" + lyrics;
        } catch (Exception e) {
            return "Error searching for lyrics: " + e.getMessage();
        }
    }

    private String formatUrlPath(String artist, String title) {
        // Format artist: preserve capitalization, replace spaces with hyphens, remove special chars
        String formattedArtist = artist
                .replaceAll("[^a-zA-Z0-9\\s-]", "") // Remove special characters except spaces and hyphens
                .trim()
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-"); // Replace multiple hyphens with single hyphen
        
        // Format title: convert to lowercase, replace spaces with hyphens, remove special chars
        String formattedTitle = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters except spaces and hyphens
                .trim()
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-"); // Replace multiple hyphens with single hyphen
        
        return formattedArtist + "-" + formattedTitle;
    }

    private String getLyricsFromUrl(String songUrl) {
        try {
            // Fetch the HTML page with realistic browser headers to avoid 403 errors
            Document doc = Jsoup.connect(songUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Cache-Control", "max-age=0")
                    .referrer("https://www.google.com/")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            // Extract lyrics from the div with data-lyrics-container attribute
            Element lyricsContainer = doc.selectFirst("div[data-lyrics-container='true']");
            
            if (lyricsContainer != null) {
                // Get all text from the lyrics container, preserving line breaks
                String lyrics = lyricsContainer.select("div").stream()
                        .map(Element::text)
                        .collect(Collectors.joining("\n"));
                
                if (lyrics.isEmpty()) {
                    // Fallback: try to get text directly
                    lyrics = lyricsContainer.text();
                }
                
                return lyrics.isEmpty() ? "Lyrics not available. URL: " + songUrl : lyrics;
            } else {
                // Fallback: try alternative selectors
                Element lyricsDiv = doc.selectFirst("div.lyrics");
                if (lyricsDiv != null) {
                    return lyricsDiv.text();
                }
                return "Could not extract lyrics. URL: " + songUrl;
            }
        } catch (Exception e) {
            return "Error fetching lyrics: " + e.getMessage();
        }
    }
}

