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
                resultsContainer.innerHTML = '<p>' + data.result + '</p>';
            } catch (error) {
                resultsContainer.innerHTML = '<p>Error: ' + error.message + '</p>';
            }
        });
    }
});

