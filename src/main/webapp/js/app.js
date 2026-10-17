/* TapBite front-end helpers (vanilla JavaScript, no libraries). */
(function () {
    'use strict';

    /* 1. Image fallback: if a picture is missing, show the placeholder instead of a broken icon. */
    var placeholder = document.body.getAttribute('data-placeholder');

    function useFallback(img) {
        if (!placeholder || img.getAttribute('data-fallback-applied')) { return; }
        img.setAttribute('data-fallback-applied', 'true');   // prevents an endless error loop
        img.src = placeholder;
    }

    var images = document.querySelectorAll('img[data-fallback]');
    for (var i = 0; i < images.length; i++) {
        (function (img) {
            img.addEventListener('error', function () { useFallback(img); });
            // The error may already have happened before this script ran.
            if (img.complete && img.naturalWidth === 0 && img.getAttribute('src')) { useFallback(img); }
        })(images[i]);
    }

    /* 2. Home page: live search + cuisine filter over the cards the server already rendered. */
    var grid = document.getElementById('restaurant-grid');
    if (!grid) { return; }

    var cards = grid.querySelectorAll('.restaurant-card');
    var searchBox = document.getElementById('restaurant-search');
    var chips = document.querySelectorAll('.chip[data-cuisine]');
    var noMatch = document.getElementById('no-match');
    var counter = document.getElementById('result-count');
    var activeCuisine = '';

    function applyFilters() {
        var text = searchBox ? searchBox.value.trim().toLowerCase() : '';
        var visible = 0;
        for (var c = 0; c < cards.length; c++) {
            var card = cards[c];
            var matchesText = text === '' || card.getAttribute('data-search').indexOf(text) !== -1;
            var matchesCuisine = activeCuisine === '' || card.getAttribute('data-cuisine') === activeCuisine;
            var show = matchesText && matchesCuisine;
            card.hidden = !show;
            if (show) { visible++; }
        }
        if (counter) { counter.textContent = visible + (visible === 1 ? ' restaurant' : ' restaurants'); }
        if (noMatch) { noMatch.hidden = visible !== 0; }
    }

    if (searchBox) { searchBox.addEventListener('input', applyFilters); }

    for (var k = 0; k < chips.length; k++) {
        (function (chip) {
            chip.addEventListener('click', function () {
                activeCuisine = chip.getAttribute('data-cuisine');
                for (var j = 0; j < chips.length; j++) {
                    var on = chips[j] === chip;
                    chips[j].classList.toggle('active', on);
                    chips[j].setAttribute('aria-pressed', on ? 'true' : 'false');
                }
                applyFilters();
            });
        })(chips[k]);
    }
})();
