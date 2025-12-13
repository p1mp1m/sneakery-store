<template>
  <div class="chatbot-container">
    <!-- Chat Bubble Button -->
    <transition name="bounce">
      <button
        v-if="!isOpen"
        @click="toggleChat"
        class="chat-bubble"
        aria-label="Mở chat hỗ trợ"
      >
        <i class="material-icons">chat</i>
        <span class="pulse-dot"></span>
      </button>
    </transition>

    <!-- Chat Window -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chat-window" :class="{ 'dark': isDark }">
        <!-- Header -->
        <div class="chat-header">
          <div class="header-content">
            <div class="avatar">
              <i class="material-icons">support_agent</i>
            </div>
            <div class="header-text">
              <h3>Sneakery Support</h3>
              <p class="status">
                <span class="status-dot"></span>
                Đang online
              </p>
            </div>
          </div>
          <button @click="toggleChat" class="close-btn" aria-label="Đóng chat">
            <i class="material-icons">close</i>
          </button>
        </div>

        <!-- Messages Area -->
        <div class="chat-messages" ref="messagesContainer">
          <!-- Welcome Message -->
          <div class="message bot-message">
            <div class="message-avatar">
              <i class="material-icons">smart_toy</i>
            </div>
            <div class="message-content">
              <p>Xin chào! 👋 Tôi là trợ lý ảo của Sneakery Store.</p>
              <p>Tôi có thể giúp bạn:</p>
              <ul>
                <li>Tư vấn chọn size giày</li>
                <li>Hướng dẫn vệ sinh và bảo quản</li>
                <li>Tư vấn chọn giày theo nhu cầu</li>
                <li>Giải đáp thắc mắc về sản phẩm</li>
              </ul>
              <p>Bạn cần hỗ trợ gì ạ? 😊</p>
            </div>
          </div>

          <!-- Chat Messages -->
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="message"
            :class="message.role === 'user' ? 'user-message' : 'bot-message'"
          >
            <div v-if="message.role === 'assistant'" class="message-avatar">
              <i class="material-icons">smart_toy</i>
            </div>
            <div class="message-content">
              <p v-html="formatMessage(message.content)"></p>
              <span class="message-time">{{ message.timestamp }}</span>
            </div>
          </div>

          <!-- Typing Indicator -->
          <div v-if="isTyping" class="message bot-message">
            <div class="message-avatar">
              <i class="material-icons">smart_toy</i>
            </div>
            <div class="message-content typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="error-banner">
            <i class="material-icons">error_outline</i>
            <span>{{ errorMessage }}</span>
            <button @click="errorMessage = ''" class="error-close">
              <i class="material-icons">close</i>
            </button>
          </div>
        </div>

        <!-- Quick Suggestions -->
        <div v-if="showSuggestions && messages.length === 0" class="quick-suggestions">
          <button
            v-for="(suggestion, index) in suggestions"
            :key="index"
            @click="sendSuggestion(suggestion)"
            class="suggestion-btn"
          >
            {{ suggestion }}
          </button>
        </div>

        <!-- Input Area -->
        <div class="chat-input-container">
          <form @submit.prevent="sendMessage" class="chat-input-form">
            <input
              v-model="userInput"
              type="text"
              placeholder="Nhập tin nhắn của bạn..."
              class="chat-input"
              :disabled="isTyping"
              maxlength="500"
              ref="inputField"
            />
            <button
              type="submit"
              class="send-btn"
              :disabled="!userInput.trim() || isTyping"
              aria-label="Gửi tin nhắn"
            >
              <i class="material-icons">{{ isTyping ? 'hourglass_empty' : 'send' }}</i>
            </button>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, nextTick, computed, onMounted } from 'vue';
import chatbotService from '@/services/chatbotService';
import { useTheme } from '@/composables/useTheme';

