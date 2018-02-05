package flytta.com.flytta;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import flytta.com.flytta.adapters.Database;
import flytta.com.flytta.model.ProductModel;

public class MainActivity extends AppCompatActivity {
    public String URL = "http://uat.winitsoftware.com/ThemeManager/Data/Products/Products.xml";
    Database db;
    FrameLayout lay_progress;
    ProductListAdapter mAdapter;
    List<ProductModel> productList;
    RecyclerView recycler_view;
    LinearLayoutManager layoutManager;
    String mResponse="";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public class WebService extends AsyncTask<String, Void, String> {
        private Context mContext;

        protected String doInBackground(String... params) {
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) new URL(MainActivity.this.URL).openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(500000);
                httpConnection.setReadTimeout(500000);
                Log.i("--Url--", "" + MainActivity.this.URL);
                httpConnection.connect();
                if (httpConnection.getResponseCode() == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = br.readLine();
                        if (line != null) {
                            sb.append(line + "\n");
                        } else {
                            br.close();
                            Log.i("--Response--", "" + sb.toString());
                            return sb.toString();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    if (!s.isEmpty()) {
                        MainActivity.this.parseData(s,0);
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    class C03601 implements Listener<String> {
        C03601() {
        }

        public void onResponse(String response) {
            try {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                MainActivity.this.parseData(response,0);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    class C03612 implements ErrorListener {
        C03612() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "Server Error" + error.getMessage(), Toast.LENGTH_LONG).show();
            MainActivity.this.lay_progress.setVisibility(View.GONE);
        }
    }

    class C03623 implements RetryPolicy {
        C03623() {
        }

        public int getCurrentTimeout() {
            return 50000;
        }

        public int getCurrentRetryCount() {
            return 50000;
        }

        public void retry(VolleyError error) throws VolleyError {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main1);
        this.db = new Database(this);
        productList=new ArrayList<>();
        //this.productList = this.db.getProducts();
        this.recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        this.lay_progress = (FrameLayout) findViewById(R.id.lay_progress);

        layoutManager=new LinearLayoutManager(MainActivity.this);
        this.recycler_view.setLayoutManager(layoutManager);
        this.recycler_view.setItemAnimator(new DefaultItemAnimator());
        this.mAdapter = new ProductListAdapter(this, this.productList);
        this.recycler_view.setAdapter(this.mAdapter);


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (productList.size() <= 500) {
                    productList.add(null);
                    mAdapter.notifyItemInserted(productList.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            productList.remove(productList.size() - 1);
                            mAdapter.notifyItemRemoved(productList.size());
                           try {
                                parseData(mResponse,productList.size());
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mAdapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (this.productList.size() <=0) {
            // this.lay_progress.setVisibility(0);
            fetchProducts();
        }
    }

    public void fetchProducts() {
        StringRequest req = new StringRequest(0, this.URL, new C03601(), new C03612());
        req.setRetryPolicy(new C03623());
        Volley.newRequestQueue(this).add(req);
    }


    private void parseData(String response,int index) throws XmlPullParserException, IOException {
        mResponse=response;
        int count=0;
        ProductModel pModel = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(response));
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 0) {
                System.out.println("Start document");
            } else if (eventType == 1) {
                System.out.println("End document");
            } else if (eventType == 2) {
                System.out.println("Start tag " + xpp.getName());
                String name = xpp.getName();
                if (name.equals("Product")) {
                    pModel = new ProductModel();
                } else if (name.equals("Name")) {
                    pModel.name = xpp.nextText();
                } else if (name.equals("Description")) {
                    pModel.desc = xpp.nextText();
                } else if (name.equals("Price")) {
                    pModel.cost = xpp.nextText();
                } else if (name.equals("ImageURL")) {
                    pModel.imgUrl = xpp.nextText();
                } else if (name.equals("BigImageURL")) {
                    pModel.bigUrl = xpp.nextText();
                }
            } else if (eventType == 3) {
                System.out.println("End tag " + xpp.getName());
                if (xpp.getName().equals("Product")) {
                    if(count>index)
                    productList.add(pModel);
                    if(count==index+50){
                        index=count;
                        count=0;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                        //break;

                    }
                    count++;
                }
            } else if (eventType == 4) {
                System.out.println("Text " + xpp.getText());
            }
        }
        this.db.inserProducts(productList);
        //this.mAdapter.updateData(this.productList);
        mAdapter.notifyDataSetChanged();

       // parseData(response,index);
        this.lay_progress.setVisibility(View.GONE);


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (productList.size() <= 500) {
                    productList.add(null);
                    mAdapter.notifyItemInserted(productList.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            productList.remove(productList.size() - 1);
                            mAdapter.notifyItemRemoved(productList.size());
                            try {
                                parseData(mResponse,productList.size());
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mAdapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void showImage(String url, Context context) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(1);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //builder.setOnDismissListener(this);
        ImageView img_view = new ImageView(context);
        builder.addContentView(img_view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(context).load(url).into(img_view);
        builder.show();
    }

    private void loadPhoto(String url, Context context) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setContentView(R.layout.custom_fullimage_dialog);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_fullimage_dialog, null);
        ImageView img_view = (ImageView) layout.findViewById(R.id.fullimage);
        ;Glide.with(context).load(url).into(img_view);
        dialog.setContentView(layout);
        dialog.show();

    }



    public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context mContext;
       // private List<ProductModel> mProductList;

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        private OnLoadMoreListener onLoadMoreListener;
        private boolean isLoading;


        public ProductListAdapter(Context context, List<ProductModel> productList) {
            this.mContext = context;
           // this.p = productList;
            if (recycler_view.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_view.getLayoutManager();
                recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            isLoading = true;
                        }
                    }
                });
            }
    }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView img_view;
            public TextView txt_cost;
            public TextView txt_desc;
            public TextView txt_name;

            public MyViewHolder(View view) {
                super(view);
                this.txt_name = (TextView) view.findViewById(R.id.txt_name);
                this.txt_cost = (TextView) view.findViewById(R.id.txt_cost);
                this.txt_desc = (TextView) view.findViewById(R.id.txt_desc);
                this.img_view = (ImageView) view.findViewById(R.id.img_product);

                img_view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        MainActivity.showImage(productList.get(getAdapterPosition()).bigUrl,mContext);
                    }
                });
            }
        }

        private class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return productList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM)
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
            else
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
        }

        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof MyViewHolder) {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                ProductModel product = (ProductModel) productList.get(position);
                myViewHolder.txt_name.setText(product.name);
                myViewHolder.txt_cost.setText(product.cost);
                myViewHolder.txt_desc.setText(product.desc);
                Glide.with(this.mContext).load(product.imgUrl).into(myViewHolder.img_view);
            }else{
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        public int getItemCount() {
            return productList.size();
        }

        public void setLoaded() {
            isLoading = false;
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
