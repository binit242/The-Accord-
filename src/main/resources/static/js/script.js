console.log("script loaded");

// Get theme from local storage or default to light
let currentTheme = getTheme();
applyTheme(currentTheme);

// Function to apply the current theme
function applyTheme(theme) {
    document.querySelector('html').classList.add(theme);
}

// Listen for theme toggle button click
const changeThemeBtn = document.querySelector('#theme_change');
if (changeThemeBtn) {
    changeThemeBtn.addEventListener("click", () => {
        const oldTheme = currentTheme;
        currentTheme = currentTheme === "dark" ? "light" : "dark";

        // Update the HTML tag
        document.querySelector('html').classList.remove(oldTheme);
        document.querySelector('html').classList.add(currentTheme);

        // Save to local storage
        setTheme(currentTheme);
    });
}

// Save theme to local storage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

// Get theme from local storage
function getTheme() {
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light";
}

document.addEventListener("DOMContentLoaded", () => {
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
        anchor.addEventListener("click", (event) => {
            const targetId = anchor.getAttribute("href");
            if (!targetId || targetId.length <= 1) return;

            const target = document.querySelector(targetId);
            if (!target) return;

            event.preventDefault();
            target.scrollIntoView({
                behavior: prefersReducedMotion ? "auto" : "smooth",
                block: "start",
            });
        });
    });

    if (prefersReducedMotion) return;

    const revealSelectors = [
        "section:not(.hero-reference)",
        "section:not(.hero-reference) > div",
        ".hero-title-area",
        ".hero-review-chip",
        ".hero-info-card",
        ".hero-mini-card",
        ".feature-kicker",
        ".feature-stage h2",
        ".feature-card-field > div",
        ".accord-cta-section .container",
        ".testimonial-lane > div",
        ".accord-contact-section .container",
        ".accord-footer > .container",
        "form",
        "table",
        ".block.p-6",
        ".rounded-3xl",
        ".rounded-2xl",
        ".max-w-7xl > div",
        ".max-w-[85rem] > div",
        ".animate__animated",
    ];

    const microRevealSelectors = [
        "section:not(.hero-reference) h1",
        "section:not(.hero-reference) h2",
        "section:not(.hero-reference) h3",
        "section:not(.hero-reference) h4",
        "section:not(.hero-reference) p",
        "section:not(.hero-reference) li",
        "section:not(.hero-reference) a:not(.testimonial-card a)",
        "section:not(.hero-reference) button",
        "section:not(.hero-reference) input",
        "section:not(.hero-reference) textarea",
        "section:not(.hero-reference) label",
        "section:not(.hero-reference) img",
        "section:not(.hero-reference) svg",
        "section:not(.hero-reference) i",
        "section:not(.hero-reference) .fa",
        "section:not(.hero-reference) .fas",
        "section:not(.hero-reference) .fab",
        "section:not(.hero-reference) .material-icons",
        "table tr",
        "table th",
        "table td",
        ".accord-footer h2",
        ".accord-footer h3",
        ".accord-footer p",
        ".accord-footer li",
        ".accord-footer a",
    ];

    const revealTargets = new Set();
    const revealVariants = [
        "reveal-rise",
        "reveal-left",
        "reveal-right",
        "reveal-pop",
        "reveal-tilt",
        "reveal-glide",
    ];
    const microRevealVariants = [
        "text-reveal-wave",
        "text-reveal-split",
        "text-reveal-focus",
        "text-reveal-arc",
        "text-reveal-stamp",
        "text-reveal-slide",
        "text-reveal-glow",
    ];

    const assignRevealVariant = (element, index) => {
        if (element.classList.contains("hero-title-area")) return "reveal-left";
        if (element.classList.contains("hero-review-chip")) return "reveal-right";
        if (element.classList.contains("hero-info-card")) return "reveal-tilt";
        if (element.classList.contains("hero-mini-card")) return "reveal-pop";
        if (element.matches(".feature-card-field > div, .rounded-3xl")) return "reveal-pop";
        if (element.matches(".feature-kicker, .feature-stage h2")) return "reveal-glide";
        if (element.matches(".accord-cta-section .container")) return "reveal-zoom";
        if (element.matches("form")) return "reveal-left";
        if (element.matches("table")) return "reveal-glide";
        if (element.matches(".accord-footer > .container")) return "reveal-rise";
        if (element.matches(".max-w-7xl > div, .max-w-[85rem] > div")) return index % 2 === 0 ? "reveal-left" : "reveal-right";
        return revealVariants[index % revealVariants.length];
    };

    revealSelectors.forEach((selector) => {
        document.querySelectorAll(selector).forEach((element, index) => {
            if (element.closest("nav, .hero-shell-nav, .hero-nav-card, .hero-mobile-menu, .sidebar, .user-sidebar")) return;
            if (element.matches(".hero-phone, .hero-gray-panel, .hero-right-panel, .testimonial-track, .testimonial-card")) return;

            element.classList.add("scroll-reveal-target");
            element.classList.add(assignRevealVariant(element, index));
            element.style.setProperty("--scroll-reveal-delay", `${Math.min(index * 55, 260)}ms`);
            revealTargets.add(element);
        });
    });

    microRevealSelectors.forEach((selector) => {
        document.querySelectorAll(selector).forEach((element, index) => {
            if (element.closest("nav, .hero-shell-nav, .hero-nav-card, .hero-mobile-menu, .sidebar, .user-sidebar")) return;
            if (element.closest(".testimonial-track, .testimonial-card")) return;
            if (element.closest(".scroll-reveal-target") === element) return;
            if (element.matches("script, style, br, option")) return;

            const variant = microRevealVariants[(index + element.tagName.length) % microRevealVariants.length];
            element.classList.add("scroll-reveal-target", "micro-reveal-target", variant);
            element.style.setProperty("--scroll-reveal-delay", `${Math.min((index % 8) * 38, 266)}ms`);
            revealTargets.add(element);
        });
    });

    if (!("IntersectionObserver" in window)) {
        revealTargets.forEach((element) => element.classList.add("is-visible"));
        return;
    }

    document.documentElement.classList.add("scroll-motion-ready");

    const replayReveal = (element) => {
        element.classList.remove("is-visible");
        void element.offsetWidth;
        element.classList.add("is-visible");
    };

    const revealObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                replayReveal(entry.target);
                return;
            }

            entry.target.classList.remove("is-visible");
        });
    }, {
        threshold: 0.18,
        rootMargin: "0px 0px -10% 0px",
    });

    revealTargets.forEach((element) => revealObserver.observe(element));

    const parallaxTargets = [
        { element: document.querySelector(".hero-phone"), speed: -0.08 },
        { element: document.querySelector(".hero-gray-panel"), speed: 0.035 },
        { element: document.querySelector(".hero-right-panel"), speed: 0.045 },
    ].filter((item) => item.element);

    let ticking = false;
    const updateParallax = () => {
        const scrollY = window.scrollY || window.pageYOffset;
        parallaxTargets.forEach(({ element, speed }) => {
            const offset = Math.max(-42, Math.min(42, scrollY * speed));
            element.style.setProperty("--parallax-y", `${offset.toFixed(2)}px`);
        });
        ticking = false;
    };

    const requestParallax = () => {
        if (ticking) return;
        ticking = true;
        window.requestAnimationFrame(updateParallax);
    };

    updateParallax();
    window.addEventListener("scroll", requestParallax, { passive: true });
});
