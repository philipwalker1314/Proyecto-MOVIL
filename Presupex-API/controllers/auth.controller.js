import supabase from "../config/supabase.js";

export const register = async (req, res) => {
    try {
        const { email, password, full_name } = req.body;

        if (!email || !password) {
            return res.status(400).json({ 
                error: "Email and password are required" 
            });
        }

        if (password.length < 6) {
            return res.status(400).json({ 
                error: "Password must be at least 6 characters" 
            });
        }

        const { data, error } = await supabase.auth.signUp({
            email,
            password,
            options: {
                data: {
                    full_name: full_name || email.split('@')[0]
                }
            }
        });

        if (error) {
            return res.status(400).json({ error: error.message });
        }

        res.status(201).json({
            message: "User registered successfully",
            user: {
                id: data.user.id,
                email: data.user.email,
                full_name: data.user.user_metadata.full_name
            },
            session: data.session
        });

    } catch (error) {
        console.error("Register error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};

export const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ 
                error: "Email and password are required" 
            });
        }

        const { data, error } = await supabase.auth.signInWithPassword({
            email,
            password
        });

        if (error) {
            return res.status(401).json({ error: error.message });
        }

        res.json({
            message: "Login successful",
            user: {
                id: data.user.id,
                email: data.user.email,
                full_name: data.user.user_metadata.full_name
            },
            session: data.session
        });

    } catch (error) {
        console.error("Login error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};

export const logout = async (req, res) => {
    try {
        const { error } = await supabase.auth.signOut();

        if (error) {
            return res.status(400).json({ error: error.message });
        }

        res.json({ message: "Logout successful" });

    } catch (error) {
        console.error("Logout error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};

export const getProfile = async (req, res) => {
    try {
        const userId = req.user.id;

        const { data, error } = await supabase
            .from("users")
            .select("*")
            .eq("id", userId)
            .single();

        if (error) {
            return res.status(404).json({ error: "User not found" });
        }

        res.json(data);

    } catch (error) {
        console.error("Get profile error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};

export const updateProfile = async (req, res) => {
    try {
        const userId = req.user.id;
        const { full_name } = req.body;

        const { error } = await supabase
            .from("users")
            .update({ full_name })
            .eq("id", userId);

        if (error) {
            return res.status(400).json({ error: error.message });
        }

        res.json({ message: "Profile updated successfully" });

    } catch (error) {
        console.error("Update profile error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};

export const verifyToken = async (req, res) => {
    try {
        const token = req.headers.authorization?.replace('Bearer ', '');

        if (!token) {
            return res.status(401).json({ error: "No token provided" });
        }

        const { data: { user }, error } = await supabase.auth.getUser(token);

        if (error || !user) {
            return res.status(401).json({ error: "Invalid token" });
        }

        res.json({
            valid: true,
            user: {
                id: user.id,
                email: user.email,
                full_name: user.user_metadata.full_name
            }
        });

    } catch (error) {
        console.error("Verify token error:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};