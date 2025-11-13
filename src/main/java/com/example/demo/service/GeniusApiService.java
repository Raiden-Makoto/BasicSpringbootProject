package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class GeniusApiService {

    public String searchSong(String artist, String title) {
        try {
            // Construct the Genius URL directly from artist and title
            String urlPath = formatUrlPath(artist, title);
            String songUrl = "https://genius.com/" + urlPath + "-lyrics";
            
            // Get lyrics from the song page
            String lyrics = getLyricsFromUrl(songUrl);
            return lyrics;
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

            // DEBUG: Dump HTML to file and console
            try {
                String html = doc.html();
                System.out.println("=== DEBUG: Full HTML length: " + html.length() + " ===");
                System.out.println("=== DEBUG: URL: " + songUrl + " ===");
                
                // Write to file
                try (PrintWriter writer = new PrintWriter(new FileWriter("genius_debug.html"))) {
                    writer.write(html);
                    System.out.println("=== DEBUG: HTML dumped to genius_debug.html ===");
                }
                
                // Print lyrics container info
                Element testContainer = doc.selectFirst("div[data-lyrics-container='true']");
                if (testContainer != null) {
                    System.out.println("=== DEBUG: Found lyrics container with data-lyrics-container='true' ===");
                    System.out.println("=== DEBUG: Container HTML (first 500 chars): " + testContainer.html().substring(0, Math.min(500, testContainer.html().length())) + " ===");
                } else {
                    System.out.println("=== DEBUG: NO lyrics container found with data-lyrics-container='true' ===");
                }
            } catch (IOException e) {
                System.err.println("Failed to write debug file: " + e.getMessage());
            }

            // Find ALL lyrics containers - there are multiple div[data-lyrics-container="true"]
            Elements lyricsContainers = doc.select("div[data-lyrics-container='true']");
            
            if (lyricsContainers.isEmpty()) {
                return "Could not find lyrics container. URL: " + songUrl;
            }
            
            StringBuilder lyricsBuilder = new StringBuilder();
            
            // Process each lyrics container in order
            for (Element container : lyricsContainers) {
                // Remove unwanted elements but keep the structure
                container.select("script, style, noscript, [class*='Contributors'], [class*='Translation'], [class*='description'], [class*='metadata'], [class*='header'], [class*='footer'], [class*='About'], [class*='Embed'], [class*='ReadMore'], [class*='SongHeader'], [class*='RightSidebar'], [class*='RecommendedSongs']").remove();
                
                // Extract text in order - walk through all child nodes
                extractTextInOrder(container, lyricsBuilder);
            }
            
            String lyrics = lyricsBuilder.toString();
            
            // Final cleanup - preserve verse markers and line breaks
            lyrics = cleanLyrics(lyrics);
            
            return lyrics.isEmpty() ? "Lyrics not available." : lyrics;
        } catch (Exception e) {
            return "Error fetching lyrics: " + e.getMessage();
        }
    }

    private void extractTextInOrder(Element element, StringBuilder builder) {
        // Process all child nodes in order
        for (org.jsoup.nodes.Node node : element.childNodes()) {
            if (node instanceof org.jsoup.nodes.TextNode) {
                // Direct text node (like [Verse 1], [Chorus])
                String text = ((org.jsoup.nodes.TextNode) node).text().trim();
                if (!text.isEmpty() && !isMetadata(text)) {
                    if (text.matches("\\[.*\\]")) {
                        // Verse/chorus marker - add with spacing
                        if (builder.length() > 0 && !builder.toString().endsWith("\n\n")) {
                            builder.append("\n");
                        }
                        builder.append(text).append("\n");
                    }
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                
                // Check if it's a <br> tag
                if (childElement.tagName().equalsIgnoreCase("br")) {
                    builder.append("\n");
                } else if (childElement.className().contains("Highlight")) {
                    // This is a lyrics span - extract text
                    String text = childElement.text().trim();
                    if (!text.isEmpty() && !isMetadata(text)) {
                        builder.append(text).append("\n");
                    }
                } else {
                    // Recursively process child elements
                    extractTextInOrder(childElement, builder);
                }
            }
        }
    }
    
    private boolean isMetadata(String text) {
        if (text == null || text.trim().isEmpty()) {
            return true;
        }
        
        String lower = text.toLowerCase();
        
        // Check for common metadata patterns
        if (lower.matches(".*\\d+\\s+contributors?.*")) return true;
        if (lower.contains("translation") || lower.contains("translations")) return true;
        if (lower.contains("read more")) return true;
        if (lower.contains("you might also like")) return true;
        if (lower.contains("embed")) return true;
        if (lower.matches(".*lyrics$") && text.length() < 20) return true; // "Red Lyrics" type headers
        if (lower.contains("česky") || lower.contains("català") || lower.contains("français") || 
            lower.contains("español") || lower.contains("português")) return true;
        if (lower.contains("the title track") || lower.contains("originally released") || 
            lower.contains("re-released") || lower.contains("album") && lower.contains("single")) return true;
        if (text.length() > 150 && (lower.contains("compares") || lower.contains("relationship") || 
            lower.contains("emotions"))) return true; // Description text
        
        return false;
    }
    
    private String cleanLyrics(String lyrics) {
        if (lyrics == null || lyrics.trim().isEmpty()) {
            return "";
        }
        
        // Split into lines and process
        String[] lines = lyrics.split("\\n");
        StringBuilder cleaned = new StringBuilder();
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Skip empty lines and metadata
            if (trimmed.isEmpty() || isMetadata(trimmed)) {
                continue;
            }
            
            // Keep verse/chorus markers like [Verse 1], [Chorus], etc.
            if (trimmed.matches("\\[.*\\]")) {
                if (cleaned.length() > 0 && !cleaned.toString().endsWith("\n\n")) {
                    cleaned.append("\n");
                }
                cleaned.append(trimmed).append("\n");
            } else {
                // Regular lyrics line
                cleaned.append(trimmed).append("\n");
            }
        }
        
        lyrics = cleaned.toString();
        
        // Normalize excessive line breaks (but keep double breaks after verse markers)
        lyrics = lyrics.replaceAll("\\n{4,}", "\n\n\n");
        lyrics = lyrics.replaceAll("\\n\\n\\n", "\n\n");
        
        return lyrics.trim();
    }
}

