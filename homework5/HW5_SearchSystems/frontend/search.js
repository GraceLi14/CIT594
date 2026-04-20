const API = 'http://localhost:8080';
const DEBOUNCE_MS = 150;

const input         = document.getElementById('search-input');
const suggestBox    = document.getElementById('suggestions');
const resultsMeta   = document.getElementById('results-meta');
const resultsList   = document.getElementById('results-list');
const spellDiv      = document.getElementById('spell-suggestion');
const searchBtn     = document.getElementById('search-btn');
const historyList   = document.getElementById('history-list');
const serverWarning = document.getElementById('server-warning');

let debounceTimer    = null;
let activeIndex      = -1;
let currentSuggestions = [];

// -----------------------------------------------------------------------
// Autocomplete
// -----------------------------------------------------------------------

async function fetchSuggestions(prefix) {
  if (!prefix) { hideSuggestions(); return; }
  try {
    const res  = await fetch(`${API}/autocomplete?q=${encodeURIComponent(prefix)}`);
    const data = await res.json();
    currentSuggestions = data;
    renderSuggestions(data, prefix);
  } catch (_) {
    hideSuggestions();
  }
}

function renderSuggestions(words, prefix) {
  suggestBox.innerHTML = '';
  activeIndex = -1;
  if (!words.length) { hideSuggestions(); return; }

  words.forEach((word, idx) => {
    const el = document.createElement('div');
    el.className = 'suggestion-item';
    el.innerHTML = word.toLowerCase().startsWith(prefix.toLowerCase())
      ? `<span class="match">${word.slice(0, prefix.length)}</span>${word.slice(prefix.length)}`
      : word;
    el.addEventListener('mousedown', e => { e.preventDefault(); pickSuggestion(word); });
    suggestBox.appendChild(el);
  });

  suggestBox.style.display = 'block';
}

function hideSuggestions() {
  suggestBox.style.display = 'none';
  suggestBox.innerHTML = '';
  activeIndex = -1;
}

function pickSuggestion(word) {
  input.value = word;
  hideSuggestions();
  performSearch(word);
}

// -----------------------------------------------------------------------
// Search
// -----------------------------------------------------------------------

async function performSearch(query) {
  query = query.trim();
  if (!query) return;

  resultsMeta.className   = 'loading';
  resultsMeta.textContent = 'Searching…';
  resultsList.innerHTML   = '';
  spellDiv.style.display  = 'none';
  serverWarning.style.display = 'none';

  try {
    const res     = await fetch(`${API}/search?q=${encodeURIComponent(query)}`);
    const results = await res.json();
    renderResults(results, query);
    loadHistory();
  } catch (_) {
    resultsMeta.className   = 'error';
    resultsMeta.textContent = '';
    serverWarning.style.display = 'block';
  }
}

async function renderResults(results, query) {
  resultsList.innerHTML = '';

  if (!results.length) {
    resultsMeta.className   = '';
    resultsMeta.textContent = `No results for "${query}"`;
    // Try spell check
    try {
      const res   = await fetch(`${API}/spellcheck?q=${encodeURIComponent(query)}`);
      const words = await res.json();
      if (words.length) {
        spellDiv.innerHTML     = `Did you mean: <a href="#">${words[0]}</a>?`;
        spellDiv.style.display = 'block';
        spellDiv.querySelector('a').addEventListener('click', e => {
          e.preventDefault();
          input.value = words[0];
          performSearch(words[0]);
        });
      }
    } catch (_) {}
    return;
  }

  resultsMeta.className   = '';
  resultsMeta.textContent = `${results.length} result${results.length !== 1 ? 's' : ''} for "${query}"`;
  results.forEach(doc => {
    const card = document.createElement('div');
    card.className   = 'result-card';
    card.textContent = doc.length > 120 ? doc.slice(0, 120) + '…' : doc;
    resultsList.appendChild(card);
  });
}

// -----------------------------------------------------------------------
// History
// -----------------------------------------------------------------------

async function loadHistory() {
  try {
    const res     = await fetch(`${API}/history`);
    const queries = await res.json();
    historyList.innerHTML = '';

    if (!queries.length) {
      historyList.innerHTML = '<div class="empty-state">No recent searches</div>';
      return;
    }

    queries.forEach(q => {
      const el = document.createElement('div');
      el.className = 'history-item';
      el.innerHTML = `<span class="history-icon">↩</span>${q}`;
      el.addEventListener('click', () => { input.value = q; performSearch(q); });
      historyList.appendChild(el);
    });
  } catch (_) {}
}

// -----------------------------------------------------------------------
// Event listeners
// -----------------------------------------------------------------------

input.addEventListener('input', () => {
  clearTimeout(debounceTimer);
  const val = input.value.trim();
  debounceTimer = setTimeout(() => fetchSuggestions(val), DEBOUNCE_MS);
});

input.addEventListener('keydown', e => {
  const items = suggestBox.querySelectorAll('.suggestion-item');

  if (e.key === 'ArrowDown') {
    e.preventDefault();
    activeIndex = Math.min(activeIndex + 1, items.length - 1);
  } else if (e.key === 'ArrowUp') {
    e.preventDefault();
    activeIndex = Math.max(activeIndex - 1, -1);
  } else if (e.key === 'Enter') {
    if (activeIndex >= 0 && items[activeIndex]) {
      pickSuggestion(currentSuggestions[activeIndex]);
    } else {
      hideSuggestions();
      performSearch(input.value);
    }
    return;
  } else if (e.key === 'Escape') {
    hideSuggestions();
    return;
  }

  items.forEach((el, i) => el.classList.toggle('active', i === activeIndex));
});

document.addEventListener('click', e => {
  if (!e.target.closest('.search-wrapper')) hideSuggestions();
});

searchBtn.addEventListener('click', () => {
  hideSuggestions();
  performSearch(input.value);
});

// Load history on page open
loadHistory();
