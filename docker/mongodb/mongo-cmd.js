docker run -d \
	--name mongodb \
	-p 27017:27017 \
	-e GLIBC_TUNABLES="glibc.pthread.rseq=1" \
	-e MONGO_INITDB_ROOT_USERNAME=admin \
	-e MONGO_INITDB_ROOT_PASSWORD=secretpassword \
	-v mongo_data:/data/db \
	mongo:latest


docker exec -it mongodb mongosh -u admin -p secretpassword


// Insert

db.products.insertOne({
	name: "Mechanical Keyboard",
	brand: "Keychron",
	price: 89.99,
	inStock: true,
	tags: ["hardware", "peripherals"],
	specs: { switchType: "Red", wireless: true }
});


db.products.insertMany([
	{
    name: "USB-C DAC",
    brand: "KZ",
    price: 24.50,
    inStock: true,
    tags: ["audio", "accessories"]
  },
  {
    name: "27-inch 4K Monitor",
    brand: "Dell",
    price: 349.00,
    inStock: false,
    tags: ["displays", "hardware"]
  }
]);

// Read
// All documents in a Collection
db.products.find();

// Query with filter conditions
db.products.find({ brand: "KZ"});

// Find products with price greater than $50
db.products.find({ price: {$gt: 50}});

// update (modify documents)
// Update a field with $set
db.products.updateOne(
	{ name: "USB-C DAC"},
	{ $set: { price: 21.99 }}
);

curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Setup Docker Mongo",
    "description": "Run MongoDB container with GLIBC_TUNABLES env var",
    "status": "COMPLETED",
    "tags": ["docker", "mongodb", "java"]
  }'

curl "http://localhost:8080/api/tasks?status=COMPLETED"


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


// Filtering ($match)
db.orders.aggregate([
	{ $match: { status: "DELIVERED" }}
]);

// Projection ($project)
db.orders.aggregate([{
	$project: {
		_id: 0,
		orderId: 1,
		status: 1,
		totalAmount: 1
	}
}]);

// Sort and limit
db.orders.aggregate([
	{ $sort: { totalAmount: -1 }},
	{ $limit: 2 }
]);

// Simple Count ($count)
db.orders.aggregate([
	{ $match : { paymentMethod: "CREDIT_CARD"}},
	{ $count: "creditCardOrdersCount"}
]);


// basic grouping ($group)
db.orders.aggregate([
	{
		$group: {
			_id: "$status",
			totalOrders: { $sum: 1 }
		}
	}
]);

db.orders.aggregate([{
	$group: {
		_id: "$paymentMethod",
		totalOrders: { $sum: 1 }
	}
}]);

// Aggregating Amounts ($sum & $avg)
db.orders.aggregate([
	{ $match: { status: "DELIVERED" }},
	{
		$group: {
			_id: null,
			totalRevenue: { $sum: "$totalAmount" },
			averageOrderValue: { $avg: "$totalAmount" }
		}
	}
]);

// Grouping by Nested Fields
db.orders.aggregate([
	{
		$group: {
			_id: "$customer.city",
			cityRevenue: { $sum: "$totalRevenue" }
		}
	}
])

// Unwinding Arrays ($unwind)
db.orders.aggregate([
	{ $unwind: "$items"},
	{
		$project: {
			_id: 0,
			orderId: 1,
			itemName: "$items.name",
			itemPrice: "$items.price"
		}
	}
]);

// Grouping unwound data
db.orders.aggregate([
	{ $unwind: "$items"},
	{
		$group: {
			_id: "$items.category",
			totalUnitSold: { $sum: "$items.qty"}
		}
	}
]);

// Combining Stage ($match + $unwind + $group + $sort)
db.orders.aggregate([
	// filter
	{ $match: { status: "DELIVERED"}},

	// Unwind items array
	{ $unwind: "$items"},

	// Group by item name and compute revenue (price * qty)
	{
		$group : {
			_id: "$items.name",
			itemRevenue: { $sum: { $multiply: ["$items.price", "$items.qty"]}}
		}
	},

	{ $sort: { itemRevenue: -1 }}
]);


db.orders.aggregate([
	{ $match: { status: "DELIVERED" }},

	{ $unwind: "$items"},

	{
		$group: {
			_id: "$items.name",
			itemRevenue: { $sum: { $multiply: ["$items.price", "$items.qty"]}}
		}
	},

	{ $sort: { itemReveue: -1}}
]);


&6E59GBcq~m&)$V




Challenge 1: Exact Match on String Field
Goal: Retrieve all orders where the paymentMethod is explicitly "PAYPAL".

// Get Orders paid by paypal
db.orders.aggregate([
	{ $match: { paymentMethod: "PAYPAL"}}
]);

Challenge 2: Greater Than Comparison ($gt)
Goal: Find all orders where the totalAmount is strictly greater than 100.

db.orders.aggregate([
	{ $match : { totalAmount: { $gt: 100 }}}
]);

Challenge 3: Combined Conditions with AND ($and / implicit AND)
Goal: Find all orders that have a status of "DELIVERED" and were placed by a customer from the city of "Guadalajara".

