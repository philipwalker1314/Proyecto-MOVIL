import express from "express";
import cors from "cors";
import transactionRoutes from "./routes/transactions.routes.js";

const app = express();
app.use(cors());
app.use(express.json());
app.use("/uploads", express.static("uploads"));

app.use("/api/transactions", transactionRoutes);

app.listen(3000, () => {
    console.log("API running on http://localhost:3000");
});
