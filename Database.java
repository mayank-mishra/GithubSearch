package flytta.com.flytta.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import flytta.com.flytta.model.ProductModel;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_BIG_URL = "big_url";
    private static final String KEY_COST = "cost";
    private static final String KEY_DESC = "desce";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String TABLE_CONTACTS = "products";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY, name TEXT, cost TEXT, desce TEXT, url TEXT, big_url TEXT )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    public void inserProducts(List<ProductModel> products) {
        SQLiteDatabase db = getWritableDatabase();
        for (ProductModel product : products) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, product.name);
            values.put(KEY_COST, product.cost);
            values.put(KEY_DESC, product.desc);
            values.put(KEY_URL, product.imgUrl);
            values.put(KEY_BIG_URL, product.bigUrl);
            db.insert(TABLE_CONTACTS, null, values);
        }
        db.close();
    }

    public List<ProductModel> getProducts() {
        List<ProductModel> products = new ArrayList();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from products", null);
        if (cursor != null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToFirst();
                ProductModel p = new ProductModel();
                p.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                p.cost = cursor.getString(cursor.getColumnIndex(KEY_COST));
                p.desc = cursor.getString(cursor.getColumnIndex(KEY_DESC));
                p.imgUrl = cursor.getString(cursor.getColumnIndex(KEY_URL));
                p.bigUrl = cursor.getString(cursor.getColumnIndex(KEY_BIG_URL));
                products.add(p);
            }
        }
        return products;
    }
}
