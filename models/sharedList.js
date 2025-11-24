// models/SharedList.js
const mongoose = require("mongoose");

const sharedListSchema = new mongoose.Schema(
  {
    listId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "List",
      required: true,
      index: true
    },
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true
    },
    permission: {
      type: String,
      enum: ["edit", "view"],
      default: "view"
    },
    addedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true
    }
  },
  { timestamps: true }
);

sharedListSchema.index({ listId: 1, userId: 1 });

module.exports = mongoose.model("SharedList", sharedListSchema);
