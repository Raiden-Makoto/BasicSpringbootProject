document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.getElementById('searchForm');
    const resultsContainer = document.getElementById('results');
    
    if (searchForm) {
        searchForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const artist = document.getElementById('artist').value;
            const title = document.getElementById('title').value;
            
            // Show loading state
            resultsContainer.innerHTML = '<p>Searching...</p>';
            
            try {
                const formData = new URLSearchParams();
                formData.append('artist', artist);
                formData.append('title', title);
                
                const response = await fetch('/search', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });
                
                const data = await response.json();
                
                // Check if it's an error message
                if (data.result.startsWith('Error') || data.result.startsWith('Could not') || data.result.startsWith('Lyrics not available')) {
                    resultsContainer.innerHTML = '<p class="error-message">' + data.result + '</p>';
                } else {
                    // Display lyrics with nice formatting
                    resultsContainer.innerHTML = '<div class="lyrics-content">' + data.result + '</div>';
                }
            } catch (error) {
                resultsContainer.innerHTML = '<p class="error-message">Error: ' + error.message + '</p>';
            }
        });
    }
});

