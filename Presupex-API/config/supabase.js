import { createClient } from "@supabase/supabase-js";
import dotenv from "dotenv";

// Cargar variables de entorno
dotenv.config();

const supabaseUrl = process.env.SUPABASE_URL;
const supabaseKey = process.env.SUPABASE_KEY;

// Debug: ver qué está cargando
console.log("🔍 Loading Supabase config...");
console.log("URL:", supabaseUrl);
console.log("Key:", supabaseKey ? `${supabaseKey.substring(0, 20)}...` : "MISSING");

if (!supabaseUrl || !supabaseKey) {
    console.error("\n❌ ERROR: Missing Supabase credentials!");
    console.error("Please check your .env file has:");
    console.error("SUPABASE_URL=your_url");
    console.error("SUPABASE_KEY=your_key");
    process.exit(1);
}

const supabase = createClient(supabaseUrl, supabaseKey);

console.log("✅ Supabase client created successfully\n");

export default supabase;