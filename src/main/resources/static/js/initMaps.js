async function initMap() {
    let locationDescription = document.querySelector('.property-location-link').textContent;

    locationDescription = locationDescription.replace('📍', '').trim();

    const map = L.map('map').setView([0, 0], 2);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
        maxZoom: 15
    }).addTo(map);

    try {
        const response = await fetch(
            `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(locationDescription)}`
        );

        const data = await response.json();

        if (data && data.length > 0) {
            const lat = parseFloat(data[0].lat);
            const lon = parseFloat(data[0].lon);
            const propertyLocation = [lat, lon];

            map.setView(propertyLocation, 10);

            L.marker(propertyLocation)
                .addTo(map)
                .bindPopup(locationDescription)
                .openPopup();
        } else {
            console.error('Location not found by geocoder.');
        }

    } catch (error) {
        console.error('Geocoding error:', error);
    }
}

initMap();
