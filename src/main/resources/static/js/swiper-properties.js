const prevButton = document.getElementById("prev-property");
const nextButton = document.getElementById("next-property");
const propertiesContainer = document.querySelector(".properties-slider");
const featuredProperties = document.querySelectorAll(".property-card-new");


const widthOfProperty = featuredProperties[0].offsetWidth;

propertiesContainer.addEventListener("wheel", (e) => {
    e.preventDefault();
    propertiesContainer.scrollLeft += e.deltaY;
    propertiesContainer.style.scrollBehavior = "auto";
});


nextButton.onclick = () => {
    propertiesContainer.scrollLeft += widthOfProperty;
    propertiesContainer.style.scrollBehavior = "smooth";
}

prevButton.onclick = () => {
    propertiesContainer.scrollLeft -= widthOfProperty;
    propertiesContainer.style.scrollBehavior = "smooth";
}









