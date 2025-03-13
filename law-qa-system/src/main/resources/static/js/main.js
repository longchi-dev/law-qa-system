document.addEventListener("DOMContentLoaded", function () {
    const editButton = document.getElementById("editButton");
    const saveButton = document.getElementById("saveButton");
    const form = document.getElementById("updateForm");
    const closeBtn = document.getElementById('closeDropdown');
    const dropdownContent = document.getElementById('profileDropdown');
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            // Xóa lớp 'show' để ẩn dropdown
            dropdownContent.classList.remove('show');
        });
    }
    editButton.addEventListener("click", function () {
        document.querySelectorAll(".view-mode").forEach(el => el.classList.add("d-none"));
        document.querySelectorAll(".edit-mode").forEach(el => el.classList.remove("d-none"));
        editButton.classList.add("d-none");
        saveButton.classList.remove("d-none");
    });

    form.addEventListener("submit", function () {
        document.querySelectorAll(".view-mode").forEach(el => el.classList.remove("d-none"));
        document.querySelectorAll(".edit-mode").forEach(el => el.classList.add("d-none"));
        editButton.classList.remove("d-none");
        saveButton.classList.add("d-none");
    });
});


document.addEventListener("DOMContentLoaded", function () {
    // Lấy ngày hiện tại
    let date6months = new Date();
    let date1month = new Date();
    let date1year = new Date();

    // Tính toán ngày hết hạn
    date6months.setMonth(date6months.getMonth() + 6);
    date1month.setMonth(date1month.getMonth() + 1);
    date1year.setFullYear(date1year.getFullYear() + 1);

    // Format ngày tháng theo dd/mm/yyyy
    let formattedDate6months = date6months.toLocaleDateString("vi-VN");
    let formattedDate1month = date1month.toLocaleDateString("vi-VN");
    let formattedDate1year = date1year.toLocaleDateString("vi-VN");

    // Lấy phần tử và cập nhật nếu tồn tại
    let expiry6 = document.getElementById("expiryDate6months");
    let expiry1 = document.getElementById("expiryDate1month");
    let expiry12 = document.getElementById("expiryDate1year");

    if (expiry6) expiry6.innerText = formattedDate6months;
    if (expiry1) expiry1.innerText = formattedDate1month;
    if (expiry12) expiry12.innerText = formattedDate1year;
});

document.addEventListener("DOMContentLoaded", () => {
    var paymentSuccessElement = document.getElementById('paymentSuccessData');
    var paymentFailElement = document.getElementById('paymentFailData');

    var paymentSuccess = paymentSuccessElement ? paymentSuccessElement.getAttribute('data-payment-success') : null;
    var paymentFail = paymentFailElement ? paymentFailElement.getAttribute('data-payment-success') : null;

    var modalSuccessElement = document.getElementById('paymentSuccessModal');
    var modalFailElement = document.getElementById('paymentFailModal');

    // Kiểm tra modal trước khi khởi tạo
    if (modalSuccessElement && paymentSuccess === 'true') {
        var modalSuccess = new bootstrap.Modal(modalSuccessElement);
        modalSuccess.show();
    }

    if (modalFailElement && paymentSuccess === 'false') {
        var modalFail = new bootstrap.Modal(modalFailElement);
        modalFail.show();
    }
});


const cards = document.querySelectorAll('.order-card');
const payButton = document.getElementById('payButton');
cards.forEach(card => {
    card.addEventListener('click', function() {
        // Bỏ chọn tất cả các card
        cards.forEach(c => c.classList.remove('selected'));
        // Thêm class 'selected' cho card được chọn
        this.classList.add('selected');
        // Kích hoạt nút "Tiếp tục" nếu có card được chọn
        payButton.disabled = false;
    });
});
const amountButtons = document.querySelectorAll('.payment-amount');
let selectedAmount = null;
if(amountButtons) {
    amountButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Lưu giá trị mệnh giá đã chọn
            selectedAmount = this.getAttribute('data-amount');
            // Hiển thị mệnh giá người dùng đã chọn
            console.log("Mệnh giá đã chọn: " + selectedAmount);
        });
    });
}
if(payButton){
    payButton.addEventListener('click', function() {
        // Kiểm tra lại nếu không còn card nào được chọn, thì disable nút
        if (!document.querySelector('.order-card.selected')) {
            payButton.disabled = true;
        }
        if (selectedAmount) {
            // Gửi yêu cầu thanh toán với mệnh giá đã chọn
            fetch(`/api/v1/payment/vn-pay?amount=${selectedAmount}&bankCode=`)
                .then(response => response.json())
                .then(data => {
                    if (data.code === 200 && data.data.paymentUrl) {
                        // Điều hướng đến URL thanh toán
                        window.location.href = data.data.paymentUrl;
                    } else {
                        alert("Payment failed. Please try again.");
                    }
                })
                .catch(error => {
                    alert("An error occurred. Please try again.");
                });
        } else {
            alert("Please select an amount.");
        }
    });
}
$(document).ready(function () {
    $(".nav2 ul li").click(function () {
        $(this).addClass("active").siblings().removeClass("active");

        let index = $(this).index();

        $(".profile-body .tab").hide();

        $(".profile-body .tab").eq(index).show();
    });

    $(".profile-body .tab").hide();
    $(".profile-body .tab").first().show();
});

