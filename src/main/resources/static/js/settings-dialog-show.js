const logoutButton = document.getElementById('settingsLogout');
const dialog = document.getElementById('dialog');

const showLogoutDialog = (show) => {
    if (show) {
        dialog.showModal();
        requestAnimationFrame(() => {
            dialog.classList.add('show');
        });
    } else {
        dialog.classList.remove('show');
        setTimeout(() => dialog.close(), 150);
    }
};


logoutButton.addEventListener("click", () => showLogoutDialog(true));
