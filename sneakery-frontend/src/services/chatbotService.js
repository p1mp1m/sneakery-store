import axios from 'axios';
import logger from '@/utils/logger';

// OpenRouter API configuration
const OPENROUTER_API_URL = 'https://openrouter.ai/api/v1/chat/completions';
const OPENROUTER_API_KEY = import.meta.env.VITE_OPENROUTER_API_KEY || '';
const MODEL = 'openai/gpt-3.5-turbo';

// 🔧 Tạo axios instance riêng cho chatbot (không có interceptor)
// Điều này ngăn việc axios interceptor toàn cục xử lý lỗi 401 từ OpenRouter
const chatbotAxios = axios.create();

// System prompt cho chatbot
const SYSTEM_PROMPT = `Bạn là một chatbot hỗ trợ khách hàng cho website bán giày thể thao và giày thời trang Sneakery Store.

Nhiệm vụ của bạn:

1. Trả lời thân thiện, ngắn gọn, dễ hiểu bằng tiếng Việt.
2. Giải thích kiến thức chung về giày: size, chất liệu, cách vệ sinh, chọn giày theo nhu cầu.
3. Không tự bịa ra giá bán hoặc tồn kho. Nếu được hỏi, hãy trả lời:
   "Hiện tại tôi không có dữ liệu giá hoặc tồn kho, bạn vui lòng xem trực tiếp trên trang sản phẩm hoặc liên hệ hotline để được tư vấn chi tiết."
4. Không khẳng định thông tin kỹ thuật phức tạp nếu không chắc.
5. Tư vấn các loại giày phù hợp:
   - Giày đi học: Sneaker cổ thấp, thoải mái, dễ phối đồ
   - Giày chạy bộ: Có đệm tốt, hỗ trợ bàn chân, độ bám cao
   - Giày đi làm: Thiết kế lịch sự, màu trung tính
   - Giày casual đi chơi: Năng động, thời trang, thoải mái
6. Khi khách hỏi về các thương hiệu, hãy tư vấn dựa trên uy tín: Nike (thể thao), Adidas (đa năng), Puma (thời trang thể thao), Converse (casual), Vans (skateboarding/lifestyle).
7. Khi không liên quan đến giày, bạn vẫn trả lời lịch sự nhưng giữ giọng chatbot hỗ trợ khách hàng.
8. Không đề cập đến việc bạn là AI hoặc mô hình ngôn ngữ.

Mục tiêu: Hỗ trợ khách nhanh chóng, đúng trọng tâm, thân thiện như nhân viên tư vấn online của Sneakery Store.`;

class ChatbotService {
    /**
     * Gửi tin nhắn đến OpenRouter API
     * @param {Array} messages - Mảng các tin nhắn chat (lịch sử hội thoại)
     * @returns {Promise<string>} - Phản hồi từ chatbot
     */
    async sendMessage(messages) {
        logger.log('ChatbotService - Sending message to OpenRouter');
        
        try {
            // Chuẩn bị messages với system prompt
            const fullMessages = [
                { role: 'system', content: SYSTEM_PROMPT },
                ...messages
            ];

            const response = await chatbotAxios.post(
                OPENROUTER_API_URL,
                {
                    model: MODEL,
                    messages: fullMessages,
                },
                {
                    headers: {
                        'Authorization': `Bearer ${OPENROUTER_API_KEY}`,
                        'Content-Type': 'application/json',
                        'HTTP-Referer': window.location.origin,
                        'X-Title': 'Sneakery Store Chatbot',
                    }
                }
            );

            logger.log('ChatbotService - Raw API response:', response.data);
            const botReply = response.data.choices?.[0]?.message?.content;
            logger.log('ChatbotService - botReply:', botReply);
            
            return botReply;
        } catch (error) {
            logger.error('ChatbotService - Error:', error.response?.data || error.message);
            
            // Xử lý lỗi thân thiện
            if (error.response?.status === 401) {
                throw new Error('Lỗi xác thực API. Vui lòng kiểm tra lại cấu hình.');
            } else if (error.response?.status === 429) {
                throw new Error('Quá nhiều yêu cầu. Vui lòng thử lại sau ít phút.');
            } else if (!navigator.onLine) {
                throw new Error('Không có kết nối internet. Vui lòng kiểm tra kết nối của bạn.');
            } else {
                throw new Error('Xin lỗi, tôi đang gặp sự cố. Vui lòng thử lại sau.');
            }
        }
    }

    /**
     * Lấy câu trả lời mẫu khi không kết nối được API
     * @param {string} userMessage - Tin nhắn của người dùng
     * @returns {string} - Câu trả lời mẫu
     */
    getFallbackResponse(userMessage) {
        const lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.includes('size') || lowerMessage.includes('cỡ')) {
            return 'Để chọn size giày chính xác, bạn nên đo chiều dài bàn chân (từ gót đến đầu ngón chân dài nhất) và tham khảo bảng size của từng thương hiệu. Mỗi hãng có bảng size khác nhau một chút. Bạn có thể xem bảng size chi tiết trên trang sản phẩm hoặc liên hệ để được tư vấn!';
        } else if (lowerMessage.includes('vệ sinh') || lowerMessage.includes('giặt') || lowerMessage.includes('làm sạch')) {
            return 'Để vệ sinh giày, bạn nên: 1) Dùng bàn chải mềm và nước ấm pha chút xà phòng nhẹ, 2) Không ngâm giày lâu, 3) Lau nhẹ nhàng và để khô tự nhiên, tránh phơi trực tiếp dưới nắng. Với giày da, nên dùng dung dịch chuyên dụng.';
        } else if (lowerMessage.includes('đổi') || lowerMessage.includes('trả') || lowerMessage.includes('hoàn')) {
            return 'Sneakery Store hỗ trợ đổi trả trong vòng 30 ngày với sản phẩm còn nguyên tem, hộp và chưa qua sử dụng. Vui lòng liên hệ hotline hoặc xem chính sách đổi trả chi tiết trên website!';
        } else if (lowerMessage.includes('ship') || lowerMessage.includes('giao') || lowerMessage.includes('vận chuyển')) {
            return 'Chúng tôi có chính sách miễn phí vận chuyển cho đơn hàng trên 500.000đ. Thời gian giao hàng thường từ 2-5 ngày tùy khu vực. Bạn sẽ được cập nhật mã vận đơn qua email/SMS!';
        } else {
            return 'Xin chào! Tôi là trợ lý ảo của Sneakery Store. Tôi có thể giúp bạn tư vấn về size giày, chất liệu, cách vệ sinh, và các loại giày phù hợp với nhu cầu của bạn. Bạn cần hỗ trợ gì ạ?';
        }
    }
}

export default new ChatbotService();
