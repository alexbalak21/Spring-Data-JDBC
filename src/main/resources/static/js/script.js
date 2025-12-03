const button = document.querySelector('button');
const counter = document.querySelector('#counter');

let count = 0;

button.addEventListener('click', () => {
    count++;
    counter.textContent = count;
});