export default {
  name: 'Chatbot',
  setup() {
    const isOpen = ref(false);
    const userInput = ref('');
    const messages = ref([]);
    const isTyping = ref(false);
    const errorMessage = ref('');
    const messagesContainer = ref(null);
    const inputField = ref(null);
    const showSuggestions = ref(true);
    
    const { isDark } = useTheme();

    const suggestions = [
      'Làm sao để chọn size giày phù hợp?',
      'Cách vệ sinh giày sneaker',
      'Tư vấn giày chạy bộ',
      'Chính sách đổi trả như thế nào?',
    ];

    const toggleChat = () => {
      isOpen.value = !isOpen.value;
      if (isOpen.value) {
        nextTick(() => {
          inputField.value?.focus();
        });
      }
    };

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
      });
    };

    const getCurrentTime = () => {
      const now = new Date();
      return now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    };

    const formatMessage = (text) => {
      // Convert line breaks to <br>
      let formatted = text.replace(/\n/g, '<br>');
      
      // Convert **bold** to <strong>
      formatted = formatted.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
      
      // Convert numbered lists
      formatted = formatted.replace(/(\d+)\.\s/g, '<br>$1. ');
      
      return formatted;
    };

    const sendMessage = async () => {
      const message = userInput.value.trim();
      if (!message || isTyping.value) return;

      // Hide suggestions after first message
      showSuggestions.value = false;
      
      // Add user message
      messages.value.push({
        role: 'user',
        content: message,
        timestamp: getCurrentTime(),
      });

      userInput.value = '';
      errorMessage.value = '';
      scrollToBottom();

      // Show typing indicator
      isTyping.value = true;

      try {
        // Prepare conversation history
        const conversationHistory = messages.value.map(msg => ({
          role: msg.role,
          content: msg.content,
        }));

        // Send to chatbot service
        const botReply = await chatbotService.sendMessage(conversationHistory);

        // Add bot response
        messages.value.push({
          role: 'assistant',
          content: botReply,
          timestamp: getCurrentTime(),
        });
      } catch (error) {
        console.error('Chatbot error:', error);
        errorMessage.value = error.message || 'Đã xảy ra lỗi. Vui lòng thử lại.';
        
        // Add fallback response
        const fallbackReply = chatbotService.getFallbackResponse(message);
        messages.value.push({
          role: 'assistant',
          content: fallbackReply,
          timestamp: getCurrentTime(),
        });
      } finally {
        isTyping.value = false;
        scrollToBottom();
        nextTick(() => {
          inputField.value?.focus();
        });
      }
    };

    const sendSuggestion = (suggestion) => {
      userInput.value = suggestion;
      sendMessage();
    };

    return {
      isOpen,
      userInput,
      messages,
      isTyping,
      errorMessage,
      messagesContainer,
      inputField,
      showSuggestions,
      suggestions,
      isDark,
      toggleChat,
      sendMessage,
      sendSuggestion,
      formatMessage,
    };
  },
};
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Chat Bubble */
.chat-bubble {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.chat-bubble:hover {
  transform: scale(1.1);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.6);
}

.chat-bubble .material-icons {
  font-size: 32px;
  color: white;
}

.pulse-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 12px;
  height: 12px;
  background: #10b981;
  border-radius: 50%;
  border: 2px solid white;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

/* Chat Window */
.chat-window {
  width: 400px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-window.dark {
  background: #1f2937;
}

/* Header */
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar .material-icons {
  font-size: 28px;
}

.header-text h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-text .status {
  margin: 4px 0 0 0;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0.9;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Messages Area */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f9fafb;
}

.chat-window.dark .chat-messages {
  background: #111827;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-avatar .material-icons {
  font-size: 20px;
  color: white;
}

.message-content {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
}

.bot-message .message-content {
  background: white;
  color: #1f2937;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.chat-window.dark .bot-message .message-content {
  background: #374151;
  color: #f3f4f6;
}

.user-message {
  flex-direction: row-reverse;
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
  margin-left: auto;
}

.message-content p {
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.message-content p:last-child {
  margin-bottom: 0;
}

.message-content ul {
  margin: 8px 0;
  padding-left: 20px;
}

.message-content li {
  margin: 4px 0;
}

.message-time {
  font-size: 11px;
  opacity: 0.6;
  margin-top: 4px;
  display: block;
}

/* Typing Indicator */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #9ca3af;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

/* Error Banner */
.error-banner {
  background: #fee2e2;
  color: #991b1b;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

.chat-window.dark .error-banner {
  background: #7f1d1d;
  color: #fecaca;
}

.error-close {
  background: none;
  border: none;
  cursor: pointer;
  color: inherit;
  margin-left: auto;
  padding: 0;
  display: flex;
}

/* Quick Suggestions */
.quick-suggestions {
  padding: 12px 20px;
  background: white;
  border-top: 1px solid #e5e7eb;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chat-window.dark .quick-suggestions {
  background: #1f2937;
  border-top-color: #374151;
}

.suggestion-btn {
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.chat-window.dark .suggestion-btn {
  background: #374151;
  border-color: #4b5563;
  color: #d1d5db;
}

.suggestion-btn:hover {
  background: #e5e7eb;
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
}

.chat-window.dark .suggestion-btn:hover {
  background: #4b5563;
}

/* Input Area */
.chat-input-container {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.chat-window.dark .chat-input-container {
  background: #1f2937;
  border-top-color: #374151;
}

.chat-input-form {
  display: flex;
  gap: 12px;
  align-items: center;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.chat-window.dark .chat-input {
  background: #374151;
  border-color: #4b5563;
  color: #f3f4f6;
}

.chat-input:focus {
  border-color: #667eea;
}

.chat-input::placeholder {
  color: #9ca3af;
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Animations */
.bounce-enter-active {
  animation: bounceIn 0.5s;
}

.bounce-leave-active {
  animation: bounceOut 0.3s;
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes bounceOut {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(0);
    opacity: 0;
  }
}

.slide-up-enter-active {
  animation: slideUp 0.3s ease-out;
}

.slide-up-leave-active {
  animation: slideDown 0.3s ease-in;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes slideDown {
  from {
    transform: translateY(0);
    opacity: 1;
  }
  to {
    transform: translateY(100%);
    opacity: 0;
  }
}

/* Scrollbar */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.chat-window.dark .chat-messages::-webkit-scrollbar-thumb {
  background: #4b5563;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* Responsive */
@media (max-width: 480px) {
  .chatbot-container {
    bottom: 16px;
    right: 16px;
  }

  .chat-window {
    width: calc(100vw - 32px);
    height: calc(100vh - 100px);
    max-height: 600px;
  }

  .chat-bubble {
    width: 56px;
    height: 56px;
  }

  .chat-bubble .material-icons {
    font-size: 28px;
  }
}
</style>
