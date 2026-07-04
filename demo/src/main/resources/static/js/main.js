document.addEventListener('DOMContentLoaded', () => {
  const links = document.querySelectorAll('.nav-link');
  links.forEach(l => {
    if (l.getAttribute('href') === location.pathname) l.style.color = 'var(--accent)';
  });
});
