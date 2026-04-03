package com.example.project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FlyDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "fly.db"
        private const val DATABASE_VERSION = 2  // bumped from 1 → 2 to fix image mismatch

        const val TABLE_PRODUCTS = "products"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PRICE = "price"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_IMAGE_RES = "image_res"
        const val COLUMN_RATING = "rating"
        const val COLUMN_IS_POPULAR = "is_popular"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_IMAGE_RES INTEGER,
                $COLUMN_RATING REAL,
                $COLUMN_IS_POPULAR INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
        insertDummyData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    private fun insertDummyData(db: SQLiteDatabase) {
        val products = listOf(

            // 🍔 Burgers
            mapOf(COLUMN_NAME to "Classic Smash Burger",
                COLUMN_DESCRIPTION to "Double smashed beef patty, cheddar, pickles, special sauce",
                COLUMN_PRICE to 8.99, COLUMN_CATEGORY to "Burgers",
                COLUMN_IMAGE_RES to R.drawable.img_smash_burger,
                COLUMN_RATING to 4.8, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "BBQ Bacon Burger",
                COLUMN_DESCRIPTION to "Crispy bacon, smoky BBQ sauce, onion rings, lettuce",
                COLUMN_PRICE to 10.49, COLUMN_CATEGORY to "Burgers",
                COLUMN_IMAGE_RES to R.drawable.img_bbq_bacon_burger,
                COLUMN_RATING to 4.6, COLUMN_IS_POPULAR to 0),

            mapOf(COLUMN_NAME to "Mushroom Swiss Burger",
                COLUMN_DESCRIPTION to "Sautéed mushrooms, Swiss cheese, garlic aioli",
                COLUMN_PRICE to 9.99, COLUMN_CATEGORY to "Burgers",
                COLUMN_IMAGE_RES to R.drawable.img_mushroom_swiss_burger,
                COLUMN_RATING to 4.5, COLUMN_IS_POPULAR to 0),

            // 🍕 Pizza
            mapOf(COLUMN_NAME to "Margherita Pizza",
                COLUMN_DESCRIPTION to "San Marzano tomato, fresh mozzarella, basil, olive oil",
                COLUMN_PRICE to 11.99, COLUMN_CATEGORY to "Pizza",
                COLUMN_IMAGE_RES to R.drawable.img_margherita_pizza,
                COLUMN_RATING to 4.9, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "Pepperoni Feast",
                COLUMN_DESCRIPTION to "Double pepperoni, mozzarella, tomato sauce, oregano",
                COLUMN_PRICE to 13.49, COLUMN_CATEGORY to "Pizza",
                COLUMN_IMAGE_RES to R.drawable.img_pepperoni_pizza,
                COLUMN_RATING to 4.7, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "BBQ Chicken Pizza",
                COLUMN_DESCRIPTION to "Grilled chicken, BBQ sauce, red onions, cilantro",
                COLUMN_PRICE to 13.99, COLUMN_CATEGORY to "Pizza",
                COLUMN_IMAGE_RES to R.drawable.img_bbq_chicken_pizza,
                COLUMN_RATING to 4.5, COLUMN_IS_POPULAR to 0),

            // 🌮 Wraps
            mapOf(COLUMN_NAME to "Grilled Chicken Wrap",
                COLUMN_DESCRIPTION to "Grilled chicken strips, lettuce, tomato, garlic sauce, tortilla",
                COLUMN_PRICE to 7.49, COLUMN_CATEGORY to "Wraps",
                COLUMN_IMAGE_RES to R.drawable.img_chicken_wrap,
                COLUMN_RATING to 4.6, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "Falafel Wrap",
                COLUMN_DESCRIPTION to "Crispy falafel, hummus, pickled vegetables, tahini",
                COLUMN_PRICE to 6.99, COLUMN_CATEGORY to "Wraps",
                COLUMN_IMAGE_RES to R.drawable.img_falafel_wrap,
                COLUMN_RATING to 4.4, COLUMN_IS_POPULAR to 0),

            // 🍣 Sushi
            mapOf(COLUMN_NAME to "Salmon Avocado Roll",
                COLUMN_DESCRIPTION to "Fresh salmon, creamy avocado, cucumber, sesame seeds",
                COLUMN_PRICE to 14.99, COLUMN_CATEGORY to "Sushi",
                COLUMN_IMAGE_RES to R.drawable.img_salmon_roll,
                COLUMN_RATING to 4.9, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "Spicy Tuna Roll",
                COLUMN_DESCRIPTION to "Tuna, spicy mayo, cucumber, tobiko",
                COLUMN_PRICE to 13.99, COLUMN_CATEGORY to "Sushi",
                COLUMN_IMAGE_RES to R.drawable.img_tuna_roll,
                COLUMN_RATING to 4.7, COLUMN_IS_POPULAR to 0),

            // 🥤 Drinks
            mapOf(COLUMN_NAME to "Mango Lemonade",
                COLUMN_DESCRIPTION to "Fresh mango, lemon juice, mint, sparkling water",
                COLUMN_PRICE to 3.99, COLUMN_CATEGORY to "Drinks",
                COLUMN_IMAGE_RES to R.drawable.img_mango_lemonade,
                COLUMN_RATING to 4.8, COLUMN_IS_POPULAR to 1),

            mapOf(COLUMN_NAME to "Classic Milkshake",
                COLUMN_DESCRIPTION to "Vanilla ice cream, whole milk, your choice of flavor",
                COLUMN_PRICE to 4.99, COLUMN_CATEGORY to "Drinks",
                COLUMN_IMAGE_RES to R.drawable.img_milkshake,
                COLUMN_RATING to 4.6, COLUMN_IS_POPULAR to 0)
        )

        for (product in products) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, product[COLUMN_NAME] as String)
                put(COLUMN_DESCRIPTION, product[COLUMN_DESCRIPTION] as String)
                put(COLUMN_PRICE, product[COLUMN_PRICE] as Double)
                put(COLUMN_CATEGORY, product[COLUMN_CATEGORY] as String)
                put(COLUMN_IMAGE_RES, product[COLUMN_IMAGE_RES] as Int)
                put(COLUMN_RATING, product[COLUMN_RATING] as Double)
                put(COLUMN_IS_POPULAR, product[COLUMN_IS_POPULAR] as Int)
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null)
        while (cursor.moveToNext()) { list.add(cursorToProduct(cursor)) }
        cursor.close()
        return list
    }

    fun getProductsByCategory(category: String): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null,
            "$COLUMN_CATEGORY = ?", arrayOf(category), null, null, null)
        while (cursor.moveToNext()) { list.add(cursorToProduct(cursor)) }
        cursor.close()
        return list
    }

    fun getPopularProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null,
            "$COLUMN_IS_POPULAR = ?", arrayOf("1"), null, null, null)
        while (cursor.moveToNext()) { list.add(cursorToProduct(cursor)) }
        cursor.close()
        return list
    }

    fun getProductById(id: Int): Product? {
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null,
            "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, null)
        val product = if (cursor.moveToFirst()) cursorToProduct(cursor) else null
        cursor.close()
        return product
    }

    fun getCategories(): List<String> {
        val list = mutableListOf("All")
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_CATEGORY FROM $TABLE_PRODUCTS", null)
        while (cursor.moveToNext()) { list.add(cursor.getString(0)) }
        cursor.close()
        return list
    }

    private fun cursorToProduct(cursor: android.database.Cursor): Product {
        return Product(
            id          = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name        = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
            price       = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
            category    = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
            imageRes    = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES)),
            rating      = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)),
            isPopular   = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_POPULAR)) == 1
        )
    }
}