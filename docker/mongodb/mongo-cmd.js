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






















