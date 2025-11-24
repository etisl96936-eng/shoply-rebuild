const mongoose = require('mongoose');

const listSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  title:  { type: String, default: 'Shopping List' },
  
  products: [
    {
      productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product' },
      name:      { type: String, required: true },
      quantity:  { type: Number, default: 1 },
      purchased: { type: Boolean, default: false },
      prices: {
        shufersal: { type: Number },
        ramiLevy:  { type: Number },
        victory:   { type: Number }
      }
    }
  ],

  sharedWith: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  completed: { type: Boolean, default: false },
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('List', listSchema);
