document.addEventListener('DOMContentLoaded', function() {
    const trigger = document.getElementById('guestsDropdownTrigger');
    const dropdown = document.getElementById('guestsDropdown');
    const closeBtn = document.getElementById('guestsCloseBtn');
    const guestsCountDisplay = document.getElementById('guestsCount');

    const maxGuests = parseInt(document.getElementById("maxGuests")?.value);

    const translations = {
        en : {
            'property.details.reservations.guest-1' : '1 guest',
            'property.details.reservations.guests-plural-lowercase' : 'guests'
        },
        de : {
            'property.details.reservations.guest-1' : '1 Gast',
            'property.details.reservations.guests-plural-lowercase' : 'Gäste'
        },
        bg : {
            'property.details.reservations.guest-1' : '1 гост',
            'property.details.reservations.guests-plural-lowercase' : 'гости'
        },
        es : {
            'property.details.reservations.guest-1' : '1 huésped',
            'property.details.reservations.guests-plural-lowercase' : 'huéspedes'
        }
    }

    // Get current language
    // const urlParams = new URLSearchParams(window.location.search);
    // const currentLanguage = urlParams.get('lang') || 'en';
    const currentLanguage = localStorage.getItem('preferredLanguageCode');


    let guests = 1;


    trigger.addEventListener('click', function() {
        trigger.classList.toggle('active');
        dropdown.classList.toggle('show');
    });

    closeBtn.addEventListener('click', function() {
        trigger.classList.remove('active');
        dropdown.classList.remove('show');
    });

    document.addEventListener('click', function(e) {
        if (!trigger.contains(e.target) && !dropdown.contains(e.target)) {
            trigger.classList.remove('active');
            dropdown.classList.remove('show');
        }
    });

    // Counter buttons
    document.querySelectorAll('.counter-btn').forEach(btn => {
        btn.addEventListener('click', function() {

            const isPlus = this.classList.contains('plus');

            if (isPlus) {
                guests++;
            } else {
                guests--;
            }

            updateDisplay();
        });
    });

    function updateDisplay() {
        // Update hidden inputs
        document.getElementById("hiddenGuestsCount").textContent = guests;

        let displayText = '';
        const guestCount = guests;

        displayText = getGuestsCountText(guestCount);


        guestsCountDisplay.textContent = displayText;

        document.querySelectorAll('.counter-btn').forEach(btn => {
            const isPlus = btn.classList.contains('plus');
            const isMinus = btn.classList.contains('minus');

            if (isPlus) {
                if (guests >= maxGuests) {
                    btn.disabled = true;
                } else {
                    btn.disabled = false;
                }
            }

            if (isMinus) {
                if (guests <= 1) {
                    btn.disabled = true;
                } else {
                    btn.disabled = false;
                }
            }

        });

        document.getElementById("guestsInput").value = guestCount;
    }

    function getGuestsCountText(guestCount) {
        const key = guestCount === 1
            ? 'property.details.reservations.guest-1'
            : 'property.details.reservations.guests-plural-lowercase';


        return guestCount === 1
            ? `${translations[currentLanguage][key]}`
            :  `${guestCount} ${translations[currentLanguage][key]}`;
    }


});