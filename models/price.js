// models/Price.js
const mongoose = require("mongoose");

const priceSchema = new mongoose.Schema(
  {
    productId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: true,
      index: true
    },
    chain_name: {
      type: String,
      required: true,
      index: true
    },
    price: {
      type: Number,
      required: true
    },
    updatedAt: {
      type: Date,
      default: Date.now
    }
  },
  { timestamps: true }
);

// אינדקס משולב
priceSchema.index({ productId: 1, chain_name: 1 });

module.exports = mongoose.model("Price", priceSchema);
