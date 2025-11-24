require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');

const app = express();

// Load database indexes (optimization)
require('./database/indexes');   // <-- added here

// Connect to MongoDB
mongoose.connect(process.env.MONGO_URI)
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log('MongoDB connection error:', err));

// Basic test route
app.get('/', (req, res) => {
  res.send('Hello World!');
});

// Start the server
app.listen(process.env.PORT, () => {
  console.log(`Server running on port ${process.env.PORT}`);
});
זה