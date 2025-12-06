export default {
    async getAll(db) {
        const [rows] = await db.query("SELECT * FROM transactions");
        return rows;
    },

    async create(db, t) {
        const [result] = await db.query(
            `INSERT INTO transactions(amount, category, type, description, date, image_url)
             VALUES (?, ?, ?, ?, ?, ?)`,
            [t.amount, t.category, t.type, t.description, t.date, t.image_url]
        );
        return result.insertId;
    },

    async update(db, id, t) {
        await db.query(
            `UPDATE transactions SET amount=?, category=?, type=?, description=?, date=?, image_url=?
             WHERE id=?`,
            [t.amount, t.category, t.type, t.description, t.date, t.image_url, id]
        );
    },

    async delete(db, id) {
        await db.query("DELETE FROM transactions WHERE id=?", [id]);
    }
};
