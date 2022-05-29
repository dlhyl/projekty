// Lubomir Dlhy
import { Low, JSONFile } from "lowdb";

const db = new Low(new JSONFile("db.json"));
await db.read();

db.data = db.data || { users: [] };

await db.write();

export default db;
