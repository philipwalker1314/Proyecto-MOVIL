import express from "express";
import { 
    register, 
    login, 
    logout, 
    getProfile, 
    updateProfile,
    verifyToken 
} from "../controllers/auth.controller.js";
import { authenticate } from "../middleware/auth.middleware.js";

const router = express.Router();

router.post("/register", register);
router.post("/login", login);
router.post("/verify", verifyToken);

router.post("/logout", authenticate, logout);
router.get("/profile", authenticate, getProfile);
router.put("/profile", authenticate, updateProfile);

export default router;