db.orders.aggregate([
	{ 
		$match: { 
			status: "DELIVERED",
			"customer.city": "Guadalajara" 
		} 
	}
]);

Challenge 4: Matching Inside Nested Documents
Goal: Query the embedded document to return all orders placed by a customer whose name is "Ana".

db.orders.aggregate([
	{ $match: { "customer.name": "Ana"}}
]);

Challenge 5: Matching Elements Inside an Array of Objects
Goal: Retrieve all orders that contain at least one item belonging to the "Audio" category inside the items array.

db.orders.aggregate([
	{ $match: { "items.category": "Audio"}}
]);


Challenge 1: Basic Field Selection & Exclusion
Goal: Return all documents, but project only the orderId, status, and totalAmount fields. Exclude the default _id field.

db.orders.aggregate([
	{ 
		$project: {
			_id: 0,
			orderId: 1,
			status: 1,
			totalAmount: 1
		}
	}
])

Challenge 2: Renaming Fields
Goal: Return all documents, including only orderId and totalAmount, but rename totalAmount to orderTotal in the resulting output. (Exclude _id).

db.orders.aggregate([
	{
		$project: {
			_id: 0,
			orderId: 1,
			orderTotal: "$totalAmount"
		}
	}
]);


Challenge 3: Projecting Embedded Document Fields
Goal: Flatten the output by projecting orderId, the customer's name as customerName, and the customer's city as city. (Exclude _id).

db.orders.aggregate([
	{
		$project: {
			_id: 0,
			orderId: 1,
			customerName: "$customer.name",
			customerCity: "$customer.city"
		}
	}
]);

Challenge 4: Simple Arithmetic Computation
Goal: Project orderId and create a new field called discountedTotal that calculates a 10% discount on totalAmount (multiply totalAmount by 0.9). (Exclude _id).

db.orders.aggregate([
	{
		$project: {
			_id: 0,
			orderId: 1,
			discountedTotal: { $multiply: [ "$totalAmount", 0.90 ]} 
		}
	}
]);

Challenge 5: Array Length Expression
Goal: Project orderId and a new field called totalItems that calculates the total number of items in the items array using array size operators. (Exclude _id).


db.orders.aggregate([
	{
		$project: {
			_id: 0,
			orderId: 1,
			totalItems: { $size: "$items"}
		}
	}
]);


Challenge 1: Count Documents per Group
Goal: Group all orders by their status and calculate the total count of orders for each status. Name the count field totalOrders.

db.orders.aggregate([
	{
		$group: {
			_id: "$status",
			totalOrders: { $sum: 1 }
		}
	}
]);

Challenge 2: Summing Numeric Values
Goal: Group orders by paymentMethod and calculate the sum of totalAmount for each payment method. Name the accumulated total field grandTotal.

db.orders.aggregate([
{
	$group: {
		_id: "$paymentMethod",
		grandTotal: { $sum: "$totalAmount"}
	}
}
]);

Challenge 3: Average Calculation
Goal: Group orders by the customer's city (customer.city) and calculate the average totalAmount spent per city. Name the result field avgOrderValue.

db.orders.aggregate([
{
	$group: {
		_id: "$customer.city",
		avgOrderValue: { $avg: "$totalAmount" }
	}
}
]);

Challenge 4: Finding Max and Min Values
Goal: Group orders by status and find both the highest (maxTotal) and lowest (minTotal) totalAmount for each status within a single grouping stage.

db.orders.aggregate([{
	$group: {
		_id: "$status",
		maxTotal: { $max: "$totalAmount" },
		minTotal: { $min: "$totalAmount" }
	}
}]);

Challenge 5: Global Aggregation (Single Bucket)
Goal: Calculate the total overall revenue across all orders in the database by grouping with a null ID (_id: null). 
Name the output total overallRevenue.

db.orders.aggregate([{
	$group: {
		_id: null,
		overallRevenue: { $sum: "$totalAmount" }
	}
}]);


Challenge 1: Basic Array Unwinding
Goal: Deconstruct the items array in the orders collection so that each item in an order becomes its own separate document. 
Output the unwound documents.

db.orders.aggregate([{
	
}])

Challenge 2: $unwind + $match
Goal: Unwind the items array, then filter the resulting documents to return only individual items where items.category is equal 
to "Electronics".

Challenge 3: $unwind + $group (Aggregation per Array Item)
Goal: Unwind the items array, then group by the item's name (items.name) to calculate the total quantity sold for each distinct 
product. Name the total field totalQuantitySold.

Challenge 4: $unwind + Arithmetic Calculation
Goal: Unwind the items array and project each item's name (itemName), quantity (qty), unit price (price), and calculate a new 
computed field itemSubtotal (items.qty * items.price). Exclude _id.

Challenge 5: Preserving Null / Empty Arrays
Goal: Unwind the items array, but ensure that any order that has an empty items array or null items field is not dropped from 
the results (include array index / preserve null and empty arrays).



























