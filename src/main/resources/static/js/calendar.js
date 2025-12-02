
    let currentMonth = 11; // December
    let currentYear = 2025;

    let checkIn = null;
    let checkOut = null;

    const calendar = document.getElementById("calendar");
    const monthLabel = document.getElementById("currentMonth");

    const checkInInput = document.getElementById("checkInInput");
    const checkOutInput = document.getElementById("checkOutInput");

    const totalPriceEl = document.getElementById("totalPrice");
    const totalPriceHidden = document.getElementById("totalPriceHidden");

    // Set your property price
    const pricePerNight = parseFloat(document.getElementById("pricePerNight")?.dataset.price);
    const cleaningFee = parseFloat(document.getElementById("cleaningFee").dataset.fee);
    const serviceFee = parseFloat(document.getElementById("serviceFee").dataset.fee);

    function renderCalendar() {
    calendar.innerHTML = "";

    const today = new Date();
    today.setHours(0,0,0,0);

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

    // Block past dates
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

    calendar.innerHTML += `<div class="${classes}" onclick="dateClick(${day})">${day}</div>`;
}
}

    function sameDate(a, b) {
        return a.getDate() === b.getDate() &&
        a.getMonth() === b.getMonth() &&
        a.getFullYear() === b.getFullYear();
}

    function format(date) {
        // Returns yyyy-MM-dd
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // months are 0-indexed
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }


    function dateClick(day) {
    const date = new Date(currentYear, currentMonth, day);
    const today = new Date();
    today.setHours(0,0,0,0);

    if (date < today) return; // block past days

    // Pick check-in
    if (!checkIn || (checkIn && checkOut)) {
    checkIn = date;
    checkOut = null;
    updateInputs();
    renderCalendar();
    return;
}

    // Pick check-out
    if (date > checkIn) {
    checkOut = date;
    updateInputs();
    updatePrice();
    renderCalendar();
    return;
}

    // If clicked before check-in → restart
    checkIn = date;
    checkOut = null;
    updateInputs();
    renderCalendar();
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

    document.getElementById("nextMonth").onclick = () => {
    currentMonth++;
    if (currentMonth > 11) {
    currentMonth = 0;
    currentYear++;
}
    renderCalendar();
};

    document.getElementById("prevMonth").onclick = () => {
    currentMonth--;
    if (currentMonth < 0) {
    currentMonth = 11;
    currentYear--;
}
    renderCalendar();
};

    renderCalendar();
