const tags = [];

document.getElementById('tagInput')?.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' || e.key === ',') {
    e.preventDefault();
    const val = e.target.value.trim().replace(/^#/, '');
    if (val && !tags.includes(val) && tags.length < 10) { tags.push(val); renderTags(); }
    e.target.value = '';
  }
});

function renderTags() {
  const list = document.getElementById('tagList');
  const hidden = document.getElementById('hiddenTags');
  list.innerHTML = ''; hidden.innerHTML = '';
  tags.forEach((tag, i) => {
    const el = document.createElement('span');
    el.className = 'tag-item';
    el.innerHTML = `#${tag} <span class="tag-remove" onclick="removeTag(${i})">x</span>`;
    list.appendChild(el);
    const input = document.createElement('input');
    input.type = 'hidden'; input.name = 'tags'; input.value = tag;
    hidden.appendChild(input);
  });
}

function removeTag(i) { tags.splice(i, 1); renderTags(); }
