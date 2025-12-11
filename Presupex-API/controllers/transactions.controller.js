import TransactionModel from "../models/transaction.model.js";
import supabase from "../config/supabase.js";

export const getAll = async (req, res) => {
    try {
        const userId = req.user.id;
        const data = await TransactionModel.getAll(userId);
        res.json(data);
    } catch (error) {
        console.error("Get all transactions error:", error);
        res.status(500).json({ error: "Failed to fetch transactions" });
    }
};

export const create = async (req, res) => {
    try {
        const userId = req.user.id;
        const { amount, category, type, description, date, image_base64 } = req.body;

        if (!amount || !category || !type || !date) {
            return res.status(400).json({ 
                error: "Missing required fields" 
            });
        }

        let image_url = null;

        if (image_base64) {
            const fileName = `${userId}/${Date.now()}.jpg`;

            const { error: uploadError } = await supabase.storage
                .from("transactions")
                .upload(fileName, Buffer.from(image_base64, "base64"), {
                    contentType: "image/jpeg",
                });

            if (uploadError) {
                console.error("Upload error:", uploadError);
                return res.status(400).json({ error: "Failed to upload image" });
            }

            const { data: publicUrl } = supabase.storage
                .from("transactions")
                .getPublicUrl(fileName);

            image_url = publicUrl.publicUrl;
        }

        const id = await TransactionModel.create({
            user_id: userId,
            amount,
            category,
            type,
            description,
            date,
            image_url
        });

        res.status(201).json({ 
            message: "Transaction created", 
            id 
        });

    } catch (error) {
        console.error("Create transaction error:", error);
        res.status(500).json({ error: "Failed to create transaction" });
    }
};

export const update = async (req, res) => {
    try {
        const userId = req.user.id;
        const { id } = req.params;
        const { amount, category, type, description, date, image_base64, image_url } = req.body;

        const existing = await TransactionModel.getById(id);
        if (!existing || existing.user_id !== userId) {
            return res.status(403).json({ 
                error: "Access denied" 
            });
        }

        let newImageUrl = image_url;

        if (image_base64) {
            const fileName = `${userId}/${Date.now()}.jpg`;

            await supabase.storage
                .from("transactions")
                .upload(fileName, Buffer.from(image_base64, "base64"), {
                    contentType: "image/jpeg",
                });

            const { data: publicUrl } = supabase.storage
                .from("transactions")
                .getPublicUrl(fileName);

            newImageUrl = publicUrl.publicUrl;
        }

        await TransactionModel.update(id, {
            amount,
            category,
            type,
            description,
            date,
            image_url: newImageUrl
        });

        res.json({ message: "Transaction updated" });

    } catch (error) {
        console.error("Update transaction error:", error);
        res.status(500).json({ error: "Failed to update transaction" });
    }
};

export const remove = async (req, res) => {
    try {
        const userId = req.user.id;
        const { id } = req.params;

        const existing = await TransactionModel.getById(id);
        if (!existing || existing.user_id !== userId) {
            return res.status(403).json({ 
                error: "Access denied" 
            });
        }

        await TransactionModel.delete(id);
        res.json({ message: "Transaction deleted" });

    } catch (error) {
        console.error("Delete transaction error:", error);
        res.status(500).json({ error: "Failed to delete transaction" });
    }
};

export const getTotals = async (req, res) => {
    try {
        const userId = req.user.id;
        const transactions = await TransactionModel.getAll(userId);

        const income = transactions
            .filter(t => t.type === 'income')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);

        const expenses = transactions
            .filter(t => t.type === 'expense')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);

        const balance = income - expenses;

        res.json({
            income,
            expenses,
            balance,
            count: transactions.length
        });

    } catch (error) {
        console.error("Get totals error:", error);
        res.status(500).json({ error: "Failed to calculate totals" });
    }
};