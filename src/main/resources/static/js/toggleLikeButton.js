

const path = window.location.pathname;
const isManagePropertiesPage = path.includes("/properties/my-properties");


document.querySelectorAll(".favorite-btn")
    .forEach(button => {
        if (isManagePropertiesPage) {
            button.style.display = "none";
        } else {
            button.addEventListener("click", () => {
                button.classList.toggle("active");
            });
        }
    });









