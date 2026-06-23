document.addEventListener("DOMContentLoaded", function () {
    fetch("./components/sidebar.html")
        .then(response => {
            if (!response.ok) throw new Error("sidebar 불러오기 실패: " + response.status);
            return response.text();
        })
        .then(html => {
            const sidebarContainer = document.getElementById("sidebar-container");
            if (!sidebarContainer) return;

            sidebarContainer.innerHTML = html;

            const currentPage = location.pathname.split("/").pop() || "./index.html";
            document.querySelectorAll(".menu a").forEach(a => {
                if (a.getAttribute("href") === currentPage) {
                    a.classList.add("active");
                }
            });
        })
        .catch(console.error);
});
