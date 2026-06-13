console.log("Contacts.js");

// Detect environment and set correct baseURL
let baseURL;
if (location.hostname === "localhost") {
  baseURL = "http://localhost:8081";
} else {
  baseURL = "https://www.scm20.site";
}

// Modal options
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    setTimeout(() => {
      const contactModal = document.getElementById("view_contact_modal");
      if (contactModal) contactModal.classList.add("scale-100");
    }, 50);
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// Modal instance options
const instanceOptions = {
  id: "view_contact_modal",
  override: true,
};

// Initialize modal after DOM loads
document.addEventListener("DOMContentLoaded", () => {
  const viewContactModal = document.getElementById("view_contact_modal");
  if (viewContactModal) {
    const contactModal = new Modal(viewContactModal, options, instanceOptions);
    window.contactModal = contactModal;

    // Expose modal functions globally
    window.openContactModal = () => contactModal.show();
    window.closeContactModal = () => contactModal.hide();
  } else {
    console.warn("Modal element with id 'view_contact_modal' not found.");
  }
});

// Load contact data and show modal
async function loadContactdata(id) {
  console.log("Loading contact with ID:", id);
  try {
    const response = await fetch(`${baseURL}/api/contacts/${id}`);
    const data = await response.json();
    console.log("Contact data:", data);

    document.querySelector("#contact_name").innerHTML = data.name;
    document.querySelector("#contact_email").innerHTML = data.email;
    document.querySelector("#contact_image").src = data.picture;
    document.querySelector("#contact_address").innerHTML = data.address;
    document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
    document.querySelector("#contact_about").innerHTML = data.description;

    const contactFavorite = document.querySelector("#contact_favorite");
    contactFavorite.innerHTML = data.favorite
      ? "❤️"
      : "💔";

   document.querySelector("#contact_website").href = data.websiteLink;
document.querySelector("#contact_linkedIn").href = data.linkedInLink;
document.querySelector("#contact_facebook").href = data.facebookLink;
document.querySelector("#contact_instagram").href = data.instaLink;


   

    if (window.contactModal) {
      window.contactModal.show();
    } else {
      console.error("Modal not initialized.");
    }
  } catch (error) {
    console.error("Error loading contact:", error);
  }
}

// Delete contact with confirmation
async function deleteContact(id) {
  Swal.fire({
   title: "Do you want to delete the contact?",
  icon: "warning",
  showCancelButton: true,
  confirmButtonText: "Delete",
  cancelButtonText: "Cancel",
  customClass: {
    popup: 'custom-swal-popup',
    title: 'custom-swal-title',
    confirmButton: 'swal2-confirm-button',
    cancelButton: 'swal2-cancel-button',
  },
  buttonsStyling: false,
  }).then((result) => {
    if (result.isConfirmed) {
      const url = `${baseURL}/user/contacts/delete/${id}`;
      window.location.replace(url);
    }
  });
}
