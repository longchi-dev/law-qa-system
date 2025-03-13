document.addEventListener("DOMContentLoaded", () => {
    const chatBox = document.getElementById("chatBox");
    let currentChatId = null;
    const messageInput = document.getElementById("messageInput");
    const sendButton = document.getElementById("sendButton");
    const addLink = document.querySelector(".add");
    document.querySelectorAll(".options-btn").forEach(button => {
        button.addEventListener("click", function (event) {
            event.stopPropagation(); // Ngăn chặn sự kiện lan ra ngoài
        });
    });
    if (addLink) {
        addLink.addEventListener("click", function (event) {
            event.preventDefault(); // Ngừng hành động mặc định của liên kết

            currentChatId = null;

            // Tùy thuộc vào mục đích, bạn có thể chuyển hướng tới một trang khác (ví dụ: "/chat")
            window.location.href = "/chat"; // Chuyển hướng tới trang tạo chat mới hoặc nơi bạn muốn
        });
    }
    sendButton.addEventListener("click", (event) => {
        event.preventDefault();

        const message = messageInput.value.trim();
        if (message === "") return;

        appendMessage("user", message);
        messageInput.value = "";

        showLoader();
        const chatIdParam = currentChatId ? `&chatId=${currentChatId}` : '';

        fetch(`/chat/send?${chatIdParam}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": "Bearer your-api-key"
            },
            body: `message=${encodeURIComponent(message)}`
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                try {
                    if (data) {
                        appendMessage("ai", data, true); // Gọi appendMessage với isAI = true
                        updateChatList({id: Date.now(), choices: [{ message: { content: "Đoạn chat mới" } }]});
                        hideLoader();
                    } else {
                        appendMessage("ai", "Error: Invalid AI response.");
                    }
                } catch (e) {
                    appendMessage("ai", data);
                    console.error("Fetch error (not JSON):", e);
                }
            })
            .catch(error => {
                console.error("Fetch error:", error);
                appendMessage("ai", "Error: Unable to connect to the server.");
            });
    });
    const chatLinks = document.querySelectorAll(".chat-link");
    chatLinks.forEach(link => {
        link.addEventListener("click", function (event) {
            event.preventDefault(); // Ngừng hành động mặc định của liên kết (chuyển hướng)
            currentChatId = this.getAttribute("data-chat-id");
            fetchMessages(currentChatId); // Lấy chatId từ thuộc tính data
            highlightCurrentChat(this);
            console.log(this);
        });
    });
    function appendMessage(role, content, isAI = false) {
        console.log("Appending message:", role, content);

        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message", role);

        if (isAI) {
            content = content
                .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>") // **đậm** -> <strong>
                .replace(/__(.*?)__/g, "<em>$1</em>") // __nghiêng__ -> <em>
                .replace(/\n/g, "<br>"); // Xuống dòng
            messageDiv.innerHTML = "";

            typeTextEffect(messageDiv, content);
        } else {
            messageDiv.textContent = content;
        }

        chatBox.appendChild(messageDiv);
        chatBox.scrollTop = chatBox.scrollHeight;
    }
    function fetchMessages(chatId) {
        chatBox.innerHTML = '';  // Xóa các tin nhắn cũ trước khi tải lại

        // Gửi yêu cầu lấy tin nhắn
        fetch(`/chat/messages?chatId=${chatId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();  // Chuyển đổi dữ liệu thành JSON
            })
            .then(data => {
                hideLoader();
                if (Array.isArray(data)) {
                    data.forEach(message => {
                        appendMessage("user", message.question);  // Hiển thị câu hỏi của người dùng

                        let aiResponse = "";
                        console.log("Raw AI Response:", message.response);

                        try {
                            // Nếu có dữ liệu phản hồi hợp lệ từ AI
                            const parsedResponse = JSON.parse(message.response);
                            aiResponse = parsedResponse?.choices?.[0]?.message?.content || "Không có phản hồi từ AI.";
                        } catch (e) {
                            aiResponse = message.response || "Không thể xử lý phản hồi từ AI.";
                        }

                        appendMessage("ai", aiResponse, false);
                    });
                } else {
                    console.error("Không có tin nhắn hoặc dữ liệu không hợp lệ.");
                }
            })
            .catch(error => {
                hideLoader();
                console.error("Error fetching messages:", error);
            });
    }
    function updateChatList(data) {
        const chatListContainer = document.querySelector(".chat-list");
        console.log(data)
        if (data && data.id && data.choices && Array.isArray(data.choices)) {
            const chat = {
                id: data.id,
                title: data.choices[0].message.content // Lấy tiêu đề từ phản hồi của AI
            };

            // Tạo phần tử mới cho chat
            const chatItem = document.createElement("a");
            chatItem.classList.add("d-flex", "align-items-center", "chat-link");
            chatItem.setAttribute("data-chat-id", chat.id);

            chatItem.innerHTML = `
    
             <div class="flex-grow-1 ms-3 chat-link">
                <p style="font-weight: bolder;">Đoạn chat mới</p>
            </div>
        `;

            // Thêm phần tử chat mới vào chat list
           const latestChat = chatListContainer.querySelector(".chat-link:first-child");
            if (latestChat) {
                const latestChatId = latestChat.getAttribute("data-chat-id");
                chatItem.setAttribute("data-chat-id", latestChatId);
            }
            chatListContainer.insertBefore(chatItem, chatListContainer.firstChild);

            chatItem.addEventListener("click", function (event) {
                event.preventDefault(); // Ngăn load lại trang
                const chatId = parseInt(this.getAttribute("data-chat-id")); // Lấy chat ID từ data-attribute
                console.log(chatId)
                if (chatId) {
                    fetchMessages(chatId+1); // Gửi request để lấy tin nhắn của đoạn chat
                    highlightCurrentChat(chatItem); // Đánh dấu chat được chọn
                } else {
                    console.error("Không tìm thấy data-chat-id!");
                }
            });
        } else {
            console.error("Dữ liệu không hợp lệ hoặc thiếu thông tin để tạo chat list.");
        }


    }
    function highlightCurrentChat(selectedChatItem) {
        document.querySelectorAll(".chat-link p").forEach(p => {
            p.style.fontWeight = "normal";
        });
        const chatTitles = document.querySelectorAll(".chat-link p");
        const title2 = selectedChatItem.querySelector(".chat-link");
        chatTitles.forEach(p => {
            const chatItem = p.closest(".chat-link");  // Lấy phần tử <a> cha của <p>
            if (chatItem === title2) {
                p.style.fontWeight = "bold";  // Nếu phần tử này được chọn, làm cho chữ đậm
            }
            else {
                p.style.fontWeight = "normal";  // Nếu không phải, đặt chữ bình thường
            }
        });
    }
    document.querySelectorAll(".rename-chat").forEach(button => {
        button.addEventListener("click", function () {
            let chatItem = this.closest(".chat-link");
            let titleElement = chatItem.querySelector("p");
            if (chatItem.querySelector("input")) return;

            let input = document.createElement("input");
            input.type = "text";
            input.className = "form-control";
            input.value = titleElement.textContent.trim();
            // Thay thế p bằng input
            titleElement.replaceWith(input);
            input.focus();
            // Khi nhấn Enter hoặc mất focus thì cập nhật tên mới
            function saveTitle() {
                let newTitle = input.value.trim() || "Chưa đặt tên";
                let newTitleElement = document.createElement("p");
                let chatId = chatItem.getAttribute("data-chat-id");
                newTitleElement.textContent = newTitle;

                input.replaceWith(newTitleElement);
                fetch(`/chat/${chatId}/rename`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ newTitle: newTitle })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.error) {
                            alert(data.error);
                        } else {
                            newTitleElement.textContent = data.newTitle;
                        }
                    })
                    .catch(error => console.error("Lỗi:", error));

            }
            input.addEventListener("blur", saveTitle);
            input.addEventListener("keypress", function (event) {
                if (event.key === "Enter") {
                    saveTitle();
                }
            });
        });
    });
    document.querySelectorAll(".share-chat").forEach(button => {
        button.addEventListener("click", function () {
            let chatId = this.getAttribute("data-chat-id");

            fetch(`/chat/${chatId}/share`, { method: "POST" })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        alert("Lỗi: " + data.error);
                    } else {
                        let shareLink = data.shareableLink;
                        showShareOptions(shareLink);
                    }
                })
                .catch(error => console.error("Lỗi:", error));
        });
    });
    function showShareOptions(shareLink) {
        let optionsHtml = `
        <div class="modal fade" id="shareModal" tabindex="-1" aria-labelledby="shareModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Chia sẻ đoạn chat</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <input type="text" class="form-control mb-3" value="${shareLink}" readonly>
                <button class="btn btn-primary w-100 mb-2 share-button" data-share-link="${shareLink}">
                  <i class="fa-brands fa-facebook"></i> Chia sẻ lên Facebook
                </button>
                <button class="btn btn-info w-100 mb-2 text-white share-button" data-share-link="${shareLink}">
                  <i class="fa-solid fa-comments"></i> Chia sẻ lên Zalo
                </button>
                <button class="btn btn-dark w-100 share-button" data-share-link="${shareLink}">
                  <i class="fa-brands fa-twitter"></i> Chia sẻ lên Twitter
                </button>
              </div>
            </div>
          </div>
        </div>
    `;

        let modalContainer = document.createElement("div");
        modalContainer.innerHTML = optionsHtml;
        document.body.appendChild(modalContainer);
        new bootstrap.Modal(document.getElementById("shareModal")).show();
    }
    document.body.addEventListener("click", function(event) {
        if (event.target.classList.contains("share-button")) {
            let link = event.target.getAttribute("data-share-link");
            console.log("Link to share: ", link); // Log to check if the link is correct

            // Lựa chọn hành động chia sẻ tùy thuộc vào loại nút
            if (event.target.classList.contains("btn-primary")) {
                shareToFacebook(link);
            } else if (event.target.classList.contains("btn-info")) {
                shareToZalo(link);
            } else if (event.target.classList.contains("btn-dark")) {
                shareToTwitter(link);
            }
        }
    });
    function shareToFacebook(link) {
        window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(link)}`, "_blank");
    }
    function shareToZalo(link) {
        window.open(`https://zalo.me/share?url=${encodeURIComponent(link)}`, "_blank");
    }
    function shareToTwitter(link) {
        window.open(`https://twitter.com/intent/tweet?url=${encodeURIComponent(link)}`, "_blank");
    }
    function deleteChat(chatId) {
        // Gửi yêu cầu DELETE đến API
        fetch(`/chat/delete/${chatId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    console.log("Chat deleted successfully");
                    const chatElement = document.querySelector(`a[data-chat-id="${chatId}"]`);
                    chatElement.remove(); // Loại bỏ phần tử này khỏi DOM
                    chatBox.innerHTML = ''; //
                } else {
                    alert("Failed to delete chat");
                }
            })
            .catch(error => {
                console.error("Error deleting chat:", error);
            });
    }
    document.querySelectorAll(".delete-chat").forEach(button => {
        button.addEventListener("click", function() {
        const chatId = this.getAttribute("data-chat-id");
        deleteChat(chatId); // Gọi hàm xóa chat
        });
    });
    function showLoader() {
        const loader = document.createElement("div");
        loader.classList.add("loader-dots");
        for (let i = 0; i < 4; i++) {
            const dot = document.createElement("div");
            loader.appendChild(dot);
        }

        chatBox.appendChild(loader);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    function hideLoader() {
        const loader = document.querySelector(".loader-dots");
        if (loader) loader.remove();
    }
    function typeTextEffect(element, text, index = 0) {
        const tempDiv = document.createElement("div");
        tempDiv.innerHTML = text;
        const nodes = Array.from(tempDiv.childNodes); // Lấy tất cả phần tử (text và thẻ HTML)

        function typeNode(nodeIndex = 0) {
            if (nodeIndex >= nodes.length) return; // Dừng khi hết phần tử

            const node = nodes[nodeIndex]; // Lấy node hiện tại

            if (node.nodeType === Node.TEXT_NODE) {
                // Nếu là văn bản, gõ từng ký tự
                let charIndex = 0;
                function typeChar() {
                    if (charIndex < node.textContent.length) {
                        element.innerHTML += node.textContent[charIndex]; // Thêm từng ký tự
                        charIndex++;
                        setTimeout(typeChar, 10); // Điều chỉnh tốc độ (10ms)
                    } else {
                        typeNode(nodeIndex + 1); // Gõ xong node này thì sang node tiếp theo
                    }
                }
                typeChar();
            } else {
                // Nếu là thẻ HTML, thêm nguyên thẻ và chuyển sang node tiếp theo
                element.appendChild(node.cloneNode(true));
                setTimeout(() => typeNode(nodeIndex + 1), 10);
            }
        }

        typeNode();
    }

});
