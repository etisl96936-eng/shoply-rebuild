// models/Report.js
const mongoose = require("mongoose");

const reportSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
      index: true
    },

    selectedSupermarket: {
      type: String,
      default: null
    },

    expensesByCategory: [
      {
        category: String,
        total: Number
      }
    ],

    totalByList: [
      {
        listId: { type: mongoose.Schema.Types.ObjectId, ref: "List" },
        total: Number
      }
    ],

    dateRange: {
      from: Date,
      to: Date
    }
  },
  { timestamps: true }
);

reportSchema.index({ userId: 1, "dateRange.from": 1, "dateRange.to": 1 });

module.exports = mongoose.model("Report", reportSchema);
