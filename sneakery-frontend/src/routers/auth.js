// routes/auth.js
import express from "express";
import jwt from "jsonwebtoken";
import nodemailer from "nodemailer";
import User from "../models/User.js";

const router = express.Router();

// ✅ Gửi email reset password
router.post("/forgot-password", async (req, res) => {
  const { email } = req.body;
  const user = await User.findOne({ email });

  if (!user) return res.status(404).json({ message: "Email không tồn tại!" });

  // Tạo token có hạn 15 phút
  const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
    expiresIn: "15m",
  });

  const resetLink = `${process.env.FRONTEND_URL}/reset-password/${token}`;

  // Gửi mail qua Gmail SMTP
  const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
      user: process.env.GMAIL_USER,
      pass: process.env.GMAIL_PASS,
    },
  });

  const mailOptions = {
    from: `"Sneakery Store" <${process.env.GMAIL_USER}>`,
    to: email,
    subject: "Đặt lại mật khẩu Sneakery Store",
    html: `
      <h2>Xin chào ${user.fullName || "bạn"},</h2>
      <p>Bạn vừa yêu cầu đặt lại mật khẩu. Nhấn vào nút bên dưới để đặt lại:</p>
      <a href="${resetLink}" style="
        background:#667eea;
        color:white;
        padding:10px 20px;
        border-radius:8px;
        text-decoration:none;
        display:inline-block;">Đặt lại mật khẩu</a>
      <p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
      <p>Liên kết này sẽ hết hạn sau 15 phút.</p>
    `,
  };

  try {
    await transporter.sendMail(mailOptions);
    res.json({ message: "Đã gửi email đặt lại mật khẩu!" });
  } catch (err) {
    res.status(500).json({ message: "Không thể gửi email." });
  }
});

router.post("/reset-password", async (req, res) => {
  const { token, password } = req.body;

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findById(decoded.id);
    if (!user) return res.status(400).json({ message: "Người dùng không tồn tại!" });

    user.password = password; // hash ở middleware hoặc model pre-save
    await user.save();

    res.json({ message: "Đặt lại mật khẩu thành công!" });
  } catch (error) {
    res.status(400).json({ message: "Token không hợp lệ hoặc đã hết hạn!" });
  }
});

export default router;