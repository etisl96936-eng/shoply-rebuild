// database/indexes.js
const User = require("../models/User");
const List = require("../models/List");
const Product = require("../models/Product");

// -------- USERS --------
User.collection.createIndex({ email: 1 }, { unique: true });
User.collection.createIndex({ phone: 1 }, { unique: true });

// -------- LISTS --------
List.collection.createIndex({ userId: 1 });
List.collection.createIndex({ createdAt: -1 });

// -------- PRODUCTS --------
Product.collection.createIndex({ name: "text" });
Product.collection.createIndex({ category: 1 });

console.log("Indexes created successfully");
