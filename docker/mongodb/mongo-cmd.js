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