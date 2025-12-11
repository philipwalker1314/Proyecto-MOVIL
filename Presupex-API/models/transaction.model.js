import supabase from "../config/supabase.js";

export default {
    async getAll(userId) {
        const { data, error } = await supabase
            .from("transactions")
            .select("*")
            .eq("user_id", userId)
            .order("date", { ascending: false });

        if (error) throw error;
        return data;
    },

    async getById(id) {
        const { data, error } = await supabase
            .from("transactions")
            .select("*")
            .eq("id", id)
            .single();

        if (error) throw error;
        return data;
    },

    async create(transaction) {
        const { data, error } = await supabase
            .from("transactions")
            .insert(transaction)
            .select("id")
            .single();

        if (error) throw error;
        return data.id;
    },

    async update(id, transaction) {
        const { error } = await supabase
            .from("transactions")
            .update(transaction)
            .eq("id", id);

        if (error) throw error;
    },

    async delete(id) {
        const { error } = await supabase
            .from("transactions")
            .delete()
            .eq("id", id);

        if (error) throw error;
    }
};