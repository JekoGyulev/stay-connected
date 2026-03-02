const toggleInputs = document.querySelectorAll(".toggle-switch > input");
const actionsDiv = document.querySelector(".settings-actions");

const initialToggleInputs = [];

toggleInputs.forEach(toggle => {

    initialToggleInputs.push(toggle.checked);

    toggle.addEventListener("change", updateDirty);
});


function updateDirty() {

    let isDirty = false;

    toggleInputs.forEach((toggle, index) => {
        if (toggle.checked !== initialToggleInputs[index]) {
            isDirty = true;
        }
    });


    if (isDirty) {
        actionsDiv.classList.add("visible");
    } else {
        actionsDiv.classList.remove("visible");
    }
}

