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

Phase 1: Filtering & Grouping Combinations
Challenge 1: Revenue by City
Goal: Group orders by customer city (customer.city) to calculate the total revenue generated in each city (totalCityRevenue).

db.orders.aggregate([
	{
		$group: {
			_id: "$customer.city",
			totalCityRevenue: { $sum: "$totalAmount" }
		}
	}
]);


Challenge 2: Category Breakdown via Unwind
Goal: Unwind the items array, filter for items where items.category is "Electronics", and group by item name (items.name) to 
find the total quantity sold for each electronic product.

db.orders.aggregate([
	{ $unwind: "$items"},
	{ $match: { "items.category": "Electronics"}},
	{ 
		$group: {
			_id: "$items.name",
			totalQuantitySold: { $sum: "$items.qty" }
		}
	}
]);


db.orders.aggregate([
	{ $unwind: "$items"},
	{ $match: { "items.category": "Audio"}},
	{ 
		$group: {
			_id: "$items.name",
			totalQuantitySold: { $sum: "$items.qty" }
		}
	}
]);

Challenge 3: Status-Filtered Order Projections
Goal: Filter orders to include only those with a status of "COMPLETED", unwind their items, and project orderId, items.name, 
and a calculated itemSubtotal (items.qty * items.price), excluding _id.

db.orders.aggregate([
	{ $match: { "status": "COMPLETED"}},
	{ $unwind: "$items"},

	{
		$project: {
			_id: 0,
			orderId: 1,
			"items.name": 1,
			itemSubtotal: { $multiply: ["$items.qty", "$items.price"]}
		}
	}
]);


db.orders.aggregate([
	{ $match: { status: "DELIVERED"}},
	{ $unwind: "$items"},

	{
		$project: {
			_id: 0,
			orderId: 1,
			itemsName: "$items.name",
			itemSubtotal: { $multiply: ["$items.qty", "$items.price"]}
		}
	}
]);

Phase 2: Analytics & Sorting Combinations
Challenge 4: Top Revenue Categories

Goal: Unwind the items array, group by item category (items.category) to calculate total revenue per category (categoryRevenue), 
and sort the results in descending order.

Challenge 5: Top Spending Customers

Goal: Group orders by customer name (customer.name) to calculate their lifetime spend (totalSpent), and return the top 3 highest-spending customers sorted descending.

Challenge 6: High-Value Item Filter & Average

Goal: Unwind items, filter for individual items with a unit price greater than $100, group by category, and calculate the average price of those items per category.

Phase 3: Advanced Multi-Stage Pipelines
Challenge 7: Bulk Order Item Analysis

Goal: Unwind items, filter out any item where qty is less than 2, group by item name to calculate total quantity sold, and sort ascending by that total quantity.

Challenge 8: Order Summary with Item Counts

Goal: Unwind items, group by orderId to count how many distinct items are in each order (itemCount) alongside the order's total amount, then sort by item count descending.

Challenge 9: Best-Selling Products Leaderboard

Goal: Unwind items, group by item name to calculate total units sold (totalSold), sort descending, and use $limit to return only the single best-selling product across the entire collection.

Challenge 10: The Ultimate Multi-Stage Pipeline

Goal: Filter orders to exclude "CANCELLED" status, unwind the items array, group by category to find total revenue generated per category, sort descending, and return only the top 2 revenue-generating categories.