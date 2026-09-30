db.orders.insertMany([
  {
    orderId: "ORD-101",
    customer: { name: "Eduardo", city: "Guadalajara" },
    status: "DELIVERED",
    paymentMethod: "CREDIT_CARD",
    items: [
      { name: "Mechanical Keyboard", category: "Hardware", price: 90, qty: 1 },
      { name: "USB-C DAC", category: "Audio", price: 25, qty: 2 }
    ],
    totalAmount: 140,
    orderDate: ISODate("2026-08-01T10:00:00Z")
  },
  {
    orderId: "ORD-102",
    customer: { name: "Ana", city: "Mexico City" },
    status: "DELIVERED",
    paymentMethod: "PAYPAL",
    items: [
      { name: "27-inch 4K Monitor", category: "Hardware", price: 350, qty: 1 }
    ],
    totalAmount: 350,
    orderDate: ISODate("2026-08-02T11:30:00Z")
  },
  {
    orderId: "ORD-103",
    customer: { name: "Carlos", city: "Guadalajara" },
    status: "CANCELLED",
    paymentMethod: "CREDIT_CARD",
    items: [
      { name: "Wireless Mouse", category: "Hardware", price: 45, qty: 1 }
    ],
    totalAmount: 45,
    orderDate: ISODate("2026-08-03T14:15:00Z")
  },
  {
    orderId: "ORD-104",
    customer: { name: "Eduardo", city: "Guadalajara" },
    status: "DELIVERED",
    paymentMethod: "DEBIT_CARD",
    items: [
      { name: "In-Ear Monitors", category: "Audio", price: 50, qty: 1 },
      { name: "Mousepad XL", category: "Accessories", price: 20, qty: 1 }
    ],
    totalAmount: 70,
    orderDate: ISODate("2026-08-05T09:00:00Z")
  },
  {
    orderId: "ORD-105",
    customer: { name: "Sofia", city: "Monterrey" },
    status: "PROCESSING",
    paymentMethod: "PAYPAL",
    items: [
      { name: "Mechanical Keyboard", category: "Hardware", price: 90, qty: 2 }
    ],
    totalAmount: 180,
    orderDate: ISODate("2026-08-06T16:45:00Z")
  },
  {
    orderId: "ORD-106",
    customer: { name: "Ana", city: "Mexico City" },
    status: "DELIVERED",
    paymentMethod: "CREDIT_CARD",
    items: [
      { name: "USB-C DAC", category: "Audio", price: 25, qty: 1 }
    ],
    totalAmount: 25,
    orderDate: ISODate("2026-08-07T18:20:00Z")
  }
]);

Challenge 1: Basic Array Unwinding
Goal: Deconstruct the items array in the orders collection so that each item in an order becomes its own separate document. 
Output the unwound documents.

db.orders.aggregate([
  { $unwind: "$items" },
  { 
    $project: {
      _id: 0,
      orderId: 1,
      itemName: "$items.name",Level 4: Using Polymorphism


Challenge 2: $unwind + $match
Goal: Unwind the items array, then filter the resulting documents to return only individual items where items.category is equal 
to "Electronics".


db.orders.aggregate([
  { $unwind: "$items"},

  { $match: { "items.category": "Audio" }},

  { 
    $project: {
      _id: 0,
      orderId: 1,
      itemName: "$items.name",
      itemCategory: "$items.category",
      itemPrice: "$items.price",
      itemQty: "$items.qty"
    }
  }
]);

Challenge 3: $unwind + $group (Aggregation per Array Item)
Goal: Unwind the items array, then group by the item's name (items.name) to calculate the total quantity sold for each distinct 
product. Name the total field totalQuantitySold.

db.orders.aggregate([
  { $unwind: "$items" },

  {
    $group: {
      _id: "$items.name",
      totalQuantitySold: { $sum: "$items.qty"}
    }
  }
]);

Challenge 4: $unwind + Arithmetic Calculation
Goal: Unwind the items array and project each item's name (itemName), quantity (qty), unit price (price), and calculate a new 
computed field itemSubtotal (items.qty * items.price). Exclude _id.

db.orders.aggregate([
  { $unwind: "$items"},

  {
    $project: {
      _id: 0,
      itemName: "$items.name",
      qty: "$items.qty",
      price: "$items.price",
      itemSubtotal: { $multiply: ["$items.qty", "$items.price"]}
    }
  }
]);

Challenge 5: Preserving Null / Empty Arrays
Goal: Unwind the items array, but ensure that any order that has an empty items array or null items field is not dropped from 
the results (include array index / preserve null and empty arrays).

db.orders.aggregate([
  { $unwind: { path: "$items", preserveNullAndEmptyArrays: true }},
]);

