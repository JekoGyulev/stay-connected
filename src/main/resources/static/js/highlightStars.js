
const starInputs = document.querySelectorAll('.star-input');

let ratingInput = document.getElementById("rating-input");

let selectedRating = 0;

starInputs.forEach(star => {
    star.addEventListener('click', function() {
        selectedRating = this.getAttribute('data-rating');
        ratingInput.value = selectedRating;
        updateStarDisplay();
    });

    star.addEventListener('mouseover', function() {
        const rating = this.getAttribute('data-rating');
        highlightStars(rating);
    });
});

document.querySelector('.star-rating-input')
    .addEventListener('mouseout', function() {
    highlightStars(selectedRating);
});

function highlightStars(rating) {
    starInputs.forEach((star, index) => {
        if (index < rating) {
            star.textContent = '★';
            star.style.color = 'var(--primary)';
        } else {
            star.textContent = '☆';
            star.style.color = 'var(--muted)';
        }
    });
}

function updateStarDisplay() {
    highlightStars(selectedRating);
}