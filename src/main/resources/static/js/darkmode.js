
    const root = document.documentElement;
    const themeOptions = document.querySelectorAll('input[name="theme"]');


    function applySystemTheme() {
        const prefersDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
        root.classList.toggle("dark-mode", prefersDarkMode);
    }


    themeOptions.forEach(option => {

        option.addEventListener("change", (e) => {
            const selectedTheme = e.target.value;

            if (selectedTheme === "dark") {
                root.classList.add("dark-mode");
            } else if (selectedTheme === "light") {
                root.classList.remove("dark-mode");
            } else if (selectedTheme === "system") {
                applySystemTheme();
            }


            localStorage.setItem("theme", selectedTheme);
        });

    });


    const preference = localStorage.getItem("theme") || "light";

    const savedRadio = document.querySelector(
        `input[name="theme"][value="${preference}"]`
    );

    if (savedRadio) {
        savedRadio.checked = true;
    }

    if (preference === "dark") {
        root.classList.add("dark-mode");

    } else if (preference === "system") {
        applySystemTheme();
    }



