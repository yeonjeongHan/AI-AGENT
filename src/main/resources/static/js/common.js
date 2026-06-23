document.addEventListener("DOMContentLoaded", function () {
    const currentPage = location.pathname.split("/").pop() || "./index.html";

    document.querySelectorAll(".menu li").forEach(li => {
        const a = li.querySelector("a");
        if (a && a.getAttribute("href") === currentPage) {
            li.classList.add("active");
        }
    });
});
