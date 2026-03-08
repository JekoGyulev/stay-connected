let checkIn = null;
let checkOut = null;

const calendar = document.getElementById("calendar");
const monthLabel = document.getElementById("currentMonth");


const checkInInput = document.getElementById("checkInInput");
const checkOutInput = document.getElementById("checkOutInput");


const totalPriceEl = document.getElementById("totalPrice");
const totalPriceHidden = document.getElementById("totalPriceHidden");


const pricePerNight = parseFloat(document.getElementById("pricePerNight")?.dataset.price);
const cleaningFee = parseFloat(document.getElementById("cleaningFee").dataset.fee);
const serviceFee = parseFloat(document.getElementById("serviceFee").dataset.fee);


const today = new Date();
today.setHours(0, 0, 0, 0);

let currentMonth = today.getMonth();
let currentYear = today.getFullYear();


const request = async (url) => {
    const response = await fetch(url);
    return await response.json();
}


function renderCalendar() {
    calendar.innerHTML = "";

    const firstDay = new Date(currentYear, currentMonth, 1).getDay();
    const lastDay = new Date(currentYear, currentMonth + 1, 0).getDate();

    monthLabel.textContent =
        new Date(currentYear, currentMonth).toLocaleDateString("en-US", {
            month: "long",
            year: "numeric"
        });

    for (let i = 0; i < firstDay; i++) {
        calendar.innerHTML += `<div class="calendar-day empty"></div>`;
    }

    for (let day = 1; day <= lastDay; day++) {
        const date = new Date(currentYear, currentMonth, day);

        let classes = "calendar-day";

        if (date < today) {
            classes += " disabled";
        } else {
            classes += "";
        }

        if (checkIn && sameDate(date, checkIn)) classes += " selected";
        if (checkOut && sameDate(date, checkOut)) classes += " selected";

        if (checkIn && checkOut && date > checkIn && date < checkOut) {
            classes += " in-range";
        }

        let formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;

        calendar.innerHTML += `<div class="${classes}" data-date="${formattedDate}"  onclick="dateClick(${day})">${day}</div>`;
    }
}

function sameDate(a, b) {
    return a.getDate() === b.getDate() &&
        a.getMonth() === b.getMonth() &&
        a.getFullYear() === b.getFullYear();
}

function format(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${day}/${month}/${year}`;
}

async function dateClick(day) {
    const date = new Date(currentYear, currentMonth, day);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (date < today) return;

    if (!checkIn || (checkIn && checkOut)) {
        checkIn = date;
        checkOut = null;
        updateInputs();
        renderCalendar();
        await loadBookedDays();

        return;
    }


    if (date > checkIn) {

        let blocked = false;

        const daysOfMonth = document.querySelectorAll(".calendar-day");

        for (let day of daysOfMonth) {

            if (day.classList.contains("empty")) continue;

            const dayDate = new Date(day.dataset.date);

            if (dayDate > checkIn && dayDate < date && day.classList.contains("disabled")) {
                blocked = true;
                break;
            }
        }


        if (blocked) {
            alert("You cannot select through booked days!");
            return;
        }



        checkOut = date;
        updateInputs();
        updatePrice();
        renderCalendar();
        await loadBookedDays();
        return;
    }


    checkIn = date;
    checkOut = null;
    updateInputs();
    renderCalendar();
    await loadBookedDays();
}

function updateInputs() {
    checkInInput.value = checkIn ? format(checkIn) : "";
    checkOutInput.value = checkOut ? format(checkOut) : "";
}

function updatePrice() {
    if (!checkIn || !checkOut) {
        document.getElementById("priceBreakdown").textContent = "€0 × 0 nights";
        document.getElementById("subtotal").textContent = "€0";
        totalPriceEl.textContent = "€0";
        return;
    }

    const diffTime = checkOut - checkIn;
    const nights = diffTime / (1000 * 60 * 60 * 24);
    const subtotal = nights * pricePerNight;

    document.getElementById("priceBreakdown").textContent =
        "€" + pricePerNight + " × " + nights + " nights";

    document.getElementById("subtotal").textContent =
        "€" + subtotal.toFixed(2);


    const total = subtotal + serviceFee + cleaningFee;

    totalPriceEl.textContent =
        "€" + total.toFixed(2);

    totalPriceHidden.value = total.toFixed(2);
}


async function loadBookedDays() {
    const currentPropertyId = window.location.pathname.split("/")[2];

    const listOfBookedDatesForProperty =
        await request(`http://localhost:8081/api/v1/reservations/${currentPropertyId}/booked-dates`);

    const allDaysOfCurrentMonth = document.querySelectorAll(".calendar-day");

    for (let bookingDateObject of listOfBookedDatesForProperty) {

        const checkIn = bookingDateObject.checkIn;
        const checkOut = bookingDateObject.checkOut;

        for (let day of allDaysOfCurrentMonth) {
            let currentDate = day.dataset.date;

            if (currentDate >= checkIn && currentDate <= checkOut) {
                day.classList.add("disabled");
            }
        }
    }

}

document.getElementById("nextMonth").onclick = () => {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    renderCalendar();
    loadBookedDays();
};

document.getElementById("prevMonth").onclick = () => {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    renderCalendar();
    loadBookedDays();
};

renderCalendar();
loadBookedDays();




