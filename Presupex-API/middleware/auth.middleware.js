import supabase from "../config/supabase.js";

export const authenticate = async (req, res, next) => {
    try {
        console.log("🔐 Auth Middleware - Headers:", req.headers.authorization);
        
        // Obtener token del header Authorization
        const authHeader = req.headers.authorization;
        
        if (!authHeader) {
            console.log("❌ No authorization header");
            return res.status(401).json({ error: "No token provided" });
        }

        const token = authHeader.replace('Bearer ', '');
        
        console.log("🎫 Token extracted:", token.substring(0, 20) + "...");

        // Verificar token con Supabase
        const { data: { user }, error } = await supabase.auth.getUser(token);

        console.log("👤 User from token:", user ? user.id : "null");
        console.log("❌ Error:", error ? error.message : "none");

        if (error || !user) {
            console.log("❌ Invalid token");
            return res.status(401).json({ error: "Invalid or expired token" });
        }

        // Adjuntar usuario a la request
        req.user = user;
        req.token = token;

        console.log("✅ Auth successful for user:", user.id);
        next();

    } catch (error) {
        console.error("💥 Auth middleware error:", error);
        res.status(500).json({ error: "Authentication failed" });
    }
};