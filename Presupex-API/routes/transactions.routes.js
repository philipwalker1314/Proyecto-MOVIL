import express from "express";
import { getAll, create, update, remove, getTotals } from "../controllers/transactions.controller.js";
import { authenticate } from "../middleware/auth.middleware.js";

const router = express.Router();

// ⚠️ IMPORTANTE: El middleware debe aplicarse a cada ruta individual
// NO usar router.use(authenticate) porque no funciona bien con todos los métodos

router.get("/", authenticate, getAll);
router.get("/totals", authenticate, getTotals);
router.post("/", authenticate, create);
router.put("/:id", authenticate, update);
router.delete("/:id", authenticate, remove);

export default router;