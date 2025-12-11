import dotenv from "dotenv";
dotenv.config(); // ← DEBE ESTAR AQUÍ ANTES DE TODO

import express from "express";
import cors from "cors";
import authRoutes from "./routes/auth.routes.js";
import transactionRoutes from "./routes/transactions.routes.js";

const app = express();

app.use(cors());
app.use(express.json({ limit: "10mb" }));

app.use("/api/auth", authRoutes);
app.use("/api/transactions", transactionRoutes);

app.get("/", (req, res) => {
    res.json({ 
        message: "Presupex API is running",
        version: "1.0.0"
    });
});

app.use((req, res) => {
    res.status(404).json({ error: "Route not found" });
});

app.use((err, req, res, next) => {
    console.error("Server error:", err);
    res.status(500).json({ error: "Internal server error" });
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`✅ API running on http://localhost:${PORT}`);
    console.log(`📝 Auth endpoints: http://localhost:${PORT}/api/auth`);
    console.log(`💰 Transactions endpoints: http://localhost:${PORT}/api/transactions`);
});