import db from "../config/db.js";
import TransactionModel from "../models/transaction.model.js";

export const getAll = async (req, res) => {
    const data = await TransactionModel.getAll(db);
    res.json(data);
};

export const create = async (req, res) => {
    const { amount, category, type, description, date } = req.body;
    const image_url = req.file ? "/uploads/" + req.file.filename : null;

    const id = await TransactionModel.create(db, {
        amount,
        category,
        type,
        description,
        date,
        image_url
    });

    res.json({ message: "Transaction created", id });
};

export const update = async (req, res) => {
    const { id } = req.params;
    const { amount, category, type, description, date } = req.body;
    const image_url = req.file ? "/uploads/" + req.file.filename : req.body.image_url;

    await TransactionModel.update(db, id, {
        amount,
        category,
        type,
        description,
        date,
        image_url
    });

    res.json({ message: "Transaction updated" });
};

export const remove = async (req, res) => {
    await TransactionModel.delete(db, req.params.id);
    res.json({ message: "Transaction deleted" });
};
