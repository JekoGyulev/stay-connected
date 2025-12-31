const input = document.getElementById('language-search-input');


input.addEventListener('keyup', (e) => {

    const searchLanguage = input.value.toLowerCase();

    const languages = document.querySelectorAll('span.language-name');

    languages.forEach(language => {

        if (language.textContent.toLowerCase().includes(searchLanguage)) {
            language.parentNode.parentNode.parentNode.style.display = 'flex';
        } else {
            language.parentNode.parentNode.parentNode.style.display = 'none';
        }

    });

});