let tab = document.querySelectorAll(".tab");

function tabs(panelIndex) {
    tab.forEach(function(node) {
        node.style.display = "none";
    });
    tab[panelIndex].style.display = "block";
}

tabs(0);



// Lắng nghe sự kiện click trên icon đóng
const toggleAdvancedSearch = document.getElementById('toggleAdvancedSearch');
if(toggleAdvancedSearch){
    toggleAdvancedSearch.addEventListener('click', function () {
            const advancedForm = document.getElementById('searchAdvancedForm');
            if (advancedForm.style.display === 'none' || !advancedForm.style.display) {
                advancedForm.style.display = 'flex';
            } else {
                advancedForm.style.display = 'none';
            }
        })
}

// Lấy các phần tử button và div chứa nội dung dropdown
const btnToggle = document.querySelector('.btn-toggle1');
const contentDrop = document.querySelector('.drop-down-content');
if (btnToggle){
    btnToggle.addEventListener('click', function() {
        // Kiểm tra trạng thái hiển thị của nội dung
        if (contentDrop.style.display === 'none' || contentDrop.style.display === '') {
            contentDrop.style.display = 'block';  // Hiển thị nội dung
        } else {
            contentDrop.style.display = 'none';   // Ẩn nội dung
        }
    });
}
// Lắng nghe sự kiện click vào button


function toggleDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    dropdown.classList.toggle('show');
}
$(document).on('click', function (event) {
    const dropdown = document.getElementById('profileDropdown');
    const gridProfile = document.querySelector('.grid-profile');
    if (gridProfile && !gridProfile.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// Script to handle city, district, and ward dropdowns using external JSON data
var citis = document.getElementById("city");
var districts = document.getElementById("district");
var wards = document.getElementById("ward");

var Parameter = {
    url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
    method: "GET",
    responseType: "application/json",
};

// Fetch data from the provided URL
axios(Parameter).then(function (result) {
    renderCity(result.data);
}).catch(function (error) {
    console.error("Error fetching data:", error);
});

// Function to populate city dropdown
function renderCity(data) {
    for (const x of data) {
        citis.options[citis.options.length] = new Option(x.Name, x.Id);
    }

    // Event listener for city dropdown
    citis.onchange = function () {
        districts.length = 1; // Reset district dropdown
        wards.length = 1; // Reset ward dropdown

        if (this.value !== "") {
            const result = data.filter(n => n.Id === this.value);

            for (const k of result[0].Districts) {
                districts.options[districts.options.length] = new Option(k.Name, k.Id);
            }
        }
    };

    // Event listener for district dropdown
    districts.onchange = function () {
        wards.length = 1; // Reset ward dropdown

        const dataCity = data.filter(n => n.Id === citis.value);
        if (this.value !== "") {
            const dataWards = dataCity[0].Districts.filter(n => n.Id === this.value)[0].Wards;

            for (const w of dataWards) {
                wards.options[wards.options.length] = new Option(w.Name, w.Id);
            }
        }
    };
}


document.addEventListener("DOMContentLoaded", function () {
    const tabItems = document.querySelectorAll(".nav-link");  // All tab navigation items
    const tabContents = document.querySelectorAll(".tab-pane");  // All tab content panels

    if (tabItems.length === 0 || tabContents.length === 0) {
        console.error("No tabs or tab contents found in the DOM!");
        return;  // Exit if no tabs or tab contents exist
    }

    // Function to display the content of a specific tab
    function showTabContent(panelIndex) {
        // Hide all tabs and contents first
        tabItems.forEach(function (tab) {
            tab.classList.remove("active");
        });
        tabContents.forEach(function (content) {
            content.classList.remove("show", "active");
        });

        // Check if the index is within bounds
        if (panelIndex >= 0 && panelIndex < tabItems.length) {
            // Activate the corresponding tab and content
            tabItems[panelIndex].classList.add("active");
            tabContents[panelIndex].classList.add("show", "active");
        } else {
            console.error("Invalid tab index: " + panelIndex);
        }
    }

    // Default behavior: Show the first tab
    showTabContent(0);  // Ensure there's at least one tab before calling this

    // Event listener for tab changes
    tabItems.forEach(function (tabItem, index) {
        tabItem.addEventListener("click", function () {
            console.log("Tab clicked: ", index);

            showTabContent(index);  // Show the corresponding content when clicked
        });
    });